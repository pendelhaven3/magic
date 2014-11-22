package com.pj.magic.gui.tables.models;

import java.util.ArrayList;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import org.springframework.stereotype.Component;

import com.pj.magic.gui.tables.PaymentSalesInvoicesTable;
import com.pj.magic.model.PaymentSalesInvoice;

@Component
public class PaymentSalesInvoicesTableModel extends AbstractTableModel {

	private static final String[] columnNames = {"SI No.", "Adj. Amount", "Amount"};
	
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
		case PaymentSalesInvoicesTable.ADJUSTMENT_AMOUNT_COLUMN_INDEX:
			return null;
		case PaymentSalesInvoicesTable.AMOUNT_COLUMN_INDEX:
			return null;
		}
		return null;
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
	
}
