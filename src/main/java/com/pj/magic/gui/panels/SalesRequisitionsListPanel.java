package com.pj.magic.gui.panels;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.annotation.PostConstruct;
import javax.swing.JScrollPane;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.pj.magic.gui.itemstable.SalesRequisitionsTable;

@Component
public class SalesRequisitionsListPanel extends MagicPanel {
	
	@Autowired
	private SalesRequisitionsTable table;
	
	@PostConstruct
	public void initialize() {
		setLayout(new GridBagLayout());
		
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;
		c.weightx = c.weighty = 1.0;
		c.gridx = 0;
		c.gridy = 0;
		
		JScrollPane scrollPane = new JScrollPane(table);
		add(scrollPane, c);
		
		focusOnThisComponentWhenThisPanelIsDisplayed(table);
	}

	public void update() {
		table.update();
	}
	
}
