package com.pj.magic.gui.tables;

import javax.swing.table.AbstractTableModel;

public class ActionsTableModel extends AbstractTableModel {

	private static final long serialVersionUID = -3064604545204823075L;
	public static final String CREATE_ACTION = "Create";

	private String[][] actions = {{CREATE_ACTION}};
	
	@Override
	public int getRowCount() {
		return actions.length;
	}

	@Override
	public int getColumnCount() {
		return 1;
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		return actions[rowIndex][columnIndex];
	}

}
