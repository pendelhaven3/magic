package com.pj.magic.gui;

import java.util.ResourceBundle;

import javax.annotation.PostConstruct;
import javax.swing.JFrame;
import javax.swing.JScrollPane;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.pj.magic.gui.itemstable.ItemsTable;

@Component
public class MagicFrame extends JFrame {

	private static final long serialVersionUID = 8934401271209799423L;
	
	@Autowired
	private ItemsTable itemsTable;

	public MagicFrame() {
		this.setSize(800, 200);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setTitle(ResourceBundle.getBundle("application").getString("application.title"));
	}
	
	@PostConstruct
	private void addContents() {
		JScrollPane scrollPane = new JScrollPane(itemsTable);
		add(scrollPane);
	}
	
	public void setItemsTable(ItemsTable itemsTable) {
		this.itemsTable = itemsTable;
	}
	
}
