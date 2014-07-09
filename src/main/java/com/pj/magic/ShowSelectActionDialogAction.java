package com.pj.magic;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import com.pj.magic.dialog.ActionsTableModel;
import com.pj.magic.dialog.SelectActionDialog;

public class ShowSelectActionDialogAction extends AbstractAction {
	
	private static final long serialVersionUID = -792886737947369111L;
	
	private ItemsTable table;

	public ShowSelectActionDialogAction(ItemsTable table) {
		this.table = table;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		SelectActionDialog dialog = new SelectActionDialog();
		dialog.setVisible(true);
		
		String action = dialog.getSelectedAction();
		if (ActionsTableModel.CREATE_ACTION.equals(action)) {
			table.switchToAddMode();
		} else if (ActionsTableModel.MODIFY_ACTION.equals(action)) {
			// TODO: Add implementation
		}
	}

}
