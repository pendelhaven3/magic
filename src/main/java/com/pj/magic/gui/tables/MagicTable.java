package com.pj.magic.gui.tables;

import java.awt.Component;
import java.awt.event.KeyEvent;
import java.util.EventObject;

import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.TableModel;

// TODO: This is not really ItemsTable
/*
 * [PJ 8/27/2014] 
 * ItemsTable has 2 modes: edit (default) and add (allows adding blank rows after the last row).
 * 
 */
public abstract class MagicTable extends JTable {

	protected boolean addMode;
	
	public MagicTable(TableModel tableModel) {
		super(tableModel);
	}
	
	protected void showErrorMessage(String message) {
		JOptionPane.showMessageDialog(this, message, "Error Message", JOptionPane.ERROR_MESSAGE);
	}
	
	protected boolean confirm(String message) {
		int confirm = JOptionPane.showConfirmDialog(this, message, "Confirmation", JOptionPane.YES_NO_OPTION);
		return confirm == JOptionPane.YES_OPTION;
	}
	
	protected void editCellAtCurrentRow(int columnIndex) {
		editCellAt(getSelectedRow(), columnIndex);
		getEditorComponent().requestFocusInWindow();
	}
	
	public boolean isAdding() {
		return addMode;
	}
	
	@Override
	public boolean editCellAt(int row, int column, EventObject e) {
		boolean result = super.editCellAt(row, column, e);
		if (e instanceof KeyEvent) {
			Component c = getEditorComponent();
			if (c instanceof JTextField) {
				((JTextField)c).setText(null);
			}
		}
		return result;
	}
	
}
