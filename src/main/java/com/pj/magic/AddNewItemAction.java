package com.pj.magic;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

public class AddNewItemAction extends AbstractAction {

	private static final long serialVersionUID = -3977841574877290390L;

	private ItemsTable table;
	
	public AddNewItemAction(ItemsTable table) {
		this.table = table;
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		table.switchToBlankItems();
	}

}
