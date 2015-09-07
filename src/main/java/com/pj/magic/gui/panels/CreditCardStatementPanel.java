package com.pj.magic.gui.panels;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

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

import com.pj.magic.gui.component.MagicToolBar;
import com.pj.magic.gui.component.MagicToolBarButton;
import com.pj.magic.gui.tables.MagicListTable;
import com.pj.magic.model.CreditCardStatement;
import com.pj.magic.model.CreditCardStatementItem;
import com.pj.magic.model.PurchasePaymentCreditCardPayment;
import com.pj.magic.service.CreditCardService;
import com.pj.magic.util.ComponentUtil;
import com.pj.magic.util.FormatterUtil;

@Component
public class CreditCardStatementPanel extends StandardMagicPanel {

	private static final Logger logger = LoggerFactory.getLogger(CreditCardStatementPanel.class);

	private static final int PURCHASE_PAYMENT_COLUMN_INDEX = 0;
	private static final int SUPPLIER_COLUMN_INDEX = 1;
	private static final int AMOUNT_COLUMN_INDEX = 2;
	private static final int TRANSACTION_DATE_COLUMN_INDEX = 3;
	
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
	
	@Override
	protected void initializeComponents() {
		initializeTable();
		
		focusOnComponentWhenThisPanelIsDisplayed(table);
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
		if (statement.getId() == null) {
			this.statement = statement;
			clearDisplay();
			return;
		}
		
		this.statement = statement = creditCardService.getCreditCardStatement(statement.getId());
		
		creditCardField.setText(statement.getCreditCard().toString());
		statementDateField.setText(FormatterUtil.formatDate(statement.getStatementDate()));
//		statusField.setText("None");
//		totalItemsField.setText("-");
//		totalAmountField.setText("-");
//		postButton.setEnabled(!statement.isPosted());
//		addItemButton.setEnabled(!statement.isPosted());
//		deleteItemButton.setEnabled(!statement.isPosted());
	}

	private void clearDisplay() {
		statementDateField.setText(null);
		statusField.setText(null);
		totalItemsField.setText(null);
		totalAmountField.setText(null);
		postButton.setEnabled(false);
		addItemButton.setEnabled(false);
		deleteItemButton.setEnabled(false);
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
		c.weightx = 1.0;
		c.gridx = 2;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		creditCardField = ComponentUtil.createLabel(200, "");
		mainPanel.add(creditCardField, c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(ComponentUtil.createLabel(120, "Statement Date:"), c);
		
		c = new GridBagConstraints();
		c.weightx = 1.0;
		c.gridx = 2;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		statementDateField = ComponentUtil.createLabel(200, "");
		mainPanel.add(statementDateField, c);

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
		c.gridwidth = 3;
		JScrollPane itemsTableScrollPane = new JScrollPane(table);
		itemsTableScrollPane.setPreferredSize(new Dimension(600, 100));
		mainPanel.add(itemsTableScrollPane, c);

		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = currentRow;
		c.gridwidth = 3;
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
//				table.removeCurrentlySelectedItem();
			}
		});
		panel.add(deleteItemButton, BorderLayout.WEST);
		
		return panel;
	}

	@Override
	protected void addToolBarButtons(MagicToolBar toolBar) {
		postButton = new MagicToolBarButton("post", "Post");
		postButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
//				postAdjustmentIn();
			}
		});
		toolBar.add(postButton);
	}

	private class CreditCardStatementItemsTableModel extends AbstractTableModel {

		private final String[] columnNames = {"PP No.", "Supplier", "Amount", "Transaction Date"};
		
		private List<CreditCardStatementItem> items = new ArrayList<>();
		
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
			PurchasePaymentCreditCardPayment payment = items.get(rowIndex).getCreditCardPayment();
			switch (columnIndex) {
			case PURCHASE_PAYMENT_COLUMN_INDEX:
				return payment.getParent().getPurchasePaymentNumber();
			case SUPPLIER_COLUMN_INDEX:
				return payment.getParent().getSupplier().getName();
			case AMOUNT_COLUMN_INDEX:
				return FormatterUtil.formatAmount(payment.getAmount());
			case TRANSACTION_DATE_COLUMN_INDEX:
				return FormatterUtil.formatDate(payment.getTransactionDate());
			default:
				throw new RuntimeException("Fetching invalid column index: " + columnIndex);
			}
		}
		
	}
	
}