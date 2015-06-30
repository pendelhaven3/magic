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

import javax.swing.AbstractAction;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.table.AbstractTableModel;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.pj.magic.Constants;
import com.pj.magic.gui.component.DatePickerFormatter;
import com.pj.magic.gui.component.EllipsisButton;
import com.pj.magic.gui.component.MagicTextField;
import com.pj.magic.gui.component.MagicToolBar;
import com.pj.magic.gui.component.MagicToolBarButton;
import com.pj.magic.gui.dialog.SelectSupplierDialog;
import com.pj.magic.gui.tables.MagicListTable;
import com.pj.magic.model.PurchasePaymentBankTransfer;
import com.pj.magic.model.Supplier;
import com.pj.magic.model.report.PurchasePaymentBankTransfersReport;
import com.pj.magic.model.search.PurchasePaymentBankTransferSearchCriteria;
import com.pj.magic.service.PurchasePaymentService;
import com.pj.magic.service.SupplierService;
import com.pj.magic.util.ComponentUtil;
import com.pj.magic.util.FormatterUtil;

import net.sourceforge.jdatepicker.impl.JDatePanelImpl;
import net.sourceforge.jdatepicker.impl.JDatePickerImpl;
import net.sourceforge.jdatepicker.impl.UtilCalendarModel;

@Component
public class PurchasePaymentBankTransfersReportPanel extends StandardMagicPanel {

	private static final int PURCHASE_PAYMENT_NUMBER_COLUMN_INDEX = 0;
	private static final int SUPPLIER_COLUMN_INDEX = 1;
	private static final int BANK_COLUMN_INDEX = 2;
	private static final int AMOUNT_COLUMN_INDEX = 3;
	private static final int TRANSFER_DATE_COLUMN_INDEX = 4;
	private static final int REFERENCE_NUMBER_COLUMN_INDEX = 5;
	
	@Autowired private PurchasePaymentService purchasePaymentService;
	@Autowired private SelectSupplierDialog selectSupplierDialog;
	@Autowired private SupplierService supplierService;
	
	private MagicListTable table;
	private BankTransfersTableModel tableModel;
	private UtilCalendarModel fromDateModel;
	private UtilCalendarModel toDateModel;
	private JButton generateButton;
	private JLabel totalBankTransfersField = new JLabel();
	private MagicTextField supplierCodeField;
	private JLabel supplierNameLabel;
	private EllipsisButton selectSupplierButton;
	
	@Override
	protected void initializeComponents() {
		fromDateModel = new UtilCalendarModel();
		toDateModel = new UtilCalendarModel();
		
		supplierCodeField = new MagicTextField();
		supplierCodeField.setMaximumLength(Constants.SUPPLIER_CODE_MAXIMUM_LENGTH);
		
		supplierNameLabel = new JLabel();
		
		selectSupplierButton = new EllipsisButton("Select Supplier (F5)");
		selectSupplierButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				openSelectSupplierDialog();
			}
		});
		
		generateButton = new JButton("Generate");
		generateButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				generateReport();
			}
		});
		
		initializeTable();
		focusOnComponentWhenThisPanelIsDisplayed(table);
	}

	private void openSelectSupplierDialog() {
		selectSupplierDialog.searchSuppliers(supplierCodeField.getText());
		selectSupplierDialog.setVisible(true);
		
		Supplier supplier = selectSupplierDialog.getSelectedSupplier();
		if (supplier != null) {
			supplierCodeField.setText(supplier.getCode());
			supplierNameLabel.setText(supplier.getName());
		} else {
			supplierNameLabel.setText(null);
		}
	}

	private void generateReport() {
		if (fromDateModel.getValue() == null) {
			showErrorMessage("From Date must be specified");
			return;
		}
		if (toDateModel.getValue() == null) {
			showErrorMessage("To Date must be specified");
			return;
		}
		
		PurchasePaymentBankTransfersReport report = doGenerateReport();
		tableModel.setBankTransfers(report.getBankTransfers());
		if (!report.getBankTransfers().isEmpty()) {
			table.changeSelection(0, 0);
		}
		totalBankTransfersField.setText(FormatterUtil.formatAmount(report.getTotalAmount()));
		
		if (report.getSupplier() != null) {
			supplierCodeField.setText(report.getSupplier().getCode());
			supplierNameLabel.setText(report.getSupplier().getName());
		} else {
			supplierCodeField.setText(null);
			supplierNameLabel.setText(null);
		}
	}

	private PurchasePaymentBankTransfersReport doGenerateReport() {
		PurchasePaymentBankTransfersReport report = new PurchasePaymentBankTransfersReport();
		report.setFromDate(fromDateModel.getValue().getTime());
		report.setToDate(toDateModel.getValue().getTime());
		
		String supplierCode = supplierCodeField.getText();
		if (!StringUtils.isEmpty(supplierCode)) {
			report.setSupplier(supplierService.findSupplierByCode(supplierCode));
		}
		
		PurchasePaymentBankTransferSearchCriteria criteria = new PurchasePaymentBankTransferSearchCriteria();
		criteria.setPosted(true);
		criteria.setFromDate(report.getFromDate());
		criteria.setToDate(report.getToDate());
		criteria.setSupplier(report.getSupplier());
		report.setBankTransfers(purchasePaymentService.searchBankTransfers(criteria));
		
		return report;
	}

	private void initializeTable() {
		tableModel = new BankTransfersTableModel();
		table = new MagicListTable(tableModel);
	}

	@Override
	protected void doOnBack() {
		getMagicFrame().switchToReportsMenuPanel();
	}
	
	public void updateDisplay() {
		fromDateModel.setValue(Calendar.getInstance());
		toDateModel.setValue(Calendar.getInstance());
		supplierCodeField.setText(null);
		supplierNameLabel.setText(null);
		totalBankTransfersField.setText("-");
		
		List<PurchasePaymentBankTransfer> bankTransfers = Collections.emptyList();
		tableModel.setBankTransfers(bankTransfers);
	}

	@Override
	protected void registerKeyBindings() {
		supplierCodeField.onF5Key(new AbstractAction() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				openSelectSupplierDialog();
			}
		});
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
		mainPanel.add(ComponentUtil.createLabel(120, "From Date:"), c);
		
		c = new GridBagConstraints();
		c.gridx = 2;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		
		JDatePanelImpl fromDatePanel = new JDatePanelImpl(fromDateModel);
		JDatePickerImpl fromDatePicker = new JDatePickerImpl(fromDatePanel, new DatePickerFormatter());
		mainPanel.add(fromDatePicker, c);

		c = new GridBagConstraints();
		c.gridx = 3;
		c.gridy = currentRow;
		mainPanel.add(Box.createHorizontalStrut(30), c);

		c = new GridBagConstraints();
		c.weightx = 1.0;
		c.gridx = 4;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(generateButton, c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(ComponentUtil.createLabel(120, "To Date:"), c);
		
		c = new GridBagConstraints();
		c.gridx = 2;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		
		JDatePanelImpl toDatePanel = new JDatePanelImpl(toDateModel);
		JDatePickerImpl toDatePicker = new JDatePickerImpl(toDatePanel, new DatePickerFormatter());
		mainPanel.add(toDatePicker, c);

		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(ComponentUtil.createLabel(120, "Supplier:"), c);
		
		c = new GridBagConstraints();
		c.gridx = 2;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		c.gridwidth = 4;
		mainPanel.add(createSupplierPanel(), c);

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
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = currentRow;
		c.gridwidth = 5;
		c.anchor = GridBagConstraints.EAST;
		mainPanel.add(createTotalsPanel(), c);
	}

	private JPanel createSupplierPanel() {
		JPanel panel = new JPanel();
		panel.setLayout(new GridBagLayout());
		
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		c.anchor = GridBagConstraints.WEST;
		supplierCodeField.setPreferredSize(new Dimension(100, 25));
		panel.add(supplierCodeField, c);
		
		c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = 0;
		c.anchor = GridBagConstraints.WEST;
		selectSupplierButton.setPreferredSize(new Dimension(30, 25));
		panel.add(selectSupplierButton, c);
		
		c = new GridBagConstraints();
		c.gridx = 2;
		c.gridy = 0;
		c.anchor = GridBagConstraints.WEST;
		panel.add(Box.createHorizontalStrut(10), c);
		
		c = new GridBagConstraints();
		c.gridx = 3;
		c.gridy = 0;
		c.anchor = GridBagConstraints.WEST;
		supplierNameLabel.setPreferredSize(new Dimension(300, 25));
		panel.add(supplierNameLabel, c);
		
		return panel;
	}

	private JPanel createTotalsPanel() {
		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new GridBagLayout());
		
		int currentRow = 0;

		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(ComponentUtil.createLabel(150, "Total Bank Transfers:"), c);
		
		c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		totalBankTransfersField = ComponentUtil.createRightLabel(120, "");
		mainPanel.add(totalBankTransfersField, c);
		
		c = new GridBagConstraints();
		c.gridx = 2;
		c.gridy = currentRow;
		mainPanel.add(Box.createHorizontalStrut(10), c);
		
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
//		toolBar.add(printPreviewButton);
		
		JButton printButton = new MagicToolBarButton("print", "Print");
		printButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				print();
			}
		});
//		toolBar.add(printButton);
	}

	private void print() {
//		if (fromDateModel.getValue() == null) {
//			showErrorMessage("Date must be specified");
//			return;
//		}
//		
//		printService.print(doGenerateReport());
	}

	private void printPreview() {
//		if (fromDateModel.getValue() == null) {
//			showErrorMessage("Date must be specified");
//			return;
//		}
//		
//		RemittanceReport report = doGenerateReport();
//		printPreviewDialog.updateDisplay(printService.generateReportAsString(report));
//		printPreviewDialog.setColumnsPerLine(PrintServiceImpl.REMITTANCE_REPORT_CHARACTERS_PER_LINE);
//		printPreviewDialog.setUseCondensedFontForPrinting(true);
//		printPreviewDialog.setVisible(true);
	}

	private class BankTransfersTableModel extends AbstractTableModel {

		private final String[] columnNames = 
			{"PP No.", "Supplier", "Bank", "Amount", "Transfer Date", "Reference Number"};
		
		private List<PurchasePaymentBankTransfer> bankTransfers = new ArrayList<>();
		
		public void setBankTransfers(List<PurchasePaymentBankTransfer> bankTransfers) {
			this.bankTransfers = bankTransfers;
			fireTableDataChanged();
		}
		
		@Override
		public int getRowCount() {
			return bankTransfers.size();
		}

		@Override
		public int getColumnCount() {
			return columnNames.length;
		}

		@Override
		public Object getValueAt(int rowIndex, int columnIndex) {
			PurchasePaymentBankTransfer bankTransfer = bankTransfers.get(rowIndex);
			switch (columnIndex) {
			case PURCHASE_PAYMENT_NUMBER_COLUMN_INDEX:
				return bankTransfer.getParent().getPurchasePaymentNumber();
			case SUPPLIER_COLUMN_INDEX:
				return bankTransfer.getParent().getSupplier().getName();
			case BANK_COLUMN_INDEX:
				return bankTransfer.getBank();
			case AMOUNT_COLUMN_INDEX:
				return FormatterUtil.formatAmount(bankTransfer.getAmount());
			case TRANSFER_DATE_COLUMN_INDEX:
				return FormatterUtil.formatDate(bankTransfer.getTransferDate());
			case REFERENCE_NUMBER_COLUMN_INDEX:
				return bankTransfer.getReferenceNumber();
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