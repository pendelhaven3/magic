package com.pj.magic;

import javax.swing.table.AbstractTableModel;

public class BlankItemsTableModel extends AbstractTableModel {

	private static final long serialVersionUID = -3834904003111821718L;
	
	private String[] columnNames = {"Code", "Description", "Unit", "Qty", "Unit Price", "Amount"};
	private String[][] data = {{"", "", "", "", "", ""}};

	@Override
	public int getColumnCount() {
		return columnNames.length;
	}

	@Override
	public int getRowCount() {
		return data.length;
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		return data[rowIndex][columnIndex];
	}
	
	@Override
	public String getColumnName(int column) {
		return columnNames[column];
	}
	
	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		return true;
	}
	
	@Override
	public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
		data[rowIndex][columnIndex] = (String)aValue;
	}
}
