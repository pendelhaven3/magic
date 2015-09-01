package com.pj.magic.gui.panels;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.table.AbstractTableModel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.pj.magic.gui.component.DatePickerFormatter;
import com.pj.magic.gui.component.MagicToolBar;
import com.pj.magic.gui.dialog.MagicDialog;
import com.pj.magic.gui.tables.MagicListTable;
import com.pj.magic.gui.tables.MarkSalesInvoicesTable;
import com.pj.magic.model.PurchasePaymentCreditCardPayment;
import com.pj.magic.service.PurchasePaymentService;
import com.pj.magic.util.ComponentUtil;
import com.pj.magic.util.FormatterUtil;

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
	
	private MagicListTable table;
	private CreditCardPaymentsTableModel tableModel;
	private SelectStatementDateDialog selectStatementDateDialog;
	private JButton markButton;
	
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
		
		selectStatementDateDialog = new SelectStatementDateDialog();
		
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
	}

	private void markCreditCardPayments() {
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
			updateDisplay();
		}		
	}

	private class CreditCardPaymentsTableModel extends AbstractTableModel {

		private final String[] COLUMN_NAMES =
				{"PP No.", "Supplier", "Amount", "Credit Card", "Transaction Date", "Mark"};
		
		private List<PurchasePaymentCreditCardPayment> creditCardPayments = new ArrayList<>();
		
		public void setCreditCardPayments(List<PurchasePaymentCreditCardPayment> creditCardPayments) {
			this.creditCardPayments = creditCardPayments;
			fireTableDataChanged();
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