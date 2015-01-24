package com.pj.magic.gui.panels;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.swing.Box;
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

import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.pj.magic.gui.component.DatePickerFormatter;
import com.pj.magic.gui.component.MagicToolBar;
import com.pj.magic.gui.component.MagicToolBarButton;
import com.pj.magic.gui.dialog.PrintPreviewDialog;
import com.pj.magic.gui.tables.MagicListTable;
import com.pj.magic.model.BadStockReturn;
import com.pj.magic.model.NoMoreStockAdjustment;
import com.pj.magic.model.PaymentAdjustment;
import com.pj.magic.model.PaymentSalesInvoice;
import com.pj.magic.model.PaymentTerminal;
import com.pj.magic.model.SalesReturn;
import com.pj.magic.model.report.CashFlowReport;
import com.pj.magic.model.report.CashFlowReportItem;
import com.pj.magic.model.search.BadStockReturnSearchCriteria;
import com.pj.magic.model.search.NoMoreStockAdjustmentSearchCriteria;
import com.pj.magic.model.search.PaymentAdjustmentSearchCriteria;
import com.pj.magic.model.search.PaymentSalesInvoiceSearchCriteria;
import com.pj.magic.model.search.SalesReturnSearchCriteria;
import com.pj.magic.model.util.TimePeriod;
import com.pj.magic.service.BadStockReturnService;
import com.pj.magic.service.NoMoreStockAdjustmentService;
import com.pj.magic.service.PaymentAdjustmentService;
import com.pj.magic.service.PaymentService;
import com.pj.magic.service.PaymentTerminalService;
import com.pj.magic.service.PrintService;
import com.pj.magic.service.PrintServiceImpl;
import com.pj.magic.service.SalesReturnService;
import com.pj.magic.util.ComponentUtil;
import com.pj.magic.util.FormatterUtil;

@Component
public class CashFlowReportPanel extends StandardMagicPanel {

	private static final int TIME_COLUMN_INDEX = 0;
	private static final int TRANSACTION_TYPE_COLUMN_INDEX = 1;
	private static final int REFERENCE_NUMBER_COLUMN_INDEX = 2;
	private static final int CUSTOMER_COLUMN_INDEX = 3;
	private static final int TRANSACTION_DATE_COLUMN_INDEX = 4;
	private static final int AMOUNT_COLUMN_INDEX = 5;
	private static final int PAYMENT_TERMINAL_COLUMN_INDEX = 6;
	
	@Autowired private PaymentService paymentService;
	@Autowired private PrintPreviewDialog printPreviewDialog;
	@Autowired private PrintService printService;
	@Autowired private PaymentTerminalService paymentTerminalService;
	@Autowired private SalesReturnService salesReturnService;
	@Autowired private BadStockReturnService badStockReturnService;
	@Autowired private NoMoreStockAdjustmentService noMoreStockAdjustmentService;
	@Autowired private PaymentAdjustmentService paymentAdjustmentService;
	
	private MagicListTable table;
	private CashFlowReportItemsTableModel tableModel;
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
				searchCashFlowReportItems();
			}
		});
		
		initializeTable();
		focusOnComponentWhenThisPanelIsDisplayed(table);
	}

	private void searchCashFlowReportItems() {
		if (paymentDateModel.getValue() == null) {
			showErrorMessage("Payment Date must be specified");
			return;
		}
		
		List<CashFlowReportItem> items = doSearchCashFlowReportItems();
		tableModel.setItems(items);
		if (items.isEmpty()) {
			showErrorMessage("No records found");
		}
	}

	private List<CashFlowReportItem> doSearchCashFlowReportItems() {
		List<CashFlowReportItem> items = new ArrayList<>();
		
		List<PaymentSalesInvoice> paymentSalesInvoices = searchPaymentSalesInvoices();
		items.addAll(Collections2.transform(paymentSalesInvoices, 
				new Function<PaymentSalesInvoice, CashFlowReportItem>() {

					@Override
					public CashFlowReportItem apply(PaymentSalesInvoice input) {
						return new CashFlowReportItem(input);
					}
		}));
		
		List<SalesReturn> salesReturns = searchSalesReturns();
		items.addAll(Collections2.transform(salesReturns, 
				new Function<SalesReturn, CashFlowReportItem>() {

					@Override
					public CashFlowReportItem apply(SalesReturn input) {
						return new CashFlowReportItem(input);
					}
		}));
		
		List<BadStockReturn> badStockReturns = searchBadStockReturns();
		items.addAll(Collections2.transform(badStockReturns, 
				new Function<BadStockReturn, CashFlowReportItem>() {

					@Override
					public CashFlowReportItem apply(BadStockReturn input) {
						return new CashFlowReportItem(input);
					}
		}));
		
		List<NoMoreStockAdjustment> noMoreStockAdjustments = searchNoMoreStockAdjustments();
		items.addAll(Collections2.transform(noMoreStockAdjustments, 
				new Function<NoMoreStockAdjustment, CashFlowReportItem>() {

					@Override
					public CashFlowReportItem apply(NoMoreStockAdjustment input) {
						return new CashFlowReportItem(input);
					}
		}));
		
		List<PaymentAdjustment> paymentAdjustments = searchPaymentAdjustments();
		items.addAll(Collections2.transform(paymentAdjustments, 
				new Function<PaymentAdjustment, CashFlowReportItem>() {

					@Override
					public CashFlowReportItem apply(PaymentAdjustment input) {
						return new CashFlowReportItem(input);
					}
		}));
		
		Collections.sort(items, new Comparator<CashFlowReportItem>() {

			@Override
			public int compare(CashFlowReportItem o1, CashFlowReportItem o2) {
				return o1.getTime().compareTo(o2.getTime());
			}
			
		});
		
		return items;
	}
	
	private List<PaymentAdjustment> searchPaymentAdjustments() {
		PaymentAdjustmentSearchCriteria criteria = new PaymentAdjustmentSearchCriteria();
		criteria.setPaid(true);
		criteria.setPaidDate(paymentDateModel.getValue().getTime());
		criteria.setPaymentTerminal((PaymentTerminal)paymentTerminalComboBox.getSelectedItem());
		
		switch (timePeriodComboBox.getSelectedIndex()) {
		case 1:
			criteria.setTimePeriod(TimePeriod.MORNING_ONLY);
			break;
		case 2:
			criteria.setTimePeriod(TimePeriod.AFTERNOON_ONLY);
			break;
		}
		
		return paymentAdjustmentService.search(criteria);
	}

	private List<NoMoreStockAdjustment> searchNoMoreStockAdjustments() {
		NoMoreStockAdjustmentSearchCriteria criteria = new NoMoreStockAdjustmentSearchCriteria();
		criteria.setPaid(true);
		criteria.setPaidDate(paymentDateModel.getValue().getTime());
		criteria.setPaymentTerminal((PaymentTerminal)paymentTerminalComboBox.getSelectedItem());
		
		switch (timePeriodComboBox.getSelectedIndex()) {
		case 1:
			criteria.setTimePeriod(TimePeriod.MORNING_ONLY);
			break;
		case 2:
			criteria.setTimePeriod(TimePeriod.AFTERNOON_ONLY);
			break;
		}
		
		return noMoreStockAdjustmentService.search(criteria);
	}

	private List<BadStockReturn> searchBadStockReturns() {
		BadStockReturnSearchCriteria criteria = new BadStockReturnSearchCriteria();
		criteria.setPaid(true);
		criteria.setPaidDate(paymentDateModel.getValue().getTime());
		criteria.setPaymentTerminal((PaymentTerminal)paymentTerminalComboBox.getSelectedItem());
		
		switch (timePeriodComboBox.getSelectedIndex()) {
		case 1:
			criteria.setTimePeriod(TimePeriod.MORNING_ONLY);
			break;
		case 2:
			criteria.setTimePeriod(TimePeriod.AFTERNOON_ONLY);
			break;
		}
		
		return badStockReturnService.search(criteria);
	}

	private List<SalesReturn> searchSalesReturns() {
		SalesReturnSearchCriteria criteria = new SalesReturnSearchCriteria();
		criteria.setPaid(true);
		criteria.setPaidDate(paymentDateModel.getValue().getTime());
		criteria.setPaymentTerminal((PaymentTerminal)paymentTerminalComboBox.getSelectedItem());
		
		switch (timePeriodComboBox.getSelectedIndex()) {
		case 1:
			criteria.setTimePeriod(TimePeriod.MORNING_ONLY);
			break;
		case 2:
			criteria.setTimePeriod(TimePeriod.AFTERNOON_ONLY);
			break;
		}
		
		return salesReturnService.search(criteria);
	}

	private List<PaymentSalesInvoice> searchPaymentSalesInvoices() {
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
		tableModel = new CashFlowReportItemsTableModel();
		table = new MagicListTable(tableModel);
		
		TableColumnModel columnModel = table.getColumnModel();
		columnModel.getColumn(TIME_COLUMN_INDEX).setPreferredWidth(50);
		columnModel.getColumn(TRANSACTION_TYPE_COLUMN_INDEX).setPreferredWidth(100);
		columnModel.getColumn(REFERENCE_NUMBER_COLUMN_INDEX).setPreferredWidth(60);
		columnModel.getColumn(CUSTOMER_COLUMN_INDEX).setPreferredWidth(300);
		columnModel.getColumn(AMOUNT_COLUMN_INDEX).setPreferredWidth(80);
	}

	@Override
	protected void doOnBack() {
		getMagicFrame().switchToReportsMenuPanel();
	}
	
	public void updateDisplay() {
		paymentDateModel.setValue(Calendar.getInstance());
		paymentTerminalComboBox.setSelectedItem(null);
		timePeriodComboBox.setSelectedIndex(0);
		List<CashFlowReportItem> items = doSearchCashFlowReportItems();
		tableModel.setItems(items);
		if (!items.isEmpty()) {
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
		mainPanel.add(Box.createHorizontalStrut(50), c);

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
		
		printService.print(createCashFlowReport());
	}

	private void printPreview() {
		if (paymentDateModel.getValue() == null) {
			showErrorMessage("Payment Date must be specified");
			return;
		}
		
		CashFlowReport report = createCashFlowReport();
		printPreviewDialog.updateDisplay(printService.generateReportAsString(report));
		printPreviewDialog.setColumnsPerLine(PrintServiceImpl.CASH_FLOW_REPORT_CHARACTERS_PER_LINE);
		printPreviewDialog.setUseCondensedFontForPrinting(true);
		printPreviewDialog.setVisible(true);
	}

	private CashFlowReport createCashFlowReport() {
		CashFlowReport report = new CashFlowReport();
		report.setItems(doSearchCashFlowReportItems());
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

	private class CashFlowReportItemsTableModel extends AbstractTableModel {

		private final String[] columnNames = 
			{"Time", "Tran. Type", "Ref. No.", "Customer", "Tran. Date", "Amount", "Terminal"};
		
		private List<CashFlowReportItem> items = new ArrayList<>();
		
		public void setItems(List<CashFlowReportItem> items) {
			this.items = items;
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
		public Object getValueAt(int rowIndex, int columnIndex) {
			CashFlowReportItem item = items.get(rowIndex);
			switch (columnIndex) {
			case TIME_COLUMN_INDEX:
				return FormatterUtil.formatTime(item.getTime());
			case TRANSACTION_TYPE_COLUMN_INDEX:
				return item.getTransactionType();
			case REFERENCE_NUMBER_COLUMN_INDEX:
				return item.getReferenceNumber();
			case CUSTOMER_COLUMN_INDEX:
				return item.getCustomer().getName();
			case TRANSACTION_DATE_COLUMN_INDEX:
				return FormatterUtil.formatDate(item.getTransactionDate());
			case AMOUNT_COLUMN_INDEX:
				return FormatterUtil.formatAmount(item.getAmount());
			case PAYMENT_TERMINAL_COLUMN_INDEX:
				return item.getPaymentTerminal().getName();
			default:
				throw new RuntimeException("Fetch invalid column index: " + columnIndex);
			}
		}
		
		@Override
		public Class<?> getColumnClass(int columnIndex) {
			switch (columnIndex) {
			case AMOUNT_COLUMN_INDEX:
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