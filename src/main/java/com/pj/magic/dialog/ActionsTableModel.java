package com.pj.magic.dialog;

import javax.swing.table.AbstractTableModel;

public class ActionsTableModel extends AbstractTableModel {

	private static final long serialVersionUID = -3064604545204823075L;
	public static final String CREATE_ACTION = "Create";
	public static final String MODIFY_ACTION = "Modify";
	public static final String DELETE_ACTION = "Modify";

	private String[][] actions = {{MODIFY_ACTION}, {CREATE_ACTION}};
	
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
