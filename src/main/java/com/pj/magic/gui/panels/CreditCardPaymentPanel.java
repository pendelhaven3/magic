package com.pj.magic.gui.panels;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigDecimal;
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

import com.pj.magic.Constants;
import com.pj.magic.gui.component.MagicToolBar;
import com.pj.magic.gui.component.MagicToolBarButton;
import com.pj.magic.gui.dialog.AddCreditCardPaymentDialog;
import com.pj.magic.gui.tables.MagicListTable;
import com.pj.magic.model.CreditCard;
import com.pj.magic.model.CreditCardPayment;
import com.pj.magic.model.CreditCardStatementItem;
import com.pj.magic.model.PurchasePaymentCreditCardPayment;
import com.pj.magic.service.CreditCardService;
import com.pj.magic.util.ComponentUtil;
import com.pj.magic.util.FormatterUtil;

@Component
public class CreditCardPaymentPanel extends StandardMagicPanel {

	private static final int PURCHASE_PAYMENT_NUMBER_COLUMN_INDEX = 0;
	private static final int SUPPLIER_COLUMN_INDEX = 1;
	private static final int TOTAL_AMOUNT_COLUMN_INDEX = 2;
	private static final int TRANSACTION_DATE_COLUMN_INDEX = 3;

	private static final int PAYMENT_DATE_COLUMN_INDEX = 0;
	private static final int AMOUNT_COLUMN_INDEX = 1;
	private static final int REMARKS_COLUMN_INDEX = 2;
	
	@Autowired private CreditCardService creditCardService;
	@Autowired private AddCreditCardPaymentDialog addCreditCardPaymentDialog;
	
	private JLabel creditCardLabel;
	private MagicListTable itemsTable;
	private CreditCardStatementItemsTableModel itemsTableModel;
	private MagicListTable paymentsTable;
	private CreditCardPaymentsTableModel paymentsTableModel;
	private JTabbedPane tabbedPane;
	private JLabel totalPurchasesLabel;
	private JLabel totalPaymentsLabel;
	private JLabel outstandingBalanceLabel;
	private CreditCard creditCard;
	
	@Override
	protected void initializeComponents() {
		creditCardLabel = new JLabel();
		
		initializeTables();
	}
	
	private void initializeTables() {
		itemsTableModel = new CreditCardStatementItemsTableModel();
		itemsTable = new MagicListTable(itemsTableModel);
		
		paymentsTableModel = new CreditCardPaymentsTableModel();
		paymentsTable = new MagicListTable(paymentsTableModel);
	}
	
	public void updateDisplay(CreditCard creditCard) {
		this.creditCard = creditCard = creditCardService.getCreditCard(creditCard.getId());
		creditCardLabel.setText(creditCard.toString());
		
		List<CreditCardPayment> payments = creditCardService.getCreditCardPayments(creditCard);
		paymentsTableModel.setPayments(payments);
		
		totalPurchasesLabel.setText(FormatterUtil.formatAmount(Constants.ZERO));
		totalPaymentsLabel.setText(FormatterUtil.formatAmount(getTotalAmount(payments)));
		outstandingBalanceLabel.setText(FormatterUtil.formatAmount(Constants.ZERO));
	}

	private static BigDecimal getTotalAmount(List<CreditCardPayment> payments) {
		BigDecimal total = Constants.ZERO;
		for (CreditCardPayment payment : payments) {
			total = total.add(payment.getAmount());
		}
		return total;
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
		creditCardLabel = ComponentUtil.createLabel(200);
		mainPanel.add(creditCardLabel, c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.gridy = currentRow;
		mainPanel.add(Box.createVerticalStrut(20), c);
		
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
		
		currentRow++;

		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = currentRow;
		c.gridwidth = 3;
		mainPanel.add(createTotalsPanel(), c);
	}

	private JPanel createTotalsPanel() {
		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new GridBagLayout());
		
		int currentRow = 0;

		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(ComponentUtil.createLabel(150, "Total Purchases:"), c);
		
		c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		totalPurchasesLabel = ComponentUtil.createRightLabel(120);
		mainPanel.add(totalPurchasesLabel, c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(ComponentUtil.createLabel(150, "Total Payments:"), c);
		
		c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		totalPaymentsLabel = ComponentUtil.createRightLabel(120, "");
		mainPanel.add(totalPaymentsLabel, c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(ComponentUtil.createLabel(150, "Outstanding Balance:"), c);
		
		c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		outstandingBalanceLabel = ComponentUtil.createRightLabel(120, "");
		mainPanel.add(outstandingBalanceLabel, c);
		
		return mainPanel;
	}

	@Override
	protected void addToolBarButtons(MagicToolBar toolBar) {
	}

	@Override
	protected void registerKeyBindings() {
	}

	@Override
	protected void doOnBack() {
		getMagicFrame().switchToCreditCardPaymentListPanel();
	}

	private JTabbedPane createTabbedPane() {
		JTabbedPane tabbedPane = new JTabbedPane();
		tabbedPane.addTab("Statements", createStatementsPanel());
		tabbedPane.addTab("Payments", createPaymentsPanel());
		return tabbedPane;
	}
	
	private JPanel createPaymentsPanel() {
		JPanel panel = new JPanel();
		panel.setLayout(new GridBagLayout());
		
		int currentRow = 0;
		
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		panel.add(createPaymentsTableToolBar(), c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;
		c.weightx = c.weighty = 1.0;
		c.gridx = 0;
		c.gridy = currentRow;
		JScrollPane scrollPane = new JScrollPane(paymentsTable);
		scrollPane.setPreferredSize(new Dimension(600, 150));
		panel.add(scrollPane, c);
		
		return panel;
	}

	private JPanel createStatementsPanel() {
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

	private JPanel createPaymentsTableToolBar() {
		JPanel panel = new JPanel();
		
		MagicToolBarButton addPaymentButton = new MagicToolBarButton("plus_small", "Add Payment", true);
		addPaymentButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				addPayment();
			}
		});
		panel.add(addPaymentButton, BorderLayout.WEST);
		
		MagicToolBarButton deletePaymentButton = new MagicToolBarButton("minus_small", "Delete Payment", true);
		deletePaymentButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				deletePayment();
			}
		});
		panel.add(deletePaymentButton, BorderLayout.WEST);
		
		return panel;
	}
	
	private void deletePayment() {
	}

	private void addPayment() {
		CreditCardPayment payment = new CreditCardPayment();
		payment.setCreditCard(creditCard);
		
		addCreditCardPaymentDialog.updateDisplay(payment);
		addCreditCardPaymentDialog.setVisible(true);
		updateDisplay(creditCard);
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
			case TOTAL_AMOUNT_COLUMN_INDEX:
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
			case TOTAL_AMOUNT_COLUMN_INDEX:
				return Number.class;
			default:
				return Object.class;
			}
		}
		
	}

	private class CreditCardPaymentsTableModel extends AbstractTableModel {

		private final String[] columnNames = {"Payment Date", "Amount", "Remarks"};
		
		private List<CreditCardPayment> payments = new ArrayList<>();
		
		public void setPayments(List<CreditCardPayment> payments) {
			this.payments = payments;
			fireTableDataChanged();
		}
		
		@Override
		public int getRowCount() {
			return payments.size();
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
			CreditCardPayment payment = payments.get(rowIndex);
			switch (columnIndex) {
			case PAYMENT_DATE_COLUMN_INDEX:
				return FormatterUtil.formatDate(payment.getPaymentDate());
			case AMOUNT_COLUMN_INDEX:
				return FormatterUtil.formatAmount(payment.getAmount());
			case REMARKS_COLUMN_INDEX:
				return payment.getRemarks();
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