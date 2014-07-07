package com.pj.magic;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.KeyStroke;

public class MagicFrame extends JFrame {

	private static final long serialVersionUID = 8934401271209799423L;

	public MagicFrame() {
		this.setSize(500, 200);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(EXIT_ON_CLOSE); // TODO: Clean up resources
		setTitle("Magic");
		addContents();
	}

	private void addContents() {
		ItemsTable table = new ItemsTable(new ItemsTableModel());
		table.setRowSelectionInterval(0, 0);
		// TODO: disable multiple row selection, shift+click and ctrl+click
		
		table.getInputMap().put(KeyStroke.getKeyStroke("F2"), "addNewItem");
		table.getActionMap().put("addNewItem", new AddNewItemAction(table));
		
		JScrollPane scrollPane = new JScrollPane(table);
		add(scrollPane);
	}
	
}
