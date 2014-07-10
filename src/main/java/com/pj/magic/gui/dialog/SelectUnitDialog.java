package com.pj.magic.gui.dialog;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.JDialog;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.KeyStroke;

public class SelectUnitDialog extends MagicDialog {

	private static final long serialVersionUID = 5843643876043492649L;
	private static final String SELECT_UNIT_ACTION_NAME = "selectUnit";
	private static final int UNIT_COLUMN_INDEX = 0;
	
	private String selectedUnit;
	
	public SelectUnitDialog() {
		setSize(180, 150);
		setLocationRelativeTo(null);
		setTitle("Select Unit");
		addContents();
	}

	private void addContents() {
		final JDialog dialog = this;
		final JTable table = new JTable(new UnitsTableModel());
		table.setRowSelectionInterval(0, 0);
		
		table.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_F9, 0), SELECT_UNIT_ACTION_NAME);
		table.getActionMap().put(SELECT_UNIT_ACTION_NAME, new AbstractAction() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				selectedUnit = (String)table.getValueAt(table.getSelectedRow(), UNIT_COLUMN_INDEX);
				dialog.setVisible(false);
			}
		});
		
		registerCloseOnEscapeKeyBinding(table);
		
		JScrollPane scrollPane = new JScrollPane(table);
		add(scrollPane);	
	}

	public String getSelectedUnit() {
		return selectedUnit;
	}
	
}
