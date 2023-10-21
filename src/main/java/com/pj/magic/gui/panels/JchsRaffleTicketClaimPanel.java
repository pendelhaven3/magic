package com.pj.magic.gui.panels;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.pj.magic.Constants;
import com.pj.magic.excel.JchsRaffleTicketClaimExcelGenerator;
import com.pj.magic.exception.AlreadyClaimedException;
import com.pj.magic.gui.component.DatePickerFormatter;
import com.pj.magic.gui.component.EllipsisButton;
import com.pj.magic.gui.component.MagicFileChooser;
import com.pj.magic.gui.component.MagicTextField;
import com.pj.magic.gui.component.MagicToolBar;
import com.pj.magic.gui.component.MagicToolBarButton;
import com.pj.magic.gui.dialog.SelectCustomerDialog;
import com.pj.magic.gui.tables.MagicListTable;
import com.pj.magic.gui.tables.models.ListBackedTableModel;
import com.pj.magic.model.Customer;
import com.pj.magic.model.PromoRaffleTicket;
import com.pj.magic.model.PromoRaffleTicketClaim;
import com.pj.magic.model.SalesInvoice;
import com.pj.magic.model.search.SalesInvoiceSearchCriteria;
import com.pj.magic.service.CustomerService;
import com.pj.magic.service.SalesInvoiceService;
import com.pj.magic.service.impl.PromoService;
import com.pj.magic.service.impl.PromoServiceImpl;
import com.pj.magic.util.ComponentUtil;
import com.pj.magic.util.ExcelUtil;
import com.pj.magic.util.FileUtil;
import com.pj.magic.util.FormatterUtil;

import lombok.extern.slf4j.Slf4j;
import net.sourceforge.jdatepicker.impl.JDatePanelImpl;
import net.sourceforge.jdatepicker.impl.JDatePickerImpl;
import net.sourceforge.jdatepicker.impl.UtilCalendarModel;

@Component
@Slf4j
public class JchsRaffleTicketClaimPanel extends StandardMagicPanel {

	@Autowired private SelectCustomerDialog selectCustomerDialog;
	@Autowired private CustomerService customerService;
	@Autowired private SalesInvoiceService salesInvoiceService;
	@Autowired private PromoService promoService;
	
	private PromoRaffleTicketClaim claim;
	private JTextField claimIdField = new JTextField();
	private MagicTextField customerCodeField;
	private JLabel customerNameLabel;
	private JButton selectCustomerButton;
	private JTabbedPane tabbedPane;
	private UtilCalendarModel transactionDateModel;
	private JDatePickerImpl transactionDatePicker;
	private JTextField totalAmountField = new JTextField();
	private MagicTextField totalTicketsField = new MagicTextField();
	private JButton checkButton;
	private JButton claimButton;
	private MagicToolBarButton excelButton;
	
	private MagicListTable salesInvoicesTable;
	private SalesInvoicesTableModel salesInvoicesTableModel = new SalesInvoicesTableModel();
	private MagicListTable ticketsTable;
	private TicketsTableModel ticketsTableModel = new TicketsTableModel();
	
	@Override
	protected void initializeComponents() {
		claimIdField.setEditable(false);
				
		customerCodeField = new MagicTextField();
		customerCodeField.setMaximumLength(Constants.CUSTOMER_CODE_MAXIMUM_LENGTH);
		customerCodeField.getDocument().addDocumentListener(new DocumentListener() {
			
			@Override
			public void removeUpdate(DocumentEvent e) {
				clearClaimComponents();
			}
			
			@Override
			public void insertUpdate(DocumentEvent e) {
				clearClaimComponents();
			}
			
			@Override
			public void changedUpdate(DocumentEvent e) {
				clearClaimComponents();
			}
		});
		
		selectCustomerButton = new EllipsisButton();
		selectCustomerButton.setToolTipText("Select Customer");
		selectCustomerButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				selectCustomer();
			}
		});;
		
		transactionDateModel = new UtilCalendarModel();
		transactionDateModel.addPropertyChangeListener(new PropertyChangeListener() {
			
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				if ("value".equals(evt.getPropertyName()) && evt.getOldValue() != null 
						&& evt.getNewValue() != null && !evt.getOldValue().equals(evt.getNewValue())) {
					clearClaimComponents();
				}
			}
		});
		
		checkButton = new JButton("Check");
		checkButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				checkClaimableTickets();
			}
		});
		
		claimButton = new JButton("Claim");
		claimButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				claimTickets();
			}
		});
		
		totalAmountField.setEditable(false);
		totalTicketsField.setEditable(false);
		
		salesInvoicesTable = new MagicListTable(salesInvoicesTableModel);
		ticketsTable = new MagicListTable(ticketsTableModel);
		
		focusOnComponentWhenThisPanelIsDisplayed(customerCodeField);
	}

	private void clearClaimComponents() {
		totalAmountField.setText(null);
		totalTicketsField.setText(null);
		claimButton.setEnabled(false);
		salesInvoicesTableModel.clear();
	}

	private void checkClaimableTickets() {
		String customerCode = customerCodeField.getText();
		if (StringUtils.isEmpty(customerCode)) {
			showErrorMessage("Customer must not be empty");
			customerCodeField.requestFocusInWindow();
			return;
		}

		Calendar transactionDateCalendarObject = transactionDateModel.getValue();
		if (transactionDateCalendarObject == null) {
			showErrorMessage("Transaction Date must not be empty");
			return;
		} else {
			claim.setTransactionDate(transactionDateCalendarObject.getTime());
		}
		
		Customer customer = customerService.findCustomerByCode(customerCode);
		if (customer == null) {
			showErrorMessage("Customer code is invalid");
			customerCodeField.requestFocusInWindow();
			return;
		} else {
			claim.setCustomer(customer);
		}
		
		List<SalesInvoice> salesInvoices = getEligibleSalesInvoices(customer, transactionDateCalendarObject.getTime());
		
		BigDecimal totalAmount = BigDecimal.ZERO;
		for (SalesInvoice salesInvoice : salesInvoices) {
			totalAmount = totalAmount.add(salesInvoice.getTotalNetAmount());
		}
		
		int claimableTickets = totalAmount.divideToIntegralValue(PromoServiceImpl.JCHS_RAFFLE_SALES_AMOUNT_PER_TICKET).intValue();
		
		totalAmountField.setText(FormatterUtil.formatAmount(totalAmount));
		totalTicketsField.setText(String.valueOf(claimableTickets));
		salesInvoicesTableModel.setItems(salesInvoices);
		claimButton.setEnabled(true);
	}

	private List<SalesInvoice> getEligibleSalesInvoices(Customer customer, Date salesDate) {
		SalesInvoiceSearchCriteria criteria = new SalesInvoiceSearchCriteria();
		criteria.setCustomer(customer);
		criteria.setTransactionDate(salesDate);
		criteria.setMarked(true);
		
		return salesInvoiceService.search(criteria);
	}

	private void claimTickets() {
		int totalTickets = totalTicketsField.getTextAsInteger();
		if (totalTickets == 0) {
			showErrorMessage("No ticket to claim");
			return;
		}
		
		try {
			claim = promoService.claimJchsRaffleTickets(claim.getCustomer(), claim.getTransactionDate());
		} catch (AlreadyClaimedException e) {
			showErrorMessage("Tickets already claimed for this transaction date");
			return;
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			showErrorMessage("Error claiming raffle tickets: " + e.getMessage());
			return;
		}
		
		showMessage("Claim raffle tickets success!");
		updateDisplay(claim);
	}

	@Override
	protected void registerKeyBindings() {
	}

	protected void selectCustomer() {
		selectCustomerDialog.searchActiveCustomers(customerCodeField.getText());
		selectCustomerDialog.setVisible(true);
		
		Customer customer = selectCustomerDialog.getSelectedCustomer();
		if (customer != null) {
			customerCodeField.setText(customer.getCode());
			customerNameLabel.setText(customer.getName());
		} else {
			customerNameLabel.setText(null);
		}
	}

	@Override
	protected void doOnBack() {
		getMagicFrame().switchToJchsRaffleTicketClaimsListPanel();
	}
	
	public void updateDisplay(PromoRaffleTicketClaim claim) {
		tabbedPane.setSelectedIndex(0);
		
		if (claim.getId() == null) {
			this.claim = claim;
			clearDisplay();
			return;
		}
		
		this.claim = claim = promoService.getJchsRaffleTicketClaim(claim.getId());
		
		claimIdField.setText(String.valueOf(claim.getId()));
		customerCodeField.setText(claim.getCustomer().getCode());
		customerCodeField.setEditable(false);
		customerNameLabel.setText(claim.getCustomer().getName());
		selectCustomerButton.setEnabled(false);
		transactionDateModel.setValue(DateUtils.toCalendar(claim.getTransactionDate()));
		transactionDatePicker.getComponents()[1].setVisible(false);
		totalAmountField.setText(FormatterUtil.formatAmount(claim.getTotalAmount()));
		totalTicketsField.setText(String.valueOf(claim.getNumberOfTickets()));
		checkButton.setEnabled(false);
		claimButton.setEnabled(false);
		ticketsTableModel.setItems(claim.getTickets());
		salesInvoicesTableModel.setItems(claim.getSalesInvoices());
		excelButton.setEnabled(true);
	}

	private void clearDisplay() {
		claimIdField.setText(null);
		customerCodeField.setEditable(true);
		customerCodeField.setText(null);
		customerNameLabel.setText(null);
		selectCustomerButton.setEnabled(true);
		transactionDateModel.setValue(null);
		transactionDatePicker.getComponents()[1].setVisible(true);
		totalAmountField.setText(null);
		totalTicketsField.setText(null);
		checkButton.setEnabled(true);
		claimButton.setEnabled(false);
		ticketsTableModel.clear();
		salesInvoicesTableModel.clear();
		excelButton.setEnabled(false);
	}

	private JPanel createCustomerPanel() {
		JPanel panel = new JPanel();
		panel.setLayout(new GridBagLayout());
		
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		c.anchor = GridBagConstraints.WEST;
		panel.add(customerCodeField, c);
		
		c = new GridBagConstraints();
		c.insets.left = 3;
		c.gridx = 1;
		c.gridy = 0;
		c.anchor = GridBagConstraints.WEST;
		selectCustomerButton.setPreferredSize(new Dimension(30, 24));
		panel.add(selectCustomerButton, c);
		
		c = new GridBagConstraints();
		c.gridx = 2;
		c.gridy = 0;
		c.anchor = GridBagConstraints.WEST;
		panel.add(Box.createHorizontalStrut(10), c);
		
		c = new GridBagConstraints();
		c.gridx = 3;
		c.gridy = 0;
		c.anchor = GridBagConstraints.WEST;
		customerNameLabel.setPreferredSize(new Dimension(300, 20));
		panel.add(customerNameLabel, c);
		
		return panel;
	}

	@Override
	protected void layoutMainPanel(JPanel mainPanel) {
		mainPanel.setLayout(new GridBagLayout());
		
		int currentRow = 0;

		GridBagConstraints c = new GridBagConstraints();
		c.insets.top = 30;
		c.gridx = 0;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(Box.createHorizontalStrut(50), c);

		c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(ComponentUtil.createLabel(120, "Claim ID:"), c);
		
		c = new GridBagConstraints();
		c.gridx = 2;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		claimIdField.setPreferredSize(new Dimension(150, 25));
		mainPanel.add(claimIdField, c);
		
		c = new GridBagConstraints();
		c.gridx = 3;
		c.gridy = currentRow;
		mainPanel.add(Box.createHorizontalStrut(100), c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(ComponentUtil.createLabel(120, "Customer:"), c);
		
		c = new GridBagConstraints();
		c.gridx = 2;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		c.gridwidth = 4;
		
		customerCodeField.setPreferredSize(new Dimension(140, 25));
		customerNameLabel = ComponentUtil.createLabel(190, "");
		
		mainPanel.add(createCustomerPanel(), c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = currentRow;
		mainPanel.add(Box.createHorizontalStrut(50), c);

		c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(ComponentUtil.createLabel(200, "Transaction Date:"), c);
		
		c = new GridBagConstraints();
		c.gridx = 2;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		
		JDatePanelImpl datePanel = new JDatePanelImpl(transactionDateModel);
		transactionDatePicker = new JDatePickerImpl(datePanel, new DatePickerFormatter());
		mainPanel.add(transactionDatePicker, c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(ComponentUtil.createLabel(120, "Total Amount:"), c);
		
		c = new GridBagConstraints();
		c.gridx = 2;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		totalAmountField.setPreferredSize(new Dimension(150, 25));
		mainPanel.add(totalAmountField, c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(ComponentUtil.createLabel(120, "Total Tickets:"), c);
		
		c = new GridBagConstraints();
		c.gridx = 2;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		totalTicketsField.setPreferredSize(new Dimension(150, 25));
		mainPanel.add(totalTicketsField, c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.insets.top = 10;
		c.gridx = 2;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(ComponentUtil.createGenericPanel(checkButton, Box.createHorizontalStrut(5), claimButton), c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.insets.top = 20;
		c.fill = GridBagConstraints.BOTH;
		c.weightx = c.weighty = 1.0;
		c.gridx = 0;
		c.gridy = currentRow;
		c.gridwidth = 6;
		
		tabbedPane = createTabbedPane();
		tabbedPane.setPreferredSize(new Dimension(600, 250));
		mainPanel.add(tabbedPane, c);
	}

	@Override
	protected void addToolBarButtons(MagicToolBar toolBar) {
		excelButton = new MagicToolBarButton("excel", "Generate Excel");
		excelButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				generateExcel();
			}
		});
		toolBar.add(excelButton);
	}

	private void generateExcel() {
		MagicFileChooser excelFileChooser = FileUtil.createSaveFileChooser(
				"Christmas Raffle Papremyo 2023 - " + claim.getCustomer().getCode() 
				+ new SimpleDateFormat("mmmDD").format(claim.getTransactionDate()) + ".xlsx");
		if (!excelFileChooser.selectSaveFile(this)) {
			return;
		}
		
		try (
			Workbook workbook = new JchsRaffleTicketClaimExcelGenerator().generate(claim);
			FileOutputStream out = new FileOutputStream(excelFileChooser.getSelectedFile());
		) {
			workbook.write(out);
			showMessage("Excel generated successfully");
		} catch (IOException e) {
			showErrorMessage("Unexpected error during excel generation");
		}
		
		try {
			ExcelUtil.openExcelFile(excelFileChooser.getSelectedFile());
		} catch (IOException e) {
			showMessageForUnexpectedError();
		}
	}

	private JTabbedPane createTabbedPane() {
		JTabbedPane tabbedPane = new JTabbedPane();
		tabbedPane.addTab("Sales Invoices", createSalesInvoicesPanel());
		tabbedPane.addTab("Tickets", createTicketsPanel());
		return tabbedPane;
	}

	private JPanel createSalesInvoicesPanel() {
		JPanel panel = new JPanel();
		panel.setLayout(new GridBagLayout());
		
		int currentRow = 0;
		
		GridBagConstraints c = new GridBagConstraints();
		
		c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;
		c.weightx = c.weighty = 1.0;
		c.gridx = 0;
		c.gridy = currentRow;
		JScrollPane salesInvoicesTableScrollPane = new JScrollPane(salesInvoicesTable);
		salesInvoicesTableScrollPane.setPreferredSize(new Dimension(600, 150));
		panel.add(salesInvoicesTableScrollPane, c);
		
		return panel;
	}
	
	private JPanel createTicketsPanel() {
		JPanel panel = new JPanel();
		panel.setLayout(new GridBagLayout());
		
		int currentRow = 0;
		
		GridBagConstraints c = new GridBagConstraints();
		
		c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;
		c.weightx = c.weighty = 1.0;
		c.gridx = 0;
		c.gridy = currentRow;
		JScrollPane salesInvoicesTableScrollPane = new JScrollPane(ticketsTable);
		salesInvoicesTableScrollPane.setPreferredSize(new Dimension(600, 150));
		panel.add(salesInvoicesTableScrollPane, c);
		
		return panel;
	}
	
	private class SalesInvoicesTableModel extends ListBackedTableModel<SalesInvoice>{

		@Override
		protected String[] getColumnNames() {
			return new String[] {"Sales Invoice No.", "Amount"};
		}

		@Override
		public Object getValueAt(int rowIndex, int columnIndex) {
			SalesInvoice salesInvoice = getItem(rowIndex);
			switch (columnIndex) {
			case 0:
				return String.valueOf(salesInvoice.getSalesInvoiceNumber());
			case 1:
				return FormatterUtil.formatAmount(salesInvoice.getTotalNetAmount());
			default:
				throw new RuntimeException("Fetching invalid column index: " + columnIndex);
			}
		}

	}
	
	private class TicketsTableModel extends ListBackedTableModel<PromoRaffleTicket>{

		@Override
		protected String[] getColumnNames() {
			return new String[] {"Ticket No."};
		}

		@Override
		public Object getValueAt(int rowIndex, int columnIndex) {
			PromoRaffleTicket ticket = getItem(rowIndex);
			switch (columnIndex) {
			case 0:
				return ticket.getTicketNumberDisplay();
			default:
				throw new RuntimeException("Fetching invalid column index: " + columnIndex);
			}
		}

	}
	
}