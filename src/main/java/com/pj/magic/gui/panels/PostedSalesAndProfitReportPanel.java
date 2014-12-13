package com.pj.magic.gui.panels;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableColumnModel;

import net.sourceforge.jdatepicker.impl.JDatePanelImpl;
import net.sourceforge.jdatepicker.impl.JDatePickerImpl;
import net.sourceforge.jdatepicker.impl.UtilCalendarModel;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.pj.magic.Constants;
import com.pj.magic.gui.component.DatePickerFormatter;
import com.pj.magic.gui.component.EllipsisButton;
import com.pj.magic.gui.component.MagicTextField;
import com.pj.magic.gui.component.MagicToolBar;
import com.pj.magic.gui.component.MagicToolBarButton;
import com.pj.magic.gui.dialog.PrintPreviewDialog;
import com.pj.magic.gui.dialog.SelectCustomerDialog;
import com.pj.magic.gui.tables.MagicListTable;
import com.pj.magic.model.Customer;
import com.pj.magic.model.SalesInvoice;
import com.pj.magic.model.SalesReturn;
import com.pj.magic.model.report.PostedSalesAndProfitReport;
import com.pj.magic.model.report.PostedSalesAndProfitReportItem;
import com.pj.magic.model.search.SalesInvoiceSearchCriteria;
import com.pj.magic.model.search.SalesReturnSearchCriteria;
import com.pj.magic.service.CustomerService;
import com.pj.magic.service.PrintService;
import com.pj.magic.service.PrintServiceImpl;
import com.pj.magic.service.SalesInvoiceService;
import com.pj.magic.service.SalesReturnService;
import com.pj.magic.util.ComponentUtil;
import com.pj.magic.util.FormatterUtil;

@Component
public class PostedSalesAndProfitReportPanel extends StandardMagicPanel {

	private static final int TRANSACTION_DATE_COLUMN_INDEX = 0;
	private static final int SALES_INVOICE_NUMBER_COLUMN_INDEX = 1;
	private static final int CUSTOMER_COLUMN_INDEX = 2;
	private static final int TOTAL_AMOUNT_COLUMN_INDEX = 3;
	private static final int TOTAL_DISCOUNTS_COLUMN_INDEX = 4;
	private static final int NET_AMOUNT_COLUMN_INDEX = 5;
	private static final int NET_COST_COLUMN_INDEX = 6;
	private static final int NET_PROFIT_COLUMN_INDEX = 7;
	
	@Autowired private CustomerService customerService;
	@Autowired private SalesInvoiceService salesInvoiceService;
	@Autowired private SelectCustomerDialog selectCustomerDialog;
	@Autowired private PrintPreviewDialog printPreviewDialog;
	@Autowired private PrintService printService;
	@Autowired private SalesReturnService salesReturnService;
	
	private MagicTextField customerCodeField;
	private JLabel customerNameLabel;
	private UtilCalendarModel fromDateModel;
	private UtilCalendarModel toDateModel;
	private JButton generateButton;
	private EllipsisButton selectCustomerButton;
	private MagicListTable table;
	private PostedSalesAndProfitReportItemsTableModel tableModel;
	private Customer customer;
	
	@Override
	protected void initializeComponents() {
		customerCodeField = new MagicTextField();
		customerCodeField.setMaximumLength(Constants.CUSTOMER_CODE_MAXIMUM_LENGTH);
		
		customerNameLabel = new JLabel();
		
		selectCustomerButton = new EllipsisButton();
		selectCustomerButton.setToolTipText("Select Customer");
		selectCustomerButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				openSelectCustomerDialog();
			}
		});
		
		fromDateModel = new UtilCalendarModel();
		toDateModel = new UtilCalendarModel();
		
		generateButton = new JButton("Generate");
		generateButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				generateReport();
			}
		});
		
		focusOnComponentWhenThisPanelIsDisplayed(customerCodeField);
		
		initializeTable();
	}

	private void initializeTable() {
		tableModel = new PostedSalesAndProfitReportItemsTableModel();
		table = new MagicListTable(tableModel);
		
		TableColumnModel columnModel = table.getColumnModel();
		columnModel.getColumn(SALES_INVOICE_NUMBER_COLUMN_INDEX).setPreferredWidth(50);
		columnModel.getColumn(CUSTOMER_COLUMN_INDEX).setPreferredWidth(200);
	}

	protected void openSelectCustomerDialog() {
		selectCustomerDialog.searchCustomers(customerCodeField.getText());
		selectCustomerDialog.setVisible(true);
		
		Customer customer = selectCustomerDialog.getSelectedCustomer();
		if (customer != null) {
			customerCodeField.setText(customer.getCode());
			customerNameLabel.setText(customer.getName());
		}
	}

	private void generateReport() {
		if (fromDateModel.getValue() == null) {
			showErrorMessage("Tran. Date From must be specified");
			return;
		}
		if (toDateModel.getValue() == null) {
			showErrorMessage("Tran. Date To must be specified");
			return;
		}
		
		customer = customerService.findCustomerByCode(customerCodeField.getText());
		if (customer == null) {
			customerNameLabel.setText("-");
		} else {
			customerCodeField.setText(customer.getCode());
			customerNameLabel.setText(customer.getName());
		}
		
		List<PostedSalesAndProfitReportItem> items = retrieveReportItems();
		tableModel.setItems(items);
		if (items.isEmpty()) {
			showErrorMessage("No records found");
		}
	}

	private List<PostedSalesAndProfitReportItem> retrieveReportItems() {
		List<PostedSalesAndProfitReportItem> items = new ArrayList<>();
		
		SalesInvoiceSearchCriteria salesInvoiceCriteria = new SalesInvoiceSearchCriteria();
		salesInvoiceCriteria.setMarked(true);
		salesInvoiceCriteria.setOrderBy("TRANSACTION_DT, SALES_INVOICE_NO");
		salesInvoiceCriteria.setCustomer(customer);
		salesInvoiceCriteria.setTransactionDateFrom(fromDateModel.getValue().getTime());
		salesInvoiceCriteria.setTransactionDateTo(toDateModel.getValue().getTime());
		
		List<SalesInvoice> salesInvoices = salesInvoiceService.search(salesInvoiceCriteria);
		items.addAll(
			Collections2.transform(salesInvoices, new Function<SalesInvoice, PostedSalesAndProfitReportItem>() {
	
				@Override
				public PostedSalesAndProfitReportItem apply(SalesInvoice input) {
					return new PostedSalesAndProfitReportItem(input);
				}
			})
		);
		
		SalesReturnSearchCriteria salesReturnCriteria = new SalesReturnSearchCriteria();
		salesReturnCriteria.setPosted(true);
		salesReturnCriteria.setCustomer(customer);
		salesReturnCriteria.setPostDateFrom(fromDateModel.getValue().getTime());
		salesReturnCriteria.setPostDateTo(toDateModel.getValue().getTime());
		
		List<SalesReturn> salesReturns = salesReturnService.search(salesReturnCriteria);
		items.addAll(
			Collections2.transform(salesReturns, new Function<SalesReturn, PostedSalesAndProfitReportItem>() {
	
				@Override
				public PostedSalesAndProfitReportItem apply(SalesReturn input) {
					return new PostedSalesAndProfitReportItem(input);
				}
			})
		);
		
		Collections.sort(items, new Comparator<PostedSalesAndProfitReportItem>() {

			@Override
			public int compare(PostedSalesAndProfitReportItem o1, PostedSalesAndProfitReportItem o2) {
				int result = o1.getTransactionDate().compareTo(o2.getTransactionDate());
				if (result == 0) {
					return o1.getTransactionNumber().compareTo(o2.getTransactionNumber());
				} else {
					return result;
				}
			}
		});
		
		return items;
	}
	
	@Override
	protected void layoutMainPanel(JPanel mainPanel) {
		mainPanel.setLayout(new GridBagLayout());
		
		int currentRow = 0;
		
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = currentRow;
		mainPanel.add(ComponentUtil.createHorizontalFiller(50), c);
		
		c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(ComponentUtil.createLabel(120, "Customer Code: "), c);
		
		c = new GridBagConstraints();
		c.gridx = 2;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		c.gridwidth = 5;
		mainPanel.add(createCustomerPanel(), c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(ComponentUtil.createLabel(130, "Tran. Date From: "), c);
		
		c = new GridBagConstraints();
		c.gridx = 2;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		
		JDatePanelImpl datePanel = new JDatePanelImpl(fromDateModel);
		JDatePickerImpl datePicker = new JDatePickerImpl(datePanel, new DatePickerFormatter());
		mainPanel.add(datePicker, c);
		
		c = new GridBagConstraints();
		c.gridx = 3;
		c.gridy = currentRow;
		mainPanel.add(ComponentUtil.createHorizontalFiller(50), c);
		
		c = new GridBagConstraints();
		c.gridx = 4;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(ComponentUtil.createLabel(120, "Tran. Date To: "), c);
		
		c = new GridBagConstraints();
		c.gridx = 5;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		
		datePanel = new JDatePanelImpl(toDateModel);
		datePicker = new JDatePickerImpl(datePanel, new DatePickerFormatter());
		mainPanel.add(datePicker, c);
		
		c = new GridBagConstraints();
		c.weightx = 1.0; // right space filler
		c.gridx = 6;
		c.gridy = currentRow;
		mainPanel.add(ComponentUtil.createFiller(), c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = currentRow;
		mainPanel.add(ComponentUtil.createVerticalFiller(20), c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 2;
		c.gridy = currentRow;
		c.gridwidth = 4;
		generateButton.setPreferredSize(new Dimension(160, 25));
		mainPanel.add(generateButton, c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = currentRow;
		mainPanel.add(ComponentUtil.createFiller(1, 30), c);
		
		currentRow++;
		
		c.fill = GridBagConstraints.BOTH;
		c.weightx = c.weighty = 1.0;
		c.gridx = 0;
		c.gridy = currentRow;
		c.gridwidth = 7;
		c.anchor = GridBagConstraints.CENTER;
		JScrollPane itemsTableScrollPane = new JScrollPane(table);
		itemsTableScrollPane.setPreferredSize(new Dimension(600, 100));
		mainPanel.add(itemsTableScrollPane, c);
	}

	private JPanel createCustomerPanel() {
		customerCodeField.setPreferredSize(new Dimension(150, 25));
		customerNameLabel.setPreferredSize(new Dimension(300, 20));
		
		JPanel panel = new JPanel();
		panel.add(customerCodeField);
		panel.add(selectCustomerButton);
		panel.add(Box.createHorizontalStrut(10));
		panel.add(customerNameLabel);
		return panel;
	}

	@Override
	protected void registerKeyBindings() {
		// none
	}

	public void updateDisplay() {
		customerCodeField.setText(null);
		customerNameLabel.setText(null);
		fromDateModel.setValue(Calendar.getInstance());
		toDateModel.setValue(Calendar.getInstance());
		tableModel.clear();
		customer = null;
	}

	@Override
	protected void doOnBack() {
		getMagicFrame().switchToMainMenuPanel();
	}

	@Override
	protected void addToolBarButtons(MagicToolBar toolBar) {
		JButton printPreviewButton = new MagicToolBarButton("print_preview", "Print Preview");
		printPreviewButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				printPreviewReport();
			}
		});
		toolBar.add(printPreviewButton);
		
		JButton printButton = new MagicToolBarButton("print", "Print");
		printButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				printReport();
			}
		});
		toolBar.add(printButton);
	}

	private void printPreviewReport() {
		PostedSalesAndProfitReport report = createReport();
		printPreviewDialog.updateDisplay(printService.generateReportAsString(report));
		printPreviewDialog.setColumnsPerLine(
				PrintServiceImpl.POSTED_SALES_AND_PROFIT_REPORT_CHARACTERS_PER_LINE);
		printPreviewDialog.setUseCondensedFontForPrinting(true);
		printPreviewDialog.setVisible(true);
	}

	private PostedSalesAndProfitReport createReport() {
		PostedSalesAndProfitReport report = new PostedSalesAndProfitReport();
		report.setCustomer(customer);
		if (fromDateModel.getValue() != null) {
			report.setTransactionDateFrom(fromDateModel.getValue().getTime());
		}
		if (toDateModel.getValue() != null) {
			report.setTransactionDateTo(toDateModel.getValue().getTime());
		}
		report.setItems(tableModel.getItems());
		return report;
	}

	private void printReport() {
		printService.print(createReport());
	}

	private class PostedSalesAndProfitReportItemsTableModel extends AbstractTableModel {

		private final String[] columnNames =
			{"Tran. Date", "SI No.", "Customer", "Total Amount", "Total Disc.", "Net Amount",
				"Net Cost", "Net Profit"};
		
		private List<PostedSalesAndProfitReportItem> items = new ArrayList<>();
		
		public void setItems(List<PostedSalesAndProfitReportItem> items) {
			this.items = items;
			fireTableDataChanged();
		}
		
		public List<PostedSalesAndProfitReportItem> getItems() {
			return items;
		}
		
		public void clear() {
			items.clear();
			fireTableDataChanged();
		}

		@Override
		public int getRowCount() {
			return items.size();
		}

		@Override
		public int getColumnCount() {
			return columnNames.length;
		}

		@Override
		public String getColumnName(int column) {
			return columnNames[column];
		}
		
		@Override
		public Object getValueAt(int rowIndex, int columnIndex) {
			PostedSalesAndProfitReportItem item = items.get(rowIndex);
			if (item.getTransactionDate() == null) {
				System.out.println(ToStringBuilder.reflectionToString(item));
			}
			switch (columnIndex) {
			case TRANSACTION_DATE_COLUMN_INDEX:
				return FormatterUtil.formatDate(item.getTransactionDate());
			case SALES_INVOICE_NUMBER_COLUMN_INDEX:
				return item.getTransactionNumber();
			case CUSTOMER_COLUMN_INDEX:
				return item.getCustomer().getName();
			case TOTAL_AMOUNT_COLUMN_INDEX:
				BigDecimal totalAmount = item.getTotalAmount();
				return (totalAmount != null) ? FormatterUtil.formatAmount(item.getTotalAmount()) : null;
			case TOTAL_DISCOUNTS_COLUMN_INDEX:
				BigDecimal totalDiscounts = item.getTotalDiscounts();
				return (totalDiscounts != null) ? FormatterUtil.formatAmount(totalDiscounts) : null;
			case NET_AMOUNT_COLUMN_INDEX:
				BigDecimal netAmount = item.getNetAmount();
				return (netAmount != null) ? FormatterUtil.formatAmount(item.getNetAmount()) : null;
			case NET_COST_COLUMN_INDEX:
				BigDecimal netCost = item.getNetCost();
				return (netCost != null) ? FormatterUtil.formatAmount(netCost) : null;
			case NET_PROFIT_COLUMN_INDEX:
				BigDecimal netProfit = item.getNetProfit();
				return (netProfit != null) ? FormatterUtil.formatAmount(netProfit) : null;
			default:
				throw new RuntimeException("Fetching invalid column index: " + columnIndex);
			}
		}
		
		@Override
		public Class<?> getColumnClass(int columnIndex) {
			switch (columnIndex) {
			case TOTAL_AMOUNT_COLUMN_INDEX:
			case TOTAL_DISCOUNTS_COLUMN_INDEX:
			case NET_AMOUNT_COLUMN_INDEX:
			case NET_COST_COLUMN_INDEX:
			case NET_PROFIT_COLUMN_INDEX:
				return Number.class;
			default:
				return Object.class;
			}
		}
		
	}
	
}
