package com.pj.magic;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;

public class ItemsTableDownAction extends AbstractAction {

	private static final long serialVersionUID = 4412958770142604322L;
	
	private ItemsTable table;
	private Action originalAction;
	
	public ItemsTableDownAction(ItemsTable table, Action originalAction) {
		this.table = table;
		this.originalAction = originalAction;
	}

	@Override
	public void actionPerformed(ActionEvent event) {
		if (table.isQuantityFieldSelected() && table.isLastRowSelected() && table.isCurrentRowValid()) {
			table.addNewRow();
		} else {
			originalAction.actionPerformed(event);
		}
	}

}
