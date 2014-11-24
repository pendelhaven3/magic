package com.pj.magic.gui.tables.models;

import java.util.ArrayList;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.pj.magic.gui.tables.PaymentSalesInvoicesTable;
import com.pj.magic.model.PaymentSalesInvoice;
import com.pj.magic.service.PaymentService;
import com.pj.magic.util.FormatterUtil;
import com.pj.magic.util.NumberUtil;

@Component
public class PaymentSalesInvoicesTableModel extends AbstractTableModel {

	private static final String[] columnNames = {"SI No.", "Net Amount", "Adj. Amount", "Amount Due"};
	
	@Autowired private PaymentService paymentService;
	
	private List<PaymentSalesInvoice> paymentSalesInvoices = new ArrayList<>();
	
	@Override
	public int getRowCount() {
		return paymentSalesInvoices.size();
	}

	@Override
	public int getColumnCount() {
		return columnNames.length;
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		PaymentSalesInvoice paymentSalesInvoice = paymentSalesInvoices.get(rowIndex);
		switch (columnIndex) {
		case PaymentSalesInvoicesTable.SALES_INVOICE_NUMBER_COLUMN_INDEX:
			return paymentSalesInvoice.getSalesInvoice().getSalesInvoiceNumber();
		case PaymentSalesInvoicesTable.NET_AMOUNT_COLUMN_INDEX:
			return FormatterUtil.formatAmount(paymentSalesInvoice.getSalesInvoice().getTotalNetAmount());
		case PaymentSalesInvoicesTable.ADJUSTMENT_AMOUNT_COLUMN_INDEX:
			if (paymentSalesInvoice.getAdjustmentAmount() != null) {
				return FormatterUtil.formatAmount(paymentSalesInvoice.getAdjustmentAmount());
			} else {
				return null;
			}
		case PaymentSalesInvoicesTable.AMOUNT_DUE_COLUMN_INDEX:
			return FormatterUtil.formatAmount(paymentSalesInvoice.getAmountDue());
		}
		return null;
	}

	@Override
	public Class<?> getColumnClass(int columnIndex) {
		switch (columnIndex) {
		case PaymentSalesInvoicesTable.NET_AMOUNT_COLUMN_INDEX:
		case PaymentSalesInvoicesTable.ADJUSTMENT_AMOUNT_COLUMN_INDEX:
		case PaymentSalesInvoicesTable.AMOUNT_DUE_COLUMN_INDEX:
			return Number.class;
		default:
			return Object.class;
		}
	}
	
	public void setPaymentSalesInvoices(List<PaymentSalesInvoice> paymentSalesInvoices) {
		this.paymentSalesInvoices = paymentSalesInvoices;
		fireTableDataChanged();
	}

	@Override
	public String getColumnName(int column) {
		return columnNames[column];
	}
	
	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		return columnIndex == PaymentSalesInvoicesTable.ADJUSTMENT_AMOUNT_COLUMN_INDEX;
	}
	
	@Override
	public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
		PaymentSalesInvoice paymentSalesInvoice = paymentSalesInvoices.get(rowIndex);
		String val = (String)aValue;
		switch (columnIndex) {
		case PaymentSalesInvoicesTable.ADJUSTMENT_AMOUNT_COLUMN_INDEX:
			if (!StringUtils.isEmpty(val)) {
				paymentSalesInvoice.setAdjustmentAmount(NumberUtil.toBigDecimal(val));
			} else {
				paymentSalesInvoice.setAdjustmentAmount(null);
			}
			paymentService.save(paymentSalesInvoice);
			fireTableCellUpdated(rowIndex, columnIndex);
			break;
		}
	}

	public PaymentSalesInvoice getPaymentSalesInvoice(int rowIndex) {
		return paymentSalesInvoices.get(rowIndex);
	}

	public List<PaymentSalesInvoice> getPaymentSalesInvoices() {
		return paymentSalesInvoices;
	}

	public void removeItem(PaymentSalesInvoice paymentSalesInvoice) {
		paymentSalesInvoices.remove(paymentSalesInvoice);
		paymentService.delete(paymentSalesInvoice);
		fireTableDataChanged();
	}

	public boolean hasItems() {
		return !paymentSalesInvoices.isEmpty();
	}
	
}
