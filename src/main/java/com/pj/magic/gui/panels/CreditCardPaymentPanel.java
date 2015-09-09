package com.pj.magic.gui.panels;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigDecimal;
import java.util.List;

import javax.swing.Box;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.pj.magic.Constants;
import com.pj.magic.gui.component.CustomAction;
import com.pj.magic.gui.component.MagicToolBar;
import com.pj.magic.gui.component.MagicToolBarButton;
import com.pj.magic.gui.dialog.AddCreditCardPaymentDialog;
import com.pj.magic.gui.tables.MagicListTable;
import com.pj.magic.gui.tables.models.ListBackedTableModel;
import com.pj.magic.model.CreditCard;
import com.pj.magic.model.CreditCardPayment;
import com.pj.magic.model.CreditCardStatement;
import com.pj.magic.service.CreditCardService;
import com.pj.magic.util.ComponentUtil;
import com.pj.magic.util.FormatterUtil;

@Component
public class CreditCardPaymentPanel extends StandardMagicPanel {

	private static final Logger logger = LoggerFactory.getLogger(CreditCardPaymentPanel.class);
	
	private static final int STATEMENT_DATE_COLUMN_INDEX = 0;
	private static final int TOTAL_UNPAID_AMOUNT_COLUMN_INDEX = 1;

	private static final int PAYMENT_DATE_COLUMN_INDEX = 0;
	private static final int AMOUNT_COLUMN_INDEX = 1;
	private static final int REMARKS_COLUMN_INDEX = 2;
	
	@Autowired private CreditCardService creditCardService;
	@Autowired private AddCreditCardPaymentDialog addCreditCardPaymentDialog;
	
	private JLabel creditCardLabel;
	private MagicListTable statementsTable;
	private CreditCardStatementsTableModel statementsTableModel;
	private MagicListTable paymentsTable;
	private CreditCardPaymentsTableModel paymentsTableModel;
	private JTabbedPane tabbedPane;
	private JLabel totalUnpaidAmountLabel;
	private JLabel totalSurplusPaymentsLabel;
	private CreditCard creditCard;
	
	@Override
	protected void initializeComponents() {
		creditCardLabel = new JLabel();
		
		initializeTables();
	}
	
	private void initializeTables() {
		statementsTableModel = new CreditCardStatementsTableModel();
		statementsTable = new MagicListTable(statementsTableModel);
		
		paymentsTableModel = new CreditCardPaymentsTableModel();
		paymentsTable = new MagicListTable(paymentsTableModel);
	}
	
	public void updateDisplay(CreditCard creditCard) {
		this.creditCard = creditCard = creditCardService.getCreditCard(creditCard.getId());
		creditCardLabel.setText(creditCard.toString());
		
		List<CreditCardStatement> statements = creditCardService.findAllStatementsByCreditCard(creditCard);
		statementsTableModel.setItems(statements);
		
		List<CreditCardPayment> payments = creditCardService.getCreditCardPayments(creditCard);
		paymentsTableModel.setItems(payments);
		
		totalUnpaidAmountLabel.setText(FormatterUtil.formatAmount(getTotalUnpaidAmount(statements)));
		totalSurplusPaymentsLabel.setText(FormatterUtil.formatAmount(
				creditCardService.getSurplusPayment(creditCard)));
		
		tabbedPane.setSelectedIndex(1);
	}

	private static BigDecimal getTotalUnpaidAmount(List<CreditCardStatement> statements) {
		BigDecimal total = Constants.ZERO;
		for (CreditCardStatement statement : statements) {
			total = total.add(statement.getTotalUnpaidAmount());
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
		mainPanel.add(ComponentUtil.createLabel(150, "Total Unpaid Amount:"), c);
		
		c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		totalUnpaidAmountLabel = ComponentUtil.createRightLabel(120);
		mainPanel.add(totalUnpaidAmountLabel, c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(ComponentUtil.createLabel(180, "Total Surplus Payments:"), c);
		
		c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		totalSurplusPaymentsLabel = ComponentUtil.createRightLabel(120, "");
		mainPanel.add(totalSurplusPaymentsLabel, c);
		
		return mainPanel;
	}

	@Override
	protected void addToolBarButtons(MagicToolBar toolBar) {
	}

	@Override
	protected void registerKeyBindings() {
		paymentsTable.onDoubleClick(new CustomAction() {
			
			@Override
			public void doAction() {
				editPayment();
			}
		});
		
	}

	private void editPayment() {
		CreditCardPayment payment = paymentsTableModel.getItem(paymentsTable.getSelectedRow());
		
		addCreditCardPaymentDialog.updateDisplay(payment);
		addCreditCardPaymentDialog.setVisible(true);
		updateDisplay(creditCard);
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
		JScrollPane scrollPane = new JScrollPane(statementsTable);
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
		if (paymentsTable.hasNoSelectedRow()) {
			return;
		}
		
		CreditCardPayment payment = paymentsTableModel.getItem(paymentsTable.getSelectedRow());
		if (willDeletingPaymentResultInNegativeSurplus(payment)) {
			showErrorMessage("Deleting payment cannot result in negative surplus payment");
			return;
		}
		
		if (confirm("Remove currently selected item?")) {
			try {
				doDeletePayment();
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
				showMessageForUnexpectedError();
				return;
			}
			showMessage("Item deleted");
		}
	}

	private boolean willDeletingPaymentResultInNegativeSurplus(CreditCardPayment payment) {
		BigDecimal surplusPayment = creditCardService.getSurplusPayment(payment.getCreditCard());
		return surplusPayment.subtract(payment.getAmount()).compareTo(Constants.ZERO) == -1;
	}

	private void doDeletePayment() {
		int selectedRowIndex = paymentsTable.getSelectedRow();
		paymentsTable.clearSelection();
		paymentsTableModel.removeItem(selectedRowIndex);
		
		if (paymentsTableModel.hasItems()) {
			if (selectedRowIndex == paymentsTableModel.getRowCount()) {
				paymentsTable.changeSelection(selectedRowIndex - 1, 0, false, false);
			} else {
				paymentsTable.changeSelection(selectedRowIndex, 0, false, false);
			}
		}
	}
	
	private void addPayment() {
		CreditCardPayment payment = new CreditCardPayment();
		payment.setCreditCard(creditCard);
		
		addCreditCardPaymentDialog.updateDisplay(payment);
		addCreditCardPaymentDialog.setVisible(true);
		updateDisplay(creditCard);
	}

	private class CreditCardStatementsTableModel extends ListBackedTableModel<CreditCardStatement> {

		private final String[] columnNames = {"Statement Date", "Unpaid Amount"};
		
		@Override
		public Object getValueAt(int rowIndex, int columnIndex) {
			CreditCardStatement statement = getItem(rowIndex);
			switch (columnIndex) {
			case STATEMENT_DATE_COLUMN_INDEX:
				return FormatterUtil.formatDate(statement.getStatementDate());
			case TOTAL_UNPAID_AMOUNT_COLUMN_INDEX:
				return FormatterUtil.formatAmount(statement.getTotalUnpaidAmount());
			default:
				throw new RuntimeException("Fetching invalid column index: " + columnIndex);
			}
		}
		
		@Override
		public Class<?> getColumnClass(int columnIndex) {
			switch (columnIndex) {
			case TOTAL_UNPAID_AMOUNT_COLUMN_INDEX:
				return Number.class;
			default:
				return Object.class;
			}
		}

		@Override
		protected String[] getColumnNames() {
			return columnNames;
		}
		
	}

	private class CreditCardPaymentsTableModel extends ListBackedTableModel<CreditCardPayment> {

		private final String[] columnNames = {"Payment Date", "Amount", "Remarks"};
		
		@Override
		public Object getValueAt(int rowIndex, int columnIndex) {
			CreditCardPayment payment = getItem(rowIndex);
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
		
		public void removeItem(int rowIndex) {
			CreditCardPayment item = getItems().remove(rowIndex);
			creditCardService.delete(item);
			fireTableDataChanged();
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
		protected String[] getColumnNames() {
			return columnNames;
		}
		
	}
	
}