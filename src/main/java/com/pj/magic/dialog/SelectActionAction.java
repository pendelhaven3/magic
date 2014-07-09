package com.pj.magic.dialog;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JTable;

import com.pj.magic.MagicDialog;

public class SelectActionAction extends AbstractAction {

	private static final long serialVersionUID = 2573926095639198648L;

	private static final int ACTION_COLUMN_INDEX = 0;

	private JTable table;
	private MagicDialog dialog;
	
	public SelectActionAction(JTable table, MagicDialog dialog) {
		this.table = table;
		this.dialog = dialog;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		String action = (String)table.getValueAt(table.getSelectedRow(), ACTION_COLUMN_INDEX);
		dialog.setReturnValue(SelectActionDialog.ACTION_RETURN_VALUE_NAME, action);
		dialog.setVisible(false);
	}

}
