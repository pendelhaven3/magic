package com.pj.magic.gui.panels;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Box;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.table.AbstractTableModel;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.pj.magic.gui.component.MagicToolBar;
import com.pj.magic.gui.tables.MagicListTable;
import com.pj.magic.model.CreditCardStatement;
import com.pj.magic.model.CreditCardStatementItem;
import com.pj.magic.model.PurchasePaymentCreditCardPayment;
import com.pj.magic.service.CreditCardService;
import com.pj.magic.util.ComponentUtil;
import com.pj.magic.util.FormatterUtil;

@Component
public class CreditCardStatementPanel extends StandardMagicPanel {

	private static final int PURCHASE_PAYMENT_NUMBER_COLUMN_INDEX = 0;
	private static final int SUPPLIER_COLUMN_INDEX = 1;
	private static final int AMOUNT_COLUMN_INDEX = 2;
	private static final int TRANSACTION_DATE_COLUMN_INDEX = 3;

	@Autowired private CreditCardService creditCardService;
	
	private CreditCardStatement statement;
	private JLabel statementNumberLabel;
	private JLabel creditCardLabel;
	private JLabel statementDateLabel;
	private MagicListTable itemsTable;
	private CreditCardStatementItemsTableModel itemsTableModel;
	private JTabbedPane tabbedPane;
	
	@Override
	protected void initializeComponents() {
		statementNumberLabel = new JLabel();
		creditCardLabel = new JLabel();
		statementDateLabel = new JLabel();
		
		initializeTables();
	}
	
	private void initializeTables() {
		itemsTableModel = new CreditCardStatementItemsTableModel();
		itemsTable = new MagicListTable(itemsTableModel);
	}
	
	public void updateDisplay(CreditCardStatement statement) {
		this.statement = statement = creditCardService.getCreditCardStatement(statement.getId());
		
		statementNumberLabel.setText(statement.getStatementNumber().toString());
		creditCardLabel.setText(statement.getCreditCard().toString());
		statementDateLabel.setText(FormatterUtil.formatDate(statement.getStatementDate()));
		
		itemsTableModel.setItems(statement.getItems());
		
		tabbedPane.setEnabled(true);
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
		mainPanel.add(ComponentUtil.createLabel(120, "Statement No.:"), c);
		
		c = new GridBagConstraints();
		c.gridx = 2;
		c.gridy = currentRow;
		c.weightx = 1.0;
		c.anchor = GridBagConstraints.WEST;
		statementNumberLabel = ComponentUtil.createLabel(100);
		mainPanel.add(statementNumberLabel, c);
		
		currentRow++;
		
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
		creditCardLabel = ComponentUtil.createLabel(200);
		mainPanel.add(creditCardLabel, c);
		
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
		mainPanel.add(statementDateLabel, c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.gridy = currentRow;
		mainPanel.add(Box.createVerticalStrut(30), c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;
		c.weightx = c.weighty = 1.0;
		c.gridx = 0;
		c.gridy = currentRow;
		c.gridwidth = 3;
		
		tabbedPane = createTabbedPane();
		tabbedPane.setPreferredSize(new Dimension(600, 250));
		mainPanel.add(tabbedPane, c);
	}

	@Override
	protected void addToolBarButtons(MagicToolBar toolBar) {
	}

	@Override
	protected void registerKeyBindings() {
	}

	@Override
	protected void doOnBack() {
		getMagicFrame().switchToCreditCardStatementListPanel();
	}

	private JTabbedPane createTabbedPane() {
		JTabbedPane tabbedPane = new JTabbedPane();
		tabbedPane.addTab("Items", createItemsPanel());
		tabbedPane.addTab("Payments", new JPanel());
		return tabbedPane;
	}
	
	private JPanel createItemsPanel() {
		JPanel panel = new JPanel();
		panel.setLayout(new GridBagLayout());
		
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;
		c.weightx = c.weighty = 1.0;
		JScrollPane scrollPane = new JScrollPane(itemsTable);
		scrollPane.setPreferredSize(new Dimension(600, 150));
		panel.add(scrollPane, c);
		
		return panel;
	}

	private class CreditCardStatementItemsTableModel extends AbstractTableModel {

		private final String[] columnNames = {"PP No.", "Supplier", "Amount", "Transaction Date"};
		
		private List<CreditCardStatementItem> items = new ArrayList<>();
		
		public void setItems(List<CreditCardStatementItem> items) {
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
		public String getColumnName(int column) {
			return columnNames[column];
		}
		
		@Override
		public Object getValueAt(int rowIndex, int columnIndex) {
			PurchasePaymentCreditCardPayment creditCardPayment = items.get(rowIndex).getCreditCardPayment();
			switch (columnIndex) {
			case PURCHASE_PAYMENT_NUMBER_COLUMN_INDEX:
				return creditCardPayment.getParent().getPurchasePaymentNumber();
			case SUPPLIER_COLUMN_INDEX:
				return creditCardPayment.getParent().getSupplier().getName();
			case AMOUNT_COLUMN_INDEX:
				return FormatterUtil.formatAmount(creditCardPayment.getAmount());
			case TRANSACTION_DATE_COLUMN_INDEX:
				return FormatterUtil.formatDate(creditCardPayment.getTransactionDate());
			default:
				throw new RuntimeException("Fetching invalid column index: " + columnIndex);
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
		
	}

}