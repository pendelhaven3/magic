package com.pj.magic.gui.dialog;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.KeyStroke;

import org.springframework.stereotype.Component;

@Component
public class SelectActionDialog extends MagicDialog {

	private static final String SELECT_ACTION_ACTION_NAME = "selectAction";
	private static final int ACTION_COLUMN_INDEX = 0;
	
	private String selectedAction;
	
	public SelectActionDialog() {
		setSize(180, 150);
		setLocationRelativeTo(null);
		setTitle("Select Action");
		addContents();
	}

	private void addContents() {
		final JTable table = new JTable(new ActionsTableModel());
		table.setTableHeader(null);
		table.setRowSelectionInterval(0, 0);
		
		table.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), SELECT_ACTION_ACTION_NAME);
		table.getActionMap().put(SELECT_ACTION_ACTION_NAME, new AbstractAction() {

			@Override
			public void actionPerformed(ActionEvent e) {
				selectedAction = (String)table.getValueAt(table.getSelectedRow(), ACTION_COLUMN_INDEX);
				setVisible(false);
			}
		});
		
		JScrollPane scrollPane = new JScrollPane(table);
		add(scrollPane);	
	}

	public String getSelectedAction() {
		return selectedAction;
	}

	@Override
	protected void doWhenEscapeKeyPressed() {
		selectedAction = null;
	}
	
}
