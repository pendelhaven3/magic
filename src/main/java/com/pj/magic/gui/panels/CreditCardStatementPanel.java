package com.pj.magic.gui.panels;

import java.awt.BorderLayout;
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

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.pj.magic.Constants;
import com.pj.magic.exception.NotEnoughSurplusPaymentException;
import com.pj.magic.gui.component.DatePickerFormatter;
import com.pj.magic.gui.component.MagicToolBar;
import com.pj.magic.gui.component.MagicToolBarButton;
import com.pj.magic.gui.dialog.MagicDialog;
import com.pj.magic.gui.tables.MagicListTable;
import com.pj.magic.gui.tables.models.ListBackedTableModel;
import com.pj.magic.model.CreditCardStatement;
import com.pj.magic.model.CreditCardStatementItem;
import com.pj.magic.model.PurchasePaymentCreditCardPayment;
import com.pj.magic.service.CreditCardService;
import com.pj.magic.util.ComponentUtil;
import com.pj.magic.util.FormatterUtil;

import net.sourceforge.jdatepicker.impl.JDatePanelImpl;
import net.sourceforge.jdatepicker.impl.JDatePickerImpl;
import net.sourceforge.jdatepicker.impl.UtilCalendarModel;

@Component
public class CreditCardStatementPanel extends StandardMagicPanel {

	private static final Logger logger = LoggerFactory.getLogger(CreditCardStatementPanel.class);

	private static final int PURCHASE_PAYMENT_COLUMN_INDEX = 0;
	private static final int SUPPLIER_COLUMN_INDEX = 1;
	private static final int AMOUNT_COLUMN_INDEX = 2;
	private static final int TRANSACTION_DATE_COLUMN_INDEX = 3;
	private static final int PAID_COLUMN_INDEX = 4;
	private static final int PAID_DATE_COLUMN_INDEX = 5;
	private static final int SELECT_COLUMN_INDEX = 6;
	
	@Autowired private CreditCardService creditCardService;
	
	private CreditCardStatement statement;
	private JLabel creditCardField;
	private JLabel statementDateField;
	private JLabel statusField;
	private MagicListTable table;
	private CreditCardStatementItemsTableModel tableModel;
	private JLabel totalItemsField;
	private JLabel totalAmountField;
	private JButton postButton;
	private JButton addItemButton;
	private JButton deleteItemButton;
	private JButton markCheckedAsPaidButton;
	private JButton markCheckedAsUnpaidButton;
	private JButton checkAllButton;
	private SelectPaidDateDialog selectPaidDateDialog;
	private JLabel surplusPaymentLabel;
	
	@Override
	protected void initializeComponents() {
		initializeTable();
		focusOnComponentWhenThisPanelIsDisplayed(table);
		
		markCheckedAsPaidButton = new JButton("Mark Checked As Paid");
		markCheckedAsPaidButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				markCheckedAsPaid();
			}
		});
		
		markCheckedAsUnpaidButton = new JButton("Mark Checked As Unpaid");
		markCheckedAsUnpaidButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				markCheckedAsUnpaid();
			}
		});
		
		checkAllButton = new JButton("Check All");
		checkAllButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				tableModel.selectAll();
			}
		});

		selectPaidDateDialog = new SelectPaidDateDialog();
	}

	private void markCheckedAsPaid() {
		BigDecimal totalPaidAmount = getTotalAmount(tableModel.getSelectedItems());
		BigDecimal totalSurplusPayments = creditCardService.getSurplusPayment(statement.getCreditCard());
		
		if (totalPaidAmount.compareTo(totalSurplusPayments) > 0) {
			showErrorMessage("Total amount of items greater than surplus payments");
			return;
		}
		
		if (!confirm("Mark items as paid?")) {
			return;
		}
		
		selectPaidDateDialog.updateDisplay();
		selectPaidDateDialog.setVisible(true);
		
		Date paidDate = selectPaidDateDialog.getPaidDate();
		if (paidDate != null) {
			try {
				creditCardService.markAsPaid(tableModel.getSelectedItems(), paidDate);
			} catch (NotEnoughSurplusPaymentException e) {
				showErrorMessage("Total amount of items greater than surplus payments");
				return;
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
				showMessageForUnexpectedError();
				return;
			}
			
			showMessage("Items marked as paid!");
			updateDisplay(statement);
		}
	}

	private void markCheckedAsUnpaid() {
		if (!confirm("Mark items as unpaid?")) {
			return;
		}
		
		try {
			creditCardService.markAsUnpaid(tableModel.getSelectedItems());
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			showMessageForUnexpectedError();
			return;
		}
		
		showMessage("Items marked as unpaid!");
		updateDisplay(statement);
	}

	private void initializeTable() {
		tableModel = new CreditCardStatementItemsTableModel();
		table = new MagicListTable(tableModel);
	}

	@Override
	protected void registerKeyBindings() {
	}
	
	@Override
	protected void doOnBack() {
		getMagicFrame().switchToCreditCardStatementListPanel();
	}
	
	public void updateDisplay(CreditCardStatement statement) {
		creditCardField.setText(statement.getCreditCard().toString());
		statementDateField.setText(FormatterUtil.formatDate(statement.getStatementDate()));
		statusField.setText(statement.getStatus());
		
		this.statement = statement = creditCardService.getCreditCardStatement(statement.getId());
		
		tableModel.setItems(statement.getItems());
		updateTotalFields(statement.getItems());
		
		statusField.setText(statement.getStatus());
		postButton.setEnabled(!statement.isPosted());
		addItemButton.setEnabled(!statement.isPosted());
		deleteItemButton.setEnabled(!statement.isPosted());
		checkAllButton.setEnabled(statement.isPosted());
		markCheckedAsPaidButton.setEnabled(statement.isPosted());
		markCheckedAsUnpaidButton.setEnabled(statement.isPosted());
		surplusPaymentLabel.setText(
				FormatterUtil.formatAmount(creditCardService.getSurplusPayment(statement.getCreditCard())));
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
		mainPanel.add(ComponentUtil.createLabel(100, "Credit Card:"), c);
		
		c = new GridBagConstraints();
		c.gridx = 2;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		creditCardField = ComponentUtil.createLabel(200, "");
		mainPanel.add(creditCardField, c);

		c = new GridBagConstraints();
		c.gridx = 3;
		c.gridy = currentRow;
		mainPanel.add(Box.createHorizontalStrut(50), c);

		c = new GridBagConstraints();
		c.gridx = 4;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(ComponentUtil.createLabel(140, "Surplus Payment:"), c);

		c = new GridBagConstraints();
		c.weightx = 1.0;
		c.gridx = 5;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		surplusPaymentLabel = ComponentUtil.createLabel(200, "");
		mainPanel.add(surplusPaymentLabel, c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(ComponentUtil.createLabel(140, "Statement Date:"), c);
		
		c = new GridBagConstraints();
		c.gridx = 2;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		statementDateField = ComponentUtil.createLabel(200, "");
		mainPanel.add(statementDateField, c);

		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(ComponentUtil.createLabel(120, "Status:"), c);
		
		c = new GridBagConstraints();
		c.gridx = 2;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		statusField = ComponentUtil.createLabel(200, "");
		mainPanel.add(statusField, c);

		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = currentRow;
		mainPanel.add(Box.createVerticalStrut(10), c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = currentRow;
		c.gridwidth = 6;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(createItemsTableToolBar(), c);

		currentRow++;
		
		c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;
		c.weightx = c.weighty = 1.0;
		c.gridx = 0;
		c.gridy = currentRow;
		c.gridwidth = 6;
		JScrollPane itemsTableScrollPane = new JScrollPane(table);
		itemsTableScrollPane.setPreferredSize(new Dimension(600, 100));
		mainPanel.add(itemsTableScrollPane, c);

		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = currentRow;
		mainPanel.add(Box.createVerticalStrut(5), c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = currentRow;
		c.gridwidth = 6;
		checkAllButton.setPreferredSize(new Dimension(130, 30));
		markCheckedAsPaidButton.setPreferredSize(new Dimension(200, 30));
		markCheckedAsUnpaidButton.setPreferredSize(new Dimension(220, 30));
		mainPanel.add(ComponentUtil.createGenericPanel(
				checkAllButton,
				markCheckedAsPaidButton,
				markCheckedAsUnpaidButton), c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = currentRow;
		c.gridwidth = 6;
		mainPanel.add(createTotalsPanel(), c);
	}
	
	private JPanel createTotalsPanel() {
		JPanel panel = new JPanel();
		panel.setLayout(new GridBagLayout());
		
		int currentRow = 0;
		
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		panel.add(ComponentUtil.createLabel(100, "Total Items:"), c);
		
		c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		totalItemsField = ComponentUtil.createLabel(60);
		panel.add(totalItemsField, c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		panel.add(ComponentUtil.createLabel(120, "Total Amount:"), c);
		
		c = new GridBagConstraints();
		c.weightx = 1.0;
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		totalAmountField = ComponentUtil.createLabel(100);
		panel.add(totalAmountField, c);
		
		return panel;
	}
	
	private JPanel createItemsTableToolBar() {
		JPanel panel = new JPanel();
		
		addItemButton = new MagicToolBarButton("plus_small", "Add Item", true);
		addItemButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
//				table.switchToAddMode();
			}
		});
		panel.add(addItemButton, BorderLayout.WEST);
		
		deleteItemButton = new MagicToolBarButton("minus_small", "Delete Item", true);
		deleteItemButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				deleteCurrentlySelectedItem();
			}
		});
		panel.add(deleteItemButton, BorderLayout.WEST);
		
		return panel;
	}

	private void deleteCurrentlySelectedItem() {
		if (statement.isPosted()) {
			return;
		}
		
		if (table.hasNoSelectedRow()) {
			return;
		}
		
		if (confirm("Remove currently selected item?")) {
			try {
				doDeleteCurrentlySelectedItem();
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
				showMessageForUnexpectedError();
				return;
			}
			
			showMessage("Item deleted");
			updateTotalFields(tableModel.getItems());
		}
	}

	private void doDeleteCurrentlySelectedItem() {
		int selectedRowIndex = table.getSelectedRow();
		table.clearSelection(); // clear row selection so model listeners will not cause exceptions while model items are being updated
		tableModel.removeItem(selectedRowIndex);
		
		if (tableModel.hasItems()) {
			if (selectedRowIndex == tableModel.getRowCount()) {
				table.changeSelection(selectedRowIndex - 1, 0, false, false);
			} else {
				table.changeSelection(selectedRowIndex, 0, false, false);
			}
		}
	}

	private void updateTotalFields(List<CreditCardStatementItem> items) {
		totalItemsField.setText(String.valueOf(items.size()));
		totalAmountField.setText(FormatterUtil.formatAmount(getTotalAmount(items)));
	}

	private static BigDecimal getTotalAmount(List<CreditCardStatementItem> items) {
		BigDecimal total = Constants.ZERO;
		for (CreditCardStatementItem item : items) {
			total = total.add(item.getCreditCardPayment().getAmount());
		}
		return total;
	}

	@Override
	protected void addToolBarButtons(MagicToolBar toolBar) {
		postButton = new MagicToolBarButton("post", "Post");
		postButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				postCreditCardStatement();
			}
		});
		toolBar.add(postButton);
	}

	private void postCreditCardStatement() {
		if (!confirm("Do you want to post this credit card statement?")) {
			return;
		}
		
		statement.setPosted(true);
		try {
			creditCardService.save(statement);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			showMessageForUnexpectedError();
			return;
		}
		
		showMessage("Credit card statement posted!");
		updateDisplay(statement);
	}

	private class CreditCardStatementItemsTableModel extends ListBackedTableModel<CreditCardStatementItem> {

		private final String[] columnNames = 
			{"PP No.", "Supplier", "Amount", "Transaction Date", "Paid", "Paid Date", ""};
		
		private List<Integer> selected = new ArrayList<>();
		
		@Override
		protected String[] getColumnNames() {
			return columnNames;
		}
		
		public void removeItem(int rowIndex) {
			CreditCardStatementItem item = getItems().remove(rowIndex);
			creditCardService.delete(item);
			
			updateSelectedAfterRowDelete(rowIndex);
			
			fireTableDataChanged();
		}

		private void updateSelectedAfterRowDelete(int deletedRow) {
			List<Integer> tempSelected = new ArrayList<>(selected);
			selected.clear();
			
			for (Integer row : tempSelected) {
				if (row.intValue() > deletedRow) {
					selected.add(row - 1);
				} else if (row.intValue() < deletedRow) {
					selected.add(row);
				}
			}
		}

		@Override
		public Object getValueAt(int rowIndex, int columnIndex) {
			CreditCardStatementItem item = getItem(rowIndex);
			PurchasePaymentCreditCardPayment payment = item.getCreditCardPayment();
			switch (columnIndex) {
			case PURCHASE_PAYMENT_COLUMN_INDEX:
				return payment.getParent().getPurchasePaymentNumber();
			case SUPPLIER_COLUMN_INDEX:
				return payment.getParent().getSupplier().getName();
			case AMOUNT_COLUMN_INDEX:
				return FormatterUtil.formatAmount(payment.getAmount());
			case TRANSACTION_DATE_COLUMN_INDEX:
				return FormatterUtil.formatDate(payment.getTransactionDate());
			case PAID_COLUMN_INDEX:
				return item.isPaid() ? "Yes" : "No";
			case PAID_DATE_COLUMN_INDEX:
				if (item.getPaidDate() != null) {
					return FormatterUtil.formatDate(item.getPaidDate());
				} else {
					return null;
				}
			case SELECT_COLUMN_INDEX:
				return selected.contains(rowIndex);
			default:
				throw new RuntimeException("Fetching invalid column index: " + columnIndex);
			}
		}
		
		@Override
		public void setItems(List<CreditCardStatementItem> items) {
			super.setItems(items);
			selected.clear();
		}
		
		@Override
		public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
			switch (columnIndex) {
			case SELECT_COLUMN_INDEX:
				if (selected.contains(rowIndex)) {
					selected.remove(selected.indexOf(rowIndex));
				} else {
					selected.add(rowIndex);
				}
			}
		}
		
		@Override
		public boolean isCellEditable(int rowIndex, int columnIndex) {
			return columnIndex == SELECT_COLUMN_INDEX;
		}
		
		public List<CreditCardStatementItem> getSelectedItems() {
			List<CreditCardStatementItem> selectedItems = new ArrayList<>();
			for (Integer i : selected) {
				selectedItems.add(getItem(i));
			}
			return selectedItems;
		}
		
		@Override
		public Class<?> getColumnClass(int columnIndex) {
			switch (columnIndex) {
			case AMOUNT_COLUMN_INDEX:
				return Number.class;
			case SELECT_COLUMN_INDEX:
				return Boolean.class;
			default:
				return Object.class;
			}
		}
		
		public void selectAll() {
			if (getItems().isEmpty()) {
				return;
			}
			
			selected.clear();
			for (int i = 0; i < getItems().size(); i++) {
				selected.add(i);
			}
			fireTableRowsUpdated(0, getItems().size() - 1);
		}

	}
	
	private class SelectPaidDateDialog extends MagicDialog {

		private UtilCalendarModel paidDateModel;
		private JButton saveButton;
		
		public SelectPaidDateDialog() {
			setSize(400, 135);
			setLocationRelativeTo(null);
			setTitle("Select Paid Date");
			getRootPane().setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
			
			initializeComponents();
			layoutComponents();
			
			addWindowListener(new WindowAdapter() {
				
				@Override
				public void windowClosing(WindowEvent e) {
					paidDateModel.setValue(null);
				}
			});
		}

		public void updateDisplay() {
			paidDateModel.setValue(null);
		}

		private void initializeComponents() {
			paidDateModel = new UtilCalendarModel();
			
			saveButton = new JButton("Save");
			saveButton.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					if (paidDateModel.getValue() == null) {
						showErrorMessage("Paid Date must be specified");
					} else {
						setVisible(false);
					}
				}
			});
		}

		@Override
		protected void doWhenEscapeKeyPressed() {
			paidDateModel.setValue(null);
		}
		
		private void layoutComponents() {
			setLayout(new GridBagLayout());
			int currentRow = 0;

			GridBagConstraints c = new GridBagConstraints();
			c.gridx = 0;
			c.gridy = currentRow;
			c.anchor = GridBagConstraints.WEST;
			add(ComponentUtil.createLabel(100, "Paid Date:"), c);

			c = new GridBagConstraints();
			c.gridx = 1;
			c.gridy = currentRow;
			c.anchor = GridBagConstraints.WEST;
			
			JDatePanelImpl datePanel = new JDatePanelImpl(paidDateModel);
			JDatePickerImpl datePicker = new JDatePickerImpl(datePanel, new DatePickerFormatter());
			add(datePicker, c);
			
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
		
		public Date getPaidDate() {
			if (paidDateModel.getValue() != null) {
				return paidDateModel.getValue().getTime();
			} else {
				return null;
			}
		}
		
	}
	
}