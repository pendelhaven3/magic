package com.pj.magic.dialog;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JTable;
import javax.swing.table.TableModel;

import com.pj.magic.MagicDialog;

public class SelectUnitAction extends AbstractAction {

	private static final long serialVersionUID = -1910941066083817404L;

	private static final int UNIT_COLUMN_INDEX = 0;

	private JTable table;
	private MagicDialog dialog;
	
	public SelectUnitAction(JTable table, MagicDialog dialog) {
		this.table = table;
		this.dialog = dialog;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		TableModel model = table.getModel();
		String unit = (String)model.getValueAt(table.getSelectedRow(), UNIT_COLUMN_INDEX);
		dialog.setReturnValue(SelectUnitDialog.UNIT_RETURN_VALUE_NAME, unit);
		dialog.setVisible(false);
	}

}
