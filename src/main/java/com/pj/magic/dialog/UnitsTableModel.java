package com.pj.magic.dialog;

import javax.swing.table.AbstractTableModel;

public class UnitsTableModel extends AbstractTableModel {

	private static final long serialVersionUID = -7401471992280782966L;

	private String[] columnNames = {"Unit"};
	private String[][] data = {{"CSE"}, {"PCS"}};
	
	@Override
	public int getRowCount() {
		return data.length;
	}

	@Override
	public int getColumnCount() {
		return columnNames.length;
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		return data[rowIndex][columnIndex];
	}

	@Override
	public String getColumnName(int column) {
		return columnNames[column];
	}
	
}
