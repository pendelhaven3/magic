package com.pj.magic.gui.tables.models;

import java.util.ArrayList;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import org.springframework.stereotype.Component;

import com.pj.magic.model.ReceivingReceipt;

@Component
public class ReceivingReceiptsTableModel extends AbstractTableModel {

	private static final String[] COLUMN_NAMES = {"RR No.", "Supplier", "Status"};
	private static final int RECEIVING_RECEIPT_NUMBER_COLUMN_INDEX = 0;
	private static final int SUPPLIER_COLUMN_INDEX = 1;
	private static final int STATUS_COLUMN_INDEX = 2;
	
	private List<ReceivingReceipt> receivingReceipts = new ArrayList<>();
	
	public void setReceivingReceipts(List<ReceivingReceipt> receivingReceipts) {
		this.receivingReceipts = receivingReceipts;
		fireTableDataChanged();
	}
	
	@Override
	public int getRowCount() {
		return receivingReceipts.size();
	}

	@Override
	public int getColumnCount() {
		return COLUMN_NAMES.length;
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		ReceivingReceipt receivingReceipt = receivingReceipts.get(rowIndex);
		switch (columnIndex) {
		case RECEIVING_RECEIPT_NUMBER_COLUMN_INDEX:
			return receivingReceipt.getReceivingReceiptNumber().toString();
		case SUPPLIER_COLUMN_INDEX:
			return receivingReceipt.getSupplier().getName();
		case STATUS_COLUMN_INDEX:
			return receivingReceipt.getStatus();
		default:
			throw new RuntimeException("Fetch invalid column index: " + columnIndex);
		}
	}

	@Override
	public String getColumnName(int column) {
		return COLUMN_NAMES[column];
	}

	public ReceivingReceipt getReceivingReceipt(int row) {
		return receivingReceipts.get(row);
	}
	
}
