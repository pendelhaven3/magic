package com.pj.magic;

import java.util.ResourceBundle;

import javax.swing.JFrame;
import javax.swing.JScrollPane;

public class MagicFrame extends JFrame {

	private static final long serialVersionUID = 8934401271209799423L;

	public MagicFrame() {
		this.setSize(500, 200);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(EXIT_ON_CLOSE); // TODO: Clean up resources
		setTitle(ResourceBundle.getBundle("application").getString("application.title"));
		addContents();
	}

	private void addContents() {
		ItemsTable table = new ItemsTable(new ItemsTableModel());
//		table.setRowSelectionInterval(0, 0);
		// TODO: disable multiple row selection, shift+click and ctrl+click
		
		JScrollPane scrollPane = new JScrollPane(table);
		add(scrollPane);
	}
	
}
