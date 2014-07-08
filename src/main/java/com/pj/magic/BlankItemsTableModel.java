package com.pj.magic;

import java.util.ArrayList;

import javax.swing.table.AbstractTableModel;

public class BlankItemsTableModel extends AbstractTableModel {

	private static final long serialVersionUID = -3834904003111821718L;
	private String[] columnNames = {"Code", "Description", "Unit", "Qty", "Unit Price", "Amount"};
	private ArrayList<String[]> data2 = new ArrayList<>();

	public BlankItemsTableModel() {
		data2.add(new String[] {"", "", "", "", "", ""});
	}
	
	@Override
	public int getColumnCount() {
		return columnNames.length;
	}

	@Override
	public int getRowCount() {
		return data2.size();
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		return data2.get(rowIndex)[columnIndex];
	}
	
	@Override
	public String getColumnName(int column) {
		return columnNames[column];
	}
	
	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		return columnIndex == ItemsTable.PRODUCT_CODE_COLUMN_INDEX
				|| columnIndex == ItemsTable.QUANTITY_COLUMN_INDEX
				|| columnIndex == ItemsTable.UNIT_COLUMN_INDEX;
	}
	
	@Override
	public void setValueAt(Object value, int rowIndex, int columnIndex) {
		data2.get(rowIndex)[columnIndex] = (String)value;
		fireTableCellUpdated(rowIndex, columnIndex);
	}
	
	public void addBlankRow() {
		data2.add(new String[] {"", "", "", "", "", ""});
		fireTableDataChanged();
	}
	
}
