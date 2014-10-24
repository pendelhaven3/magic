package com.pj.magic.gui.tables;

import java.awt.Component;
import java.awt.event.KeyEvent;
import java.util.EventObject;

import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.TableModel;

import com.pj.magic.gui.component.MagicTableCellRenderer;
import com.pj.magic.util.KeyUtil;

// TODO: This is not really ItemsTable
/*
 * [PJ 8/27/2014] 
 * ItemsTable has 2 modes: edit (default) and add (allows adding blank rows after the last row).
 * 
 */
public class MagicTable extends JTable {

	protected boolean addMode; // TODO: Return back to PO panel / items table class
	
	public MagicTable(TableModel tableModel) {
		super(tableModel);
		setSurrendersFocusOnKeystroke(true); // TODO: search other references
		initializeTablePadding();
		setRowHeight(25);
	}
	
	private void initializeTablePadding() {
		setDefaultRenderer(Object.class, new MagicTableCellRenderer());
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
			int keyCode = ((KeyEvent) e).getKeyCode();
			if (KeyUtil.isAlphaNumericKeyCode(keyCode)) {
				Component c = getEditorComponent();
				if (c instanceof JTextField) {
					((JTextField)c).setText(null);
				}
			}
		}
		return result;
	}
	
	protected void selectAndEditCellAt(int row, int column) {
		changeSelection(row, column, false, false);
		editCellAt(row, column);
		getEditorComponent().requestFocusInWindow();
	}
	
}
