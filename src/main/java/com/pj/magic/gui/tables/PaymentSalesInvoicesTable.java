package com.pj.magic.gui.tables;

import java.math.BigDecimal;
import java.util.ArrayList;

import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.AbstractTableModel;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.pj.magic.gui.component.MagicCellEditor;
import com.pj.magic.gui.component.MagicTextField;
import com.pj.magic.gui.tables.models.PaymentSalesInvoicesTableModel;
import com.pj.magic.model.Payment;
import com.pj.magic.model.PaymentSalesInvoice;
import com.pj.magic.util.NumberUtil;


@Component
public class PaymentSalesInvoicesTable extends MagicTable {

	public static final int SALES_INVOICE_NUMBER_COLUMN_INDEX = 0;
	public static final int NET_AMOUNT_COLUMN_INDEX = 1;
	public static final int ADJUSTMENT_AMOUNT_COLUMN_INDEX = 2;
	public static final int AMOUNT_DUE_COLUMN_INDEX = 3;
	
	@Autowired private PaymentSalesInvoicesTableModel tableModel;
	
	private Payment payment;
	
	@Autowired
	public PaymentSalesInvoicesTable(PaymentSalesInvoicesTableModel tableModel) {
		super(tableModel);
		initializeColumns();
		initializeModelListener();
	}
	
	private void initializeModelListener() {
		getModel().addTableModelListener(new TableModelListener() {
			
			@Override
			public void tableChanged(TableModelEvent e) {
				final AbstractTableModel model = (AbstractTableModel)e.getSource();
				final int row = e.getFirstRow();
				final int column = e.getColumn();
				
				SwingUtilities.invokeLater(new Runnable() {
					
					@Override
					public void run() {
						switch (column) {
						case ADJUSTMENT_AMOUNT_COLUMN_INDEX:
							model.fireTableRowsUpdated(row, row);
							break;
						}
					}
				});
			}
		});
	}

	private void initializeColumns() {
		MagicTextField adjustmentAmountTextField = new MagicTextField();
		adjustmentAmountTextField.setMaximumLength(13);
		
		columnModel.getColumn(ADJUSTMENT_AMOUNT_COLUMN_INDEX)
			.setCellEditor(new AmountCellEditor(adjustmentAmountTextField));
	}

	public void setPayment(Payment payment) {
		this.payment = payment;
		tableModel.setPaymentSalesInvoices(payment.getSalesInvoices());
	}

	public void clearDisplay() {
		tableModel.setPaymentSalesInvoices(new ArrayList<PaymentSalesInvoice>());
	}
	
	private class AmountCellEditor extends MagicCellEditor {
		
		public AmountCellEditor(JTextField textField) {
			super(textField);
		}
		
		@Override
		public boolean stopCellEditing() {
			String amount = ((JTextField)getComponent()).getText();
			boolean valid = false;
			if (StringUtils.isEmpty(amount)) {
				valid = true;
			} else if (!NumberUtil.isAmount(amount)){
				showErrorMessage("Adj. Amount must be a valid amount");
			} else if (NumberUtil.toBigDecimal(amount).equals(BigDecimal.ZERO.setScale(2))){
				showErrorMessage("Adj. Amount must not be 0");
			} else {
				valid = true;
			}
			return (valid) ? super.stopCellEditing() : false;
		}
		
	}

	public void removeCurrentlySelectedItem() {
		if (getSelectedRow() != -1) {
			if (confirm("Do you wish to delete the selected item?")) {
				doDeleteCurrentlySelectedItem();
			}
		}
	}

	private void doDeleteCurrentlySelectedItem() {
		int selectedRowIndex = getSelectedRow();
		PaymentSalesInvoice paymentSalesInvoice = tableModel.getPaymentSalesInvoice(selectedRowIndex);
		clearSelection();
		payment.getSalesInvoices().remove(paymentSalesInvoice);
		tableModel.removeItem(paymentSalesInvoice);
		
		if (tableModel.hasItems()) {
			if (selectedRowIndex == getModel().getRowCount()) {
				changeSelection(selectedRowIndex - 1, 0, false, false);
			} else {
				changeSelection(selectedRowIndex, 0, false, false);
			}
		}
	}
	
}