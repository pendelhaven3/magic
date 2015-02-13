package com.pj.magic.gui.panels;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

import javax.swing.Box;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.table.AbstractTableModel;

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
import com.pj.magic.model.PaymentCashPayment;
import com.pj.magic.model.PaymentCheckPayment;
import com.pj.magic.model.PaymentTerminal;
import com.pj.magic.model.report.RemittanceReport;
import com.pj.magic.model.search.PaymentCashPaymentSearchCriteria;
import com.pj.magic.model.search.PaymentCheckPaymentSearchCriteria;
import com.pj.magic.model.util.TimePeriod;
import com.pj.magic.service.PaymentService;
import com.pj.magic.service.PaymentTerminalService;
import com.pj.magic.service.PrintService;
import com.pj.magic.service.PrintServiceImpl;
import com.pj.magic.util.ComponentUtil;
import com.pj.magic.util.FormatterUtil;

@Component
public class RemittanceReportPanel extends StandardMagicPanel {

	private static final int CUSTOMER_COLUMN_INDEX = 0;
	private static final int BANK_COLUMN_INDEX = 1;
	private static final int CHECK_NUMBER_COLUMN_INDEX = 2;
	private static final int CHECK_DATE_COLUMN_INDEX = 3;
	private static final int AMOUNT_COLUMN_INDEX = 4;
	private static final int PAYMENT_TERMINAL_COLUMN_INDEX = 5;
	
	@Autowired private PaymentService paymentService;
	@Autowired private PrintPreviewDialog printPreviewDialog;
	@Autowired private PrintService printService;
	@Autowired private PaymentTerminalService paymentTerminalService;
	
	private MagicListTable table;
	private CheckPaymentsTableModel tableModel;
	private UtilCalendarModel reportDateModel;
	private JComboBox<PaymentTerminal> paymentTerminalComboBox;
	private JComboBox<String> timePeriodComboBox;
	private JButton searchButton;
	private JLabel totalCashPaymentsField = new JLabel();
	private JLabel totalCheckPaymentsField = new JLabel();
	
	@Override
	protected void initializeComponents() {
		reportDateModel = new UtilCalendarModel();
		
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
				searchRemittanceReportItems();
			}
		});
		
		initializeTable();
		focusOnComponentWhenThisPanelIsDisplayed(table);
	}

	private void searchRemittanceReportItems() {
		if (reportDateModel.getValue() == null) {
			showErrorMessage("Date must be specified");
			return;
		}
		
		RemittanceReport report = createRemittanceReport();
		tableModel.setCheckPayments(report.getCheckPayments());
		totalCashPaymentsField.setText(FormatterUtil.formatAmount(report.getTotalCashPayments()));
		totalCheckPaymentsField.setText(FormatterUtil.formatAmount(report.getTotalCheckPayments()));
	}

	private RemittanceReport createRemittanceReport() {
		RemittanceReport report = new RemittanceReport();
		report.setReportDate(reportDateModel.getValue().getTime());
		report.setPaymentTerminal((PaymentTerminal)paymentTerminalComboBox.getSelectedItem());
		
		switch (timePeriodComboBox.getSelectedIndex()) {
		case 1:
			report.setTimePeriod(TimePeriod.MORNING_ONLY);
			break;
		case 2:
			report.setTimePeriod(TimePeriod.AFTERNOON_ONLY);
			break;
		}
		
		report.setCashPayments(searchCashPayments());
		report.setCheckPayments(searchCheckPayments());
		return report;
	}

	private List<PaymentCashPayment> searchCashPayments() {
		PaymentCashPaymentSearchCriteria criteria = new PaymentCashPaymentSearchCriteria();
		criteria.setPaid(true);
		criteria.setPaymentDate(reportDateModel.getValue().getTime());
		criteria.setPaymentTerminal((PaymentTerminal)paymentTerminalComboBox.getSelectedItem());
		
		switch (timePeriodComboBox.getSelectedIndex()) {
		case 1:
			criteria.setTimePeriod(TimePeriod.MORNING_ONLY);
			break;
		case 2:
			criteria.setTimePeriod(TimePeriod.AFTERNOON_ONLY);
			break;
		}
		
		return paymentService.searchPaymentCashPayments(criteria);
	}
	
	private List<PaymentCheckPayment> searchCheckPayments() {
		PaymentCheckPaymentSearchCriteria criteria = new PaymentCheckPaymentSearchCriteria();
		criteria.setPosted(true);
		criteria.setPaymentDate(reportDateModel.getValue().getTime());
		criteria.setPaymentTerminal((PaymentTerminal)paymentTerminalComboBox.getSelectedItem());
		
		switch (timePeriodComboBox.getSelectedIndex()) {
		case 1:
			criteria.setTimePeriod(TimePeriod.MORNING_ONLY);
			break;
		case 2:
			criteria.setTimePeriod(TimePeriod.AFTERNOON_ONLY);
			break;
		}
		
		return paymentService.searchPaymentCheckPayments(criteria);
	}
	
	private void initializeTable() {
		tableModel = new CheckPaymentsTableModel();
		table = new MagicListTable(tableModel);
	}

	@Override
	protected void doOnBack() {
		getMagicFrame().switchToReportsMenuPanel();
	}
	
	public void updateDisplay() {
		reportDateModel.setValue(Calendar.getInstance());
		paymentTerminalComboBox.setSelectedItem(null);
		timePeriodComboBox.setSelectedIndex(0);
		
		totalCashPaymentsField.setText("-");
		totalCheckPaymentsField.setText("-");
		
		List<PaymentCheckPayment> checkPayments = Collections.emptyList();
		tableModel.setCheckPayments(checkPayments);
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
		mainPanel.add(ComponentUtil.createLabel(120, "Date:"), c);
		
		c = new GridBagConstraints();
		c.gridx = 2;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		
		JDatePanelImpl datePanel = new JDatePanelImpl(reportDateModel);
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
		mainPanel.add(Box.createVerticalStrut(20), c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = currentRow;
		c.gridwidth = 8;
		mainPanel.add(createTotalsPanel(), c);
				
		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = currentRow;
		mainPanel.add(Box.createVerticalStrut(20), c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;
		c.weightx = c.weighty = 1.0;
		c.gridx = 0;
		c.gridy = currentRow;
		c.gridwidth = 5;
		
		JScrollPane scrollPane = new JScrollPane(table);
		scrollPane.setPreferredSize(new Dimension(600, 200));
		mainPanel.add(scrollPane, c);
	}

	private JPanel createTotalsPanel() {
		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new GridBagLayout());
		
		int currentRow = 0;

		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(ComponentUtil.createLabel(150, "Total Cash Payments:"), c);
		
		c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		totalCashPaymentsField = ComponentUtil.createRightLabel(120, "");
		mainPanel.add(totalCashPaymentsField, c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(ComponentUtil.createLabel(160, "Total Check Payments:"), c);
		
		c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		totalCheckPaymentsField = ComponentUtil.createRightLabel(120, "");
		mainPanel.add(totalCheckPaymentsField, c);
		
		return mainPanel;
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
		if (reportDateModel.getValue() == null) {
			showErrorMessage("Date must be specified");
			return;
		}
		
		printService.print(createRemittanceReport());
	}

	private void printPreview() {
		if (reportDateModel.getValue() == null) {
			showErrorMessage("Date must be specified");
			return;
		}
		
		RemittanceReport report = createRemittanceReport();
		printPreviewDialog.updateDisplay(printService.generateReportAsString(report));
		printPreviewDialog.setColumnsPerLine(PrintServiceImpl.REMITTANCE_REPORT_CHARACTERS_PER_LINE);
		printPreviewDialog.setUseCondensedFontForPrinting(true);
		printPreviewDialog.setVisible(true);
	}

	private class CheckPaymentsTableModel extends AbstractTableModel {

		private final String[] columnNames = 
			{"Customer", "Bank", "Check No.", "Check Date", "Amount", "Terminal"};
		
		private List<PaymentCheckPayment> checkPayments = new ArrayList<>();
		
		public void setCheckPayments(List<PaymentCheckPayment> checkPayments) {
			this.checkPayments = checkPayments;
			fireTableDataChanged();
		}
		
		@Override
		public int getRowCount() {
			return checkPayments.size();
		}

		@Override
		public int getColumnCount() {
			return columnNames.length;
		}

		@Override
		public Object getValueAt(int rowIndex, int columnIndex) {
			PaymentCheckPayment checkPayment = checkPayments.get(rowIndex);
			switch (columnIndex) {
			case CUSTOMER_COLUMN_INDEX:
				return checkPayment.getParent().getCustomer().getName();
			case BANK_COLUMN_INDEX:
				return checkPayment.getBank();
			case CHECK_NUMBER_COLUMN_INDEX:
				return checkPayment.getCheckNumber();
			case CHECK_DATE_COLUMN_INDEX:
				return FormatterUtil.formatDate(checkPayment.getCheckDate());
			case AMOUNT_COLUMN_INDEX:
				return FormatterUtil.formatAmount(checkPayment.getAmount());
			case PAYMENT_TERMINAL_COLUMN_INDEX:
				return checkPayment.getParent().getPaymentTerminal().getName();
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
				return String.class;
			}
		}
		
		@Override
		public String getColumnName(int column) {
			return columnNames[column];
		}
		
	}
	
}