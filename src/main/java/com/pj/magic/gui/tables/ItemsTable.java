package com.pj.magic.gui.tables;

import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.TableModel;

public abstract class ItemsTable extends JTable {

	public ItemsTable(TableModel tableModel) {
		super(tableModel);
	}
	
	protected void showErrorMessage(String message) {
		JOptionPane.showMessageDialog(this, message, "Error Message", JOptionPane.ERROR_MESSAGE);
	}
	
}
