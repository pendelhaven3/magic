package com.pj.magic.gui.panels;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.table.AbstractTableModel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.pj.magic.Constants;
import com.pj.magic.gui.component.DatePickerFormatter;
import com.pj.magic.gui.component.EllipsisButton;
import com.pj.magic.gui.component.MagicComboBox;
import com.pj.magic.gui.component.MagicTextField;
import com.pj.magic.gui.component.MagicToolBar;
import com.pj.magic.gui.dialog.MagicDialog;
import com.pj.magic.gui.dialog.SelectSupplierDialog;
import com.pj.magic.gui.tables.MagicListTable;
import com.pj.magic.gui.tables.MarkSalesInvoicesTable;
import com.pj.magic.model.CreditCard;
import com.pj.magic.model.PurchasePaymentCreditCardPayment;
import com.pj.magic.model.Supplier;
import com.pj.magic.model.search.PurchasePaymentCreditCardPaymentSearchCriteria;
import com.pj.magic.service.CreditCardService;
import com.pj.magic.service.PurchasePaymentService;
import com.pj.magic.service.SupplierService;
import com.pj.magic.util.ComponentUtil;
import com.pj.magic.util.FormatterUtil;
import com.pj.magic.util.ListUtil;

import net.sourceforge.jdatepicker.impl.JDatePanelImpl;
import net.sourceforge.jdatepicker.impl.JDatePickerImpl;
import net.sourceforge.jdatepicker.impl.UtilCalendarModel;

@Component
public class MarkCreditCardPaymentsPanel extends StandardMagicPanel {

	private static final Logger logger = LoggerFactory.getLogger(MarkCreditCardPaymentsPanel.class);

	private static final int PURCHASE_PAYMENT_NUMBER_COLUMN_INDEX = 0;
	private static final int SUPPLIER_COLUMN_INDEX = 1;
	private static final int AMOUNT_COLUMN_INDEX = 2;
	private static final int CREDIT_CARD_COLUMN_INDEX = 3;
	private static final int TRANSACTION_DATE_COLUMN_INDEX = 4;
	private static final int MARK_COLUMN_INDEX = 5;
	
	@Autowired private PurchasePaymentService purchasePaymentService;
	@Autowired private CreditCardService creditCardService;
	@Autowired private SupplierService supplierService;
	@Autowired private SelectSupplierDialog selectSupplierDialog;
	
	private MagicListTable table;
	private CreditCardPaymentsTableModel tableModel;
	private SelectStatementDateDialog selectStatementDateDialog;
	private JButton markSelectedButton;
	private JButton markAllButton;
	private UtilCalendarModel fromDateModel;
	private UtilCalendarModel toDateModel;
	private JButton searchButton;
	private MagicTextField supplierCodeField;
	private JLabel supplierNameLabel;
	private EllipsisButton selectSupplierButton;
	private MagicComboBox<CreditCard> creditCardComboBox;
	private JLabel totalRowsLabel = new JLabel();
	private JLabel totalAmountLabel= new JLabel();
	private JLabel totalMarkedRowsLabel = new JLabel();
	private JLabel totalMarkedAmountLabel= new JLabel();
	
	@Override
	protected void initializeComponents() {
		initializeTable();
		
		markSelectedButton = new JButton("Mark Selected");
		markSelectedButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				markSelectedCreditCardPayments();
			}
		});
		
		markAllButton = new JButton("Mark All");
		markAllButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				markAllCreditCardPayments();
			}
		});
		
		selectStatementDateDialog = new SelectStatementDateDialog();
		
		fromDateModel = new UtilCalendarModel();
		toDateModel = new UtilCalendarModel();
		
		searchButton = new JButton("Search");
		searchButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				searchCreditCardPayments();
			}
		});
		
		supplierCodeField = new MagicTextField();
		supplierNameLabel = new JLabel();
		
		selectSupplierButton = new EllipsisButton("Select Supplier");
		selectSupplierButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				openSelectSupplierDialog();
			}
		});
		
		creditCardComboBox = new MagicComboBox<>();
	}

	private void markAllCreditCardPayments() {
		for (PurchasePaymentCreditCardPayment creditCardPayment : tableModel.getCreditCardPayments()) {
			creditCardPayment.setMarked(true);
		}
		tableModel.fireTableRowsUpdated(0, tableModel.getCreditCardPayments().size() - 1);
		updateMarkedTotalsFields();
		
		markSelectedCreditCardPayments();
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

	private void searchCreditCardPayments() {
		PurchasePaymentCreditCardPaymentSearchCriteria criteria = 
				new PurchasePaymentCreditCardPaymentSearchCriteria();
		criteria.setMarked(false);
		
		if (fromDateModel.getValue() != null) {
			criteria.setFromDate(fromDateModel.getValue().getTime());
		}
		
		if (toDateModel.getValue() != null) {
			criteria.setToDate(toDateModel.getValue().getTime());
		}
		
		String supplierCode = supplierCodeField.getText();
		if (!StringUtils.isEmpty(supplierCode)) {
			criteria.setSupplier(supplierService.findSupplierByCode(supplierCode));
		}
		
		criteria.setCreditCard((CreditCard)creditCardComboBox.getSelectedItem());
		
		List<PurchasePaymentCreditCardPayment> creditCardPayments = 
				purchasePaymentService.searchCreditCardPayments(criteria);
		tableModel.setCreditCardPayments(creditCardPayments);
		updateTotalFields(creditCardPayments);
	}

	private void initializeTable() {
		tableModel = new CreditCardPaymentsTableModel();
		table = new MagicListTable(tableModel);
	}

	@Override
	protected void doOnBack() {
		getMagicFrame().switchToPurchasePaymentsMenuPanel();
	}
	
	public void updateDisplay() {
		List<PurchasePaymentCreditCardPayment> creditCardPayments = 
				purchasePaymentService.getAllUnmarkedCreditCardPayments();
		tableModel.setCreditCardPayments(creditCardPayments);
		updateTotalFields(creditCardPayments);
		
		creditCardComboBox.setModel(ListUtil.toDefaultComboBoxModel(creditCardService.getAllCreditCards(), true));
		creditCardComboBox.setSelectedIndex(0);
	}

	private void updateTotalFields(List<PurchasePaymentCreditCardPayment> creditCardPayments) {
		totalRowsLabel.setText(String.valueOf(creditCardPayments.size()));
		totalAmountLabel.setText(FormatterUtil.formatAmount(getTotalAmount(creditCardPayments)));
		totalMarkedRowsLabel.setText("0");
		totalMarkedAmountLabel.setText("0");
	}

	private static BigDecimal getTotalAmount(List<PurchasePaymentCreditCardPayment> creditCardPayments) {
		BigDecimal total = Constants.ZERO;
		for (PurchasePaymentCreditCardPayment creditCardPayment : creditCardPayments) {
			total = total.add(creditCardPayment.getAmount());
		}
		return total;
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
		mainPanel.add(searchButton, c);
		
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
		c.gridwidth = 3;
		mainPanel.add(createSupplierPanel(), c);

		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(ComponentUtil.createLabel(120, "Credit Card:"), c);
		
		c = new GridBagConstraints();
		c.gridx = 2;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		c.gridwidth = 3;
		creditCardComboBox.setPreferredSize(new Dimension(200, 25));
		mainPanel.add(creditCardComboBox, c);

		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = currentRow;
		mainPanel.add(Box.createVerticalStrut(10), c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = currentRow;
		c.gridwidth = 5;
		mainPanel.add(createTotalsPanel(), c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = currentRow;
		mainPanel.add(Box.createVerticalStrut(10), c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;
		c.weighty = 1.0;
		c.gridx = 0;
		c.gridy = currentRow;
		c.gridwidth = 5;
		
		JScrollPane scrollPane = new JScrollPane(table);
		scrollPane.setPreferredSize(new Dimension(400, 200));
		mainPanel.add(scrollPane, c);

		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = currentRow;
		mainPanel.add(Box.createVerticalStrut(5), c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = currentRow;
		c.gridwidth = 5;
		markSelectedButton.setPreferredSize(new Dimension(150, 30));
		markAllButton.setPreferredSize(new Dimension(150, 30));
		mainPanel.add(ComponentUtil.createGenericPanel(
				markSelectedButton, Box.createHorizontalStrut(5), markAllButton), c);
	}

	private JPanel createTotalsPanel() {
		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new GridBagLayout());
		
		int currentRow = 0;

		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(ComponentUtil.createLabel(100, "Total Rows:"), c);
		
		c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		totalRowsLabel = ComponentUtil.createRightLabel(120, "");
		mainPanel.add(totalRowsLabel, c);
		
		c = new GridBagConstraints();
		c.gridx = 2;
		c.gridy = currentRow;
		mainPanel.add(Box.createHorizontalStrut(100), c);
		
		c = new GridBagConstraints();
		c.gridx = 3;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(ComponentUtil.createLabel(150, "Total Marked Rows:"), c);
		
		c = new GridBagConstraints();
		c.gridx = 4;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		totalMarkedRowsLabel = ComponentUtil.createRightLabel(120, "");
		mainPanel.add(totalMarkedRowsLabel, c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(ComponentUtil.createLabel(100, "Total Amount:"), c);
		
		c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		totalAmountLabel = ComponentUtil.createRightLabel(120, "");
		mainPanel.add(totalAmountLabel, c);
		
		c = new GridBagConstraints();
		c.gridx = 3;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(ComponentUtil.createLabel(160, "Total Marked Amount:"), c);
		
		c = new GridBagConstraints();
		c.gridx = 4;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		totalMarkedAmountLabel = ComponentUtil.createRightLabel(120, "");
		mainPanel.add(totalMarkedAmountLabel, c);
		
		return mainPanel;
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

	@Override
	protected void addToolBarButtons(MagicToolBar toolBar) {
	}

	private void markSelectedCreditCardPayments() {
		if (!tableModel.hasMarked()) {
			showErrorMessage("At least one row must be selected");
			return;
		}
		
		selectStatementDateDialog.updateDisplay();
		selectStatementDateDialog.setVisible(true);

		Date statementDate = selectStatementDateDialog.getStatementDate();
		if (statementDate != null) {
			try {
				purchasePaymentService.markCreditCardPayments(tableModel.getCreditCardPayments(), statementDate);
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
				showMessageForUnexpectedError();
				return;
			}
			showMessage("Credit card payments marked!");
			searchCreditCardPayments();
		}		
	}

	public void updateMarkedTotalsFields() {
		totalMarkedRowsLabel.setText(String.valueOf(getTotalMarked(tableModel.getCreditCardPayments())));
		totalMarkedAmountLabel.setText(
				FormatterUtil.formatAmount(getTotalMarkedAmount(tableModel.getCreditCardPayments())));
	}
	
	private static int getTotalMarked(List<PurchasePaymentCreditCardPayment> creditCardPayments) {
		int total = 0;
		for (PurchasePaymentCreditCardPayment creditCardPayment : creditCardPayments) {
			if (creditCardPayment.isMarked()) {
				total++;
			}
		}
		return total;
	}

	private static BigDecimal getTotalMarkedAmount(List<PurchasePaymentCreditCardPayment> creditCardPayments) {
		BigDecimal total = Constants.ZERO;
		for (PurchasePaymentCreditCardPayment creditCardPayment : creditCardPayments) {
			if (creditCardPayment.isMarked()) {
				total = total.add(creditCardPayment.getAmount());
			}
		}
		return total;
	}

	private class CreditCardPaymentsTableModel extends AbstractTableModel {

		private final String[] COLUMN_NAMES =
				{"PP No.", "Supplier", "Amount", "Credit Card", "Transaction Date", "Mark"};
		
		private List<PurchasePaymentCreditCardPayment> creditCardPayments = new ArrayList<>();
		
		public void setCreditCardPayments(List<PurchasePaymentCreditCardPayment> creditCardPayments) {
			this.creditCardPayments = creditCardPayments;
			fireTableDataChanged();
		}
		
		public boolean hasMarked() {
			for (PurchasePaymentCreditCardPayment creditCardPayment : creditCardPayments) {
				if (creditCardPayment.isMarked()) {
					return true;
				}
			}
			return false;
		}

		public List<PurchasePaymentCreditCardPayment> getCreditCardPayments() {
			return creditCardPayments;
		}
		
		@Override
		public int getRowCount() {
			return creditCardPayments.size();
		}

		@Override
		public int getColumnCount() {
			return COLUMN_NAMES.length;
		}

		@Override
		public String getColumnName(int column) {
			return COLUMN_NAMES[column];
		}
		
		@Override
		public Object getValueAt(int rowIndex, int columnIndex) {
			PurchasePaymentCreditCardPayment creditCardPayment =
					creditCardPayments.get(rowIndex);
			switch (columnIndex) {
			case PURCHASE_PAYMENT_NUMBER_COLUMN_INDEX:
				return creditCardPayment.getParent().getPurchasePaymentNumber();
			case SUPPLIER_COLUMN_INDEX:
				return creditCardPayment.getParent().getSupplier().getName();
			case AMOUNT_COLUMN_INDEX:
				return FormatterUtil.formatAmount(creditCardPayment.getAmount());
			case CREDIT_CARD_COLUMN_INDEX:
				return creditCardPayment.getCreditCard();
			case TRANSACTION_DATE_COLUMN_INDEX:
				return FormatterUtil.formatDate(creditCardPayment.getTransactionDate());
			case MARK_COLUMN_INDEX:
				return creditCardPayment.isMarked();
			default:
				throw new RuntimeException("Fetching invalid column index: " + columnIndex);
			}
		}
		
		@Override
		public Class<?> getColumnClass(int columnIndex) {
			switch (columnIndex) {
			case AMOUNT_COLUMN_INDEX:
				return Number.class;
			case MARK_COLUMN_INDEX:
				return Boolean.class;
			default:
				return Object.class;
			}
		}
		
		@Override
		public boolean isCellEditable(int rowIndex, int columnIndex) {
			return columnIndex == MarkSalesInvoicesTable.MARK_COLUMN_INDEX;
		}
		
		@Override
		public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
			PurchasePaymentCreditCardPayment creditCardPayment = creditCardPayments.get(rowIndex);
			switch (columnIndex) {
			case MARK_COLUMN_INDEX:
				creditCardPayment.setMarked(!creditCardPayment.isMarked());
				break;
			default:
				throw new RuntimeException("Setting invalid column index: " + columnIndex);
			}
			fireTableCellUpdated(rowIndex, MARK_COLUMN_INDEX);
			updateMarkedTotalsFields();
		}
		
	}
	
	private class SelectStatementDateDialog extends MagicDialog {

		private UtilCalendarModel statementDateModel;
		private JButton saveButton;
		
		public SelectStatementDateDialog() {
			setSize(400, 135);
			setLocationRelativeTo(null);
			setTitle("Select Statement Date");
			getRootPane().setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
			
			initializeComponents();
			layoutComponents();
			
			addWindowListener(new WindowAdapter() {
				
				@Override
				public void windowClosing(WindowEvent e) {
					statementDateModel.setValue(null);
				}
			});
		}

		public void updateDisplay() {
			statementDateModel.setValue(null);
		}

		private void initializeComponents() {
			statementDateModel = new UtilCalendarModel();
			
			saveButton = new JButton("Save");
			saveButton.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					if (statementDateModel.getValue() == null) {
						showErrorMessage("Statement Date must be specified");
					} else {
						setVisible(false);
					}
				}
			});
		}

		@Override
		protected void doWhenEscapeKeyPressed() {
			statementDateModel.setValue(null);
		}
		
		private void layoutComponents() {
			setLayout(new GridBagLayout());
			int currentRow = 0;

			GridBagConstraints c = new GridBagConstraints();
			c.gridx = 0;
			c.gridy = currentRow;
			c.anchor = GridBagConstraints.WEST;
			add(ComponentUtil.createLabel(130, "Statement Date:"), c);

			c = new GridBagConstraints();
			c.gridx = 1;
			c.gridy = currentRow;
			c.anchor = GridBagConstraints.WEST;
			
			JDatePanelImpl statementDatePanel = new JDatePanelImpl(statementDateModel);
			JDatePickerImpl statementDatePicker = new JDatePickerImpl(statementDatePanel, new DatePickerFormatter());
			add(statementDatePicker, c);
			
			currentRow++;
			
			c = new GridBagConstraints();
			c.gridx = 0;
			c.gridy = currentRow;
			c.anchor = GridBagConstraints.CENTER;
			add(Box.createVerticalStrut(20), c);
			
			currentRow++;
			
			c = new GridBagConstraints();
			c.gridx = 0;
			c.gridy = currentRow;
			c.gridwidth = 2;
			c.anchor = GridBagConstraints.CENTER;
			saveButton.setPreferredSize(new Dimension(100, 25));
			add(saveButton, c);
			
			currentRow++;
			
			c = new GridBagConstraints();
			c.weighty = 1.0; // bottom space filler
			c.gridx = 0;
			c.gridy = currentRow;
			add(Box.createGlue(), c);
		}
		
		public Date getStatementDate() {
			if (statementDateModel.getValue() != null) {
				return statementDateModel.getValue().getTime();
			} else {
				return null;
			}
		}
		
	}

}