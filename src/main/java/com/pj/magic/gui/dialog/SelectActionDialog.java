package com.pj.magic.gui.dialog;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.AbstractAction;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.KeyStroke;

import org.springframework.stereotype.Component;

import com.pj.magic.gui.tables.ActionsTableModel;

@Component
public class SelectActionDialog extends MagicDialog {

	private static final String SELECT_ACTION_ACTION_NAME = "selectAction";
	private static final int ACTION_COLUMN_INDEX = 0;
	
	private String selectedAction;
	private JTable table;
	
	public SelectActionDialog() {
		setSize(180, 150);
		setLocationRelativeTo(null);
		setTitle("Select Action");
		addContents();
	}

	private void addContents() {
		table = new JTable(new ActionsTableModel());
		table.setTableHeader(null);
		table.setRowSelectionInterval(0, 0);
		
		table.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), SELECT_ACTION_ACTION_NAME);
		table.getActionMap().put(SELECT_ACTION_ACTION_NAME, new AbstractAction() {

			@Override
			public void actionPerformed(ActionEvent e) {
				selectAction();
			}
		});
		
		table.addMouseListener(new MouseAdapter() {
			
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2) {
					selectAction();
				}
			}
			
		});
		
		JScrollPane scrollPane = new JScrollPane(table);
		add(scrollPane);	
	}
	
	protected void selectAction() {
		selectedAction = (String)table.getValueAt(table.getSelectedRow(), ACTION_COLUMN_INDEX);
		setVisible(false);
	}

	public String getSelectedAction() {
		return selectedAction;
	}

	@Override
	protected void doWhenEscapeKeyPressed() {
		selectedAction = null;
	}
	
}
