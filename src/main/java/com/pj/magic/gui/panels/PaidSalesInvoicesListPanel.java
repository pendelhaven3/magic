package com.pj.magic.gui.panels;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableColumnModel;

import net.sourceforge.jdatepicker.impl.JDatePanelImpl;
import net.sourceforge.jdatepicker.impl.JDatePickerImpl;
import net.sourceforge.jdatepicker.impl.UtilCalendarModel;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.pj.magic.gui.component.DatePickerFormatter;
import com.pj.magic.gui.component.MagicToolBar;
import com.pj.magic.gui.component.MagicToolBarButton;
import com.pj.magic.gui.dialog.PrintPreviewDialog;
import com.pj.magic.gui.tables.MagicListTable;
import com.pj.magic.model.PaymentSalesInvoice;
import com.pj.magic.model.PaymentTerminal;
import com.pj.magic.model.report.PaidSalesInvoicesReport;
import com.pj.magic.model.search.PaymentSalesInvoiceSearchCriteria;
import com.pj.magic.model.util.TimePeriod;
import com.pj.magic.service.PaymentService;
import com.pj.magic.service.PaymentTerminalService;
import com.pj.magic.service.PrintService;
import com.pj.magic.service.PrintServiceImpl;
import com.pj.magic.util.ComponentUtil;
import com.pj.magic.util.FormatterUtil;

@Component
public class PaidSalesInvoicesListPanel extends StandardMagicPanel {

	private static final int PAYMENT_DATE_COLUMN_INDEX = 0;
	private static final int SALES_INVOICE_NUMBER_COLUMN_INDEX = 1;
	private static final int CUSTOMER_COLUMN_INDEX = 2;
	private static final int NET_AMOUNT_COLUMN_INDEX = 3;
	private static final int AMOUNT_DUE_COLUMN_INDEX = 4;
	private static final int PAYMENT_NUMBER_COLUMN_INDEX = 5;
	private static final int PAYMENT_TERMINAL_COLUMN_INDEX = 6;
	
	@Autowired private PaymentService paymentService;
	@Autowired private PrintPreviewDialog printPreviewDialog;
	@Autowired private PrintService printService;
	@Autowired private PaymentTerminalService paymentTerminalService;
	
	private MagicListTable table;
	private PaymentSalesInvoicesTableModel tableModel;
	private UtilCalendarModel paymentDateModel;
	private JComboBox<PaymentTerminal> paymentTerminalComboBox;
	private JComboBox<String> timePeriodComboBox;
	private JButton searchButton;
	
	@Override
	protected void initializeComponents() {
		paymentDateModel = new UtilCalendarModel();
		
		paymentTerminalComboBox = new JComboBox<>();
		List<PaymentTerminal> paymentTerminals = paymentTerminalService.getAllPaymentTerminals();
		paymentTerminalComboBox.setModel(
				new DefaultComboBoxModel<>(paymentTerminals.toArray(new PaymentTerminal[paymentTerminals.size()])));
		paymentTerminalComboBox.insertItemAt(null, 0);
		
		timePeriodComboBox = new JComboBox<>();
		timePeriodComboBox.setModel(
				new DefaultComboBoxModel<>(new String[] {"Whole Day", "Morning Only", "Afternoon Only"}));
		
		searchButton = new JButton("Search");
		searchButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				searchPaidSalesInvoices();
			}
		});
		
		initializeTable();
		focusOnComponentWhenThisPanelIsDisplayed(table);
	}

	private void searchPaidSalesInvoices() {
		if (paymentDateModel.getValue() == null) {
			showErrorMessage("Payment Date must be specified");
			return;
		}
		
		List<PaymentSalesInvoice> paymentSalesInvoices = doSearchPaidSalesInvoices();
		tableModel.setPaymentSalesInvoices(paymentSalesInvoices);
		if (paymentSalesInvoices.isEmpty()) {
			showErrorMessage("No records found");
		}
	}

	private List<PaymentSalesInvoice> doSearchPaidSalesInvoices() {
		PaymentSalesInvoiceSearchCriteria criteria = new PaymentSalesInvoiceSearchCriteria();
		criteria.setPaid(true);
		criteria.setPaymentDate(paymentDateModel.getValue().getTime());
		criteria.setPaymentTerminal((PaymentTerminal)paymentTerminalComboBox.getSelectedItem());
		
		switch (timePeriodComboBox.getSelectedIndex()) {
		case 1:
			criteria.setTimePeriod(TimePeriod.MORNING_ONLY);
			break;
		case 2:
			criteria.setTimePeriod(TimePeriod.AFTERNOON_ONLY);
			break;
		}
		
		return paymentService.searchPaymentSalesInvoices(criteria);
	}
	
	private void initializeTable() {
		tableModel = new PaymentSalesInvoicesTableModel();
		table = new MagicListTable(tableModel);
		
		TableColumnModel columnModel = table.getColumnModel();
		columnModel.getColumn(PAYMENT_DATE_COLUMN_INDEX).setPreferredWidth(140);
		columnModel.getColumn(SALES_INVOICE_NUMBER_COLUMN_INDEX).setPreferredWidth(60);
		columnModel.getColumn(CUSTOMER_COLUMN_INDEX).setPreferredWidth(300);
		columnModel.getColumn(NET_AMOUNT_COLUMN_INDEX).setPreferredWidth(80);
		columnModel.getColumn(AMOUNT_DUE_COLUMN_INDEX).setPreferredWidth(80);
		columnModel.getColumn(PAYMENT_NUMBER_COLUMN_INDEX).setPreferredWidth(80);
	}

	@Override
	protected void doOnBack() {
		getMagicFrame().switchToReportsPanel();
	}
	
	public void updateDisplay() {
		paymentDateModel.setValue(Calendar.getInstance());
		paymentTerminalComboBox.setSelectedItem(null);
		timePeriodComboBox.setSelectedIndex(0);
		List<PaymentSalesInvoice> paymentSalesInvoices = doSearchPaidSalesInvoices();
		tableModel.setPaymentSalesInvoices(paymentSalesInvoices);
		if (!paymentSalesInvoices.isEmpty()) {
			table.changeSelection(0, 0, false, false);
		}
	}

	@Override
	protected void registerKeyBindings() {
		// none
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
		mainPanel.add(ComponentUtil.createLabel(120, "Payment Date:"), c);
		
		c = new GridBagConstraints();
		c.gridx = 2;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		
		JDatePanelImpl datePanel = new JDatePanelImpl(paymentDateModel);
		JDatePickerImpl datePicker = new JDatePickerImpl(datePanel, new DatePickerFormatter());
		mainPanel.add(datePicker, c);

		c = new GridBagConstraints();
		c.gridx = 3;
		c.gridy = currentRow;
		mainPanel.add(ComponentUtil.createHorizontalFiller(30), c);

		c = new GridBagConstraints();
		c.weightx = 1.0;
		c.gridx = 4;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(searchButton, c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(ComponentUtil.createLabel(120, "Terminal:"), c);
		
		c = new GridBagConstraints();
		c.gridx = 2;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		paymentTerminalComboBox.setPreferredSize(new Dimension(100, 25));
		mainPanel.add(paymentTerminalComboBox, c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(ComponentUtil.createLabel(120, "Time Period:"), c);
		
		c = new GridBagConstraints();
		c.gridx = 2;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		timePeriodComboBox.setPreferredSize(new Dimension(150, 25));
		mainPanel.add(timePeriodComboBox, c);
		
		currentRow++;

		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = currentRow;
		mainPanel.add(ComponentUtil.createVerticalFiller(20), c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;
		c.weightx = c.weighty = 1.0;
		c.gridx = 0;
		c.gridy = currentRow;
		c.gridwidth = 5;
		
		JScrollPane scrollPane = new JScrollPane(table);
		scrollPane.setPreferredSize(new Dimension(600, 400));
		mainPanel.add(scrollPane, c);
	}

	@Override
	protected void addToolBarButtons(MagicToolBar toolBar) {
		JButton printPreviewButton = new MagicToolBarButton("print_preview", "Print Preview");
		printPreviewButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				printPreview();
			}
		});
		toolBar.add(printPreviewButton);
		
		JButton printButton = new MagicToolBarButton("print", "Print");
		printButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				print();
			}
		});
		toolBar.add(printButton);
	}

	private void print() {
		if (paymentDateModel.getValue() == null) {
			showErrorMessage("Payment Date must be specified");
			return;
		}
		
		PaidSalesInvoicesReport report = createPaidSalesInvoicesReport();
		printService.print(report);
	}

	private void printPreview() {
		if (paymentDateModel.getValue() == null) {
			showErrorMessage("Payment Date must be specified");
			return;
		}
		
		PaidSalesInvoicesReport report = createPaidSalesInvoicesReport();
		printPreviewDialog.updateDisplay(printService.generateReportAsString(report));
		printPreviewDialog.setColumnsPerLine(PrintServiceImpl.PAID_SALES_INVOICES_REPORT_CHARACTERS_PER_LINE);
		printPreviewDialog.setUseCondensedFontForPrinting(true);
		printPreviewDialog.setVisible(true);
	}

	private PaidSalesInvoicesReport createPaidSalesInvoicesReport() {
		PaidSalesInvoicesReport report = new PaidSalesInvoicesReport();
		report.setPaymentSalesInvoices(doSearchPaidSalesInvoices());
		report.setPaymentDate(paymentDateModel.getValue().getTime());
		report.setPaymentTerminal((PaymentTerminal)paymentTerminalComboBox.getSelectedItem());
		
		switch (timePeriodComboBox.getSelectedIndex()) {
		case 1:
			report.setTimePeriod(TimePeriod.MORNING_ONLY);
			break;
		case 2:
			report.setTimePeriod(TimePeriod.AFTERNOON_ONLY);
			break;
		}
		
		return report;
	}

	private class PaymentSalesInvoicesTableModel extends AbstractTableModel {

		private final String[] columnNames = 
			{"Payment Date", "SI No.", "Customer", "Net Amount", "Amount Due", "Payment No.", "Terminal"};
		
		private List<PaymentSalesInvoice> paymentSalesInvoices = new ArrayList<>();
		
		public void setPaymentSalesInvoices(List<PaymentSalesInvoice> paymentSalesInvoices) {
			this.paymentSalesInvoices = paymentSalesInvoices;
			fireTableDataChanged();
		}
		
		@Override
		public int getRowCount() {
			return paymentSalesInvoices.size();
		}

		@Override
		public int getColumnCount() {
			return columnNames.length;
		}

		@Override
		public Object getValueAt(int rowIndex, int columnIndex) {
			PaymentSalesInvoice paymentSalesInvoice = paymentSalesInvoices.get(rowIndex);
			switch (columnIndex) {
			case PAYMENT_DATE_COLUMN_INDEX:
				return FormatterUtil.formatDateTime(paymentSalesInvoice.getParent().getPostDate());
			case SALES_INVOICE_NUMBER_COLUMN_INDEX:
				return paymentSalesInvoice.getSalesInvoice().getSalesInvoiceNumber();
			case CUSTOMER_COLUMN_INDEX:
				return paymentSalesInvoice.getSalesInvoice().getCustomer().getName();
			case NET_AMOUNT_COLUMN_INDEX:
				return FormatterUtil.formatAmount(paymentSalesInvoice.getSalesInvoice().getTotalNetAmount());
			case AMOUNT_DUE_COLUMN_INDEX:
				return FormatterUtil.formatAmount(paymentSalesInvoice.getAmountDue());
			case PAYMENT_NUMBER_COLUMN_INDEX:
				return paymentSalesInvoice.getParent().getPaymentNumber();
			case PAYMENT_TERMINAL_COLUMN_INDEX:
				return paymentSalesInvoice.getParent().getPaymentTerminal().getName();
			default:
				throw new RuntimeException("Fetch invalid column index: " + columnIndex);
			}
		}
		
		@Override
		public Class<?> getColumnClass(int columnIndex) {
			switch (columnIndex) {
			case NET_AMOUNT_COLUMN_INDEX:
			case AMOUNT_DUE_COLUMN_INDEX:
				return Number.class;
			default:
				return Object.class;
			}
		}
		
		@Override
		public String getColumnName(int column) {
			return columnNames[column];
		}
		
	}
	
}