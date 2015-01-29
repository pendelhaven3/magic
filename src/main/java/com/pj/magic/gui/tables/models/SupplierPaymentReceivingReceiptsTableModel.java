package com.pj.magic.gui.tables.models;

import java.util.ArrayList;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.pj.magic.gui.tables.PaymentSalesInvoicesTable;
import com.pj.magic.gui.tables.SupplierPaymentReceivingReceiptsTable;
import com.pj.magic.model.SupplierPayment;
import com.pj.magic.model.SupplierPaymentReceivingReceipt;
import com.pj.magic.service.SupplierPaymentService;
import com.pj.magic.util.FormatterUtil;

@Component
public class SupplierPaymentReceivingReceiptsTableModel extends AbstractTableModel {

	private static final String[] columnNames = {"Received Date", "RR No.", "Net Amount"};
	
	@Autowired private SupplierPaymentService supplierPaymentService;
	
	private List<SupplierPaymentReceivingReceipt> paymentReceivingReceipts = new ArrayList<>();
	
	@Override
	public int getRowCount() {
		return paymentReceivingReceipts.size();
	}

	@Override
	public int getColumnCount() {
		return columnNames.length;
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		SupplierPaymentReceivingReceipt paymentReceivingReceipt = paymentReceivingReceipts.get(rowIndex);
		switch (columnIndex) {
		case SupplierPaymentReceivingReceiptsTable.RECEIVED_DATE_COLUMN_INDEX:
			return FormatterUtil.formatDate(paymentReceivingReceipt.getReceivingReceipt().getReceivedDate());
		case SupplierPaymentReceivingReceiptsTable.RECEIVING_RECEIPT_NUMBER_COLUMN_INDEX:
			return paymentReceivingReceipt.getReceivingReceipt().getReceivingReceiptNumber();
		case PaymentSalesInvoicesTable.NET_AMOUNT_COLUMN_INDEX:
			return FormatterUtil.formatAmount(paymentReceivingReceipt.getReceivingReceipt().getTotalNetAmount());
		default:
			return null;
		}
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

	public void setSupplierPayment(SupplierPayment supplierPayment) {
		if (supplierPayment != null) {
			paymentReceivingReceipts = supplierPayment.getReceivingReceipts();
		} else {
			paymentReceivingReceipts.clear();
		}
		fireTableDataChanged();
	}
	
	@Override
	public String getColumnName(int column) {
		return columnNames[column];
	}
	
	public SupplierPaymentReceivingReceipt getPaymentReceivingReceipt(int rowIndex) {
		return paymentReceivingReceipts.get(rowIndex);
	}

	public List<SupplierPaymentReceivingReceipt> getPaymentReceivingReceipts() {
		return paymentReceivingReceipts;
	}

	public void removeItem(SupplierPaymentReceivingReceipt paymentReceivingReceipt) {
		paymentReceivingReceipts.remove(paymentReceivingReceipt);
		supplierPaymentService.delete(paymentReceivingReceipt);
		fireTableDataChanged();
	}

	public boolean hasItems() {
		return !paymentReceivingReceipts.isEmpty();
	}
	
}