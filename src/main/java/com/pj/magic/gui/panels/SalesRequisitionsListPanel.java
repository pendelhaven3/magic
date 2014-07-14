package com.pj.magic.gui.panels;

import javax.annotation.PostConstruct;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import org.springframework.stereotype.Component;

@Component
public class SalesRequisitionsListPanel extends JPanel {

	@PostConstruct
	public void initialize() {
		JScrollPane scrollPane = new JScrollPane(new JTable());
		add(scrollPane);
	}
	
}
