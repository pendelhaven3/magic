package com.pj.magic.dialog;

import java.awt.event.KeyEvent;

import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.KeyStroke;

import com.pj.magic.MagicDialog;

public class SelectActionDialog extends MagicDialog {

	private static final long serialVersionUID = 6680440304005176359L;
	private static final String SELECT_ACTION_ACTION_NAME = "selectAction";
	public static final String ACTION_RETURN_VALUE_NAME = "action";
	
	public SelectActionDialog() {
		setSize(180, 150);
		setLocationRelativeTo(null);
		setTitle("Select Action");
		addContents();
	}

	private void addContents() {
		JTable table = new JTable(new ActionsTableModel());
		table.setTableHeader(null);
		table.setRowSelectionInterval(0, 0);
		
		table.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), SELECT_ACTION_ACTION_NAME);
		table.getActionMap().put(SELECT_ACTION_ACTION_NAME, new SelectActionAction(table, this));
		
		JScrollPane scrollPane = new JScrollPane(table);
		add(scrollPane);	
	}

	public String getSelectedAction() {
		return getReturnValue(ACTION_RETURN_VALUE_NAME);
	}
	
}
