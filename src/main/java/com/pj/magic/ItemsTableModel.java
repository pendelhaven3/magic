package com.pj.magic;

import javax.swing.table.AbstractTableModel;

public class ItemsTableModel extends AbstractTableModel {
	
	private static final long serialVersionUID = 3175876080507017536L;
	
	private String[] columnNames = {"Code", "Description", "Unit", "Qty", "Unit Price", "Amount"};
	private String[][] data = {{"A","B","C","D","E","F"},{"G","H","I","J","K","L"},{"G","H","I","J","K","L"}};
	
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
	public String getColumnName(int columnIndex) {
		return columnNames[columnIndex];
	}

}
