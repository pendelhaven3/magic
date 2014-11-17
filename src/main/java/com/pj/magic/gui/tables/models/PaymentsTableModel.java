package com.pj.magic.gui.tables.models;

import java.util.ArrayList;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import org.springframework.stereotype.Component;

import com.pj.magic.model.Payment;
import com.pj.magic.util.FormatterUtil;

@Component
public class PaymentsTableModel extends AbstractTableModel {

	private static final String[] columnNames = {"Payment Date", "Customer", "Total Amount"};
	private static final int PAYMENT_DATE_COLUMN_INDEX = 0;
	private static final int CUSTOMER_COLUMN_INDEX = 1;
	private static final int TOTAL_AMOUNT_COLUMN_INDEX = 2;
	
	private List<Payment> payments = new ArrayList<>();
	
	@Override
	public int getRowCount() {
		return payments.size();
	}

	@Override
	public int getColumnCount() {
		return columnNames.length;
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		Payment payment = payments.get(rowIndex);
		switch (columnIndex) {
		case PAYMENT_DATE_COLUMN_INDEX:
			return FormatterUtil.formatDate(payment.getPaymentDate());
		case CUSTOMER_COLUMN_INDEX:
			return payment.getCustomer().getName();
		case TOTAL_AMOUNT_COLUMN_INDEX:
			return FormatterUtil.formatAmount(payment.getTotalAmount());
		default:
			throw new RuntimeException("Fetching invalid column index: " + columnIndex);
		}
	}

	@Override
	public String getColumnName(int column) {
		return columnNames[column];
	}
	
	public void setPayments(List<Payment> payments) {
		this.payments = payments;
		fireTableDataChanged();
	}
	
	public Payment getPayment(int rowIndex) {
		return payments.get(rowIndex);
	}
	
}