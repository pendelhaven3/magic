package com.pj.magic;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JTable;

public class ItemsTableTabAction extends AbstractAction {

	private static final long serialVersionUID = -7704572415650423562L;
	
	private JTable table;
	
	public ItemsTableTabAction(JTable table) {
		this.table = table;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		int selectedColumn = table.getSelectedColumn();
		int selectedRow = table.getSelectedRow();
		
		switch (selectedColumn) {
		case 0:
			table.getModel().setValueAt("SAMPLE PRODUCT DESCRIPTION", selectedRow, 1);
			table.changeSelection(selectedRow, 2, false, false);
			table.editCellAt(selectedRow, 2);
			table.getEditorComponent().requestFocusInWindow();
			break;
		case 2:
			table.changeSelection(selectedRow, 3, false, false);
			table.editCellAt(selectedRow, 3);
			table.getEditorComponent().requestFocusInWindow();
			break;
		case 3:
			if (selectedRow + 1 < table.getRowCount()) {
				table.changeSelection(selectedRow + 1, 0, false, false);
				table.editCellAt(selectedRow + 1, 0);
				table.getEditorComponent().requestFocusInWindow();
			}
			break;
		default:
			break;
		}
	}
	
}
