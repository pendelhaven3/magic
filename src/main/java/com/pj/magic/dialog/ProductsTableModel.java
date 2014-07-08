package com.pj.magic.dialog;

import javax.swing.table.AbstractTableModel;

public class ProductsTableModel extends AbstractTableModel {

	private static final long serialVersionUID = -4623255772951504093L;
	
	private String[] columnNames = {"Code", "Description"};
	private String[][] data = {
			{"REJGRN010", "REJOICE SHAMPOO GREEN 10X288"}, 
			{"ZONRE0100", "ZONROX REGULAR 100X144"}
	};
	
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

}
