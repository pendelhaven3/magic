package com.pj.magic.gui.panels;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Box;
import javax.swing.JButton;
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
import com.pj.magic.model.PurchasePaymentCreditCardPayment;
import com.pj.magic.service.PurchasePaymentService;
import com.pj.magic.util.FormatterUtil;

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
	
	private MagicListTable table;
	private CreditCardPaymentsTableModel tableModel;
	private JButton markButton;
	private JButton markToolBarButton;
	
	@Override
	protected void initializeComponents() {
		initializeTable();
		
		markButton = new JButton("Mark Credit Card Payments");
		markButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				markCreditCardPayments();
			}
		});
		
		focusOnComponentWhenThisPanelIsDisplayed(table);
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
		tableModel.setCreditCardPayments(purchasePaymentService.getAllUnmarkedCreditCardPayments());
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
		c.fill = GridBagConstraints.BOTH;
		c.weightx = c.weighty = 1.0;
		c.gridx = 0;
		c.gridy = currentRow;
		mainPanel.add(new JScrollPane(table), c);

		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = currentRow;
		mainPanel.add(Box.createVerticalStrut(5), c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.weightx = 1.0;
		c.gridx = 0;
		c.gridy = currentRow;
		markButton.setPreferredSize(new Dimension(230, 30));
		mainPanel.add(markButton, c);
	}

	@Override
	protected void addToolBarButtons(MagicToolBar toolBar) {
		markToolBarButton = new MagicToolBarButton("post", "Mark Sales Invoices");
		markToolBarButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				markCreditCardPayments();
			}
		});
		
		toolBar.add(markToolBarButton);
	}

	private void markCreditCardPayments() {
//		if (confirm("Mark Sales Invoices?")) {
//			try {
//				salesInvoiceService.markSalesInvoices(table.getSalesInvoices());
//				showMessage("Sales Invoices Marked");
//				updateDisplay();
//			} catch (Exception e) {
//				logger.error(e.getMessage(), e);
//				showMessage("Error on updating records");
//			}
//		}
	}

	private class CreditCardPaymentsTableModel extends AbstractTableModel {

		private final String[] COLUMN_NAMES =
				{"PP No.", "Supplier", "Amount", "Credit Card", "Transaction Date", "Mark"};
		
		private List<PurchasePaymentCreditCardPayment> creditCardPayments = new ArrayList<>();
		
		public void setCreditCardPayments(List<PurchasePaymentCreditCardPayment> creditCardPayments) {
			this.creditCardPayments = creditCardPayments;
			fireTableDataChanged();
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
		
	}
	
}