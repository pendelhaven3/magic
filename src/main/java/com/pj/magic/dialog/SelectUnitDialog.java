package com.pj.magic.dialog;

import java.awt.event.KeyEvent;

import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.KeyStroke;

import com.pj.magic.MagicDialog;

public class SelectUnitDialog extends MagicDialog {

	private static final long serialVersionUID = 5843643876043492649L;
	private static final String SELECT_UNIT_ACTION_NAME = "selectUnit";
	public static final String UNIT_RETURN_VALUE_NAME = "unit";
	
	public SelectUnitDialog() {
		setSize(180, 150);
		setTitle("Select Unit");
		addContents();
	}

	private void addContents() {
		JTable table = new JTable(new UnitsTableModel());
		table.setRowSelectionInterval(0, 0);
		
		table.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_F9, 0), SELECT_UNIT_ACTION_NAME);
		table.getActionMap().put(SELECT_UNIT_ACTION_NAME, new SelectUnitAction(table, this));
		
		JScrollPane scrollPane = new JScrollPane(table);
		add(scrollPane);	
	}

	public String getSelectedUnit() {
		return getReturnValue(UNIT_RETURN_VALUE_NAME);
	}
	
}
