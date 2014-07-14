package com.pj.magic.gui.tables;

import java.util.ArrayList;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import com.pj.magic.model.SalesRequisition;

public class SalesRequisitionsTableModel extends AbstractTableModel {

	private static final String[] COLUMN_NAMES = {"SR No.", "Customer Name"};
	
	private List<SalesRequisition> salesRequisitions = new ArrayList<>();
	
	@Override
	public int getRowCount() {
		return salesRequisitions.size();
	}

	@Override
	public int getColumnCount() {
		return COLUMN_NAMES.length;
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		SalesRequisition salesRequisition = salesRequisitions.get(rowIndex);
		switch (columnIndex) {
		case SalesRequisitionsTable.SALES_REQUISITION_NUMBER_COLUMN_INDEX:
			return salesRequisition.getSalesRequisitionNumber().toString();
		case SalesRequisitionsTable.CUSTOMER_NAME_COLUMN_INDEX:
			return salesRequisition.getCustomerName();
		default:
			throw new RuntimeException("Fetch invalid column index: " + columnIndex);
		}
	}
	
	@Override
	public String getColumnName(int column) {
		return COLUMN_NAMES[column];
	}
	
	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		return false;
	}
	
	public void setSalesRequisitions(List<SalesRequisition> salesRequisitions) {
		this.salesRequisitions = salesRequisitions;
		fireTableDataChanged();
	}
	
	public SalesRequisition getSalesRequisition(int rowIndex) {
		return salesRequisitions.get(rowIndex);
	}
	
}
