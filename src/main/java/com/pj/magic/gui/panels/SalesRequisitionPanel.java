package com.pj.magic.gui.panels;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.annotation.PostConstruct;
import javax.swing.AbstractAction;
import javax.swing.JScrollPane;
import javax.swing.KeyStroke;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.pj.magic.gui.itemstable.ItemsTable;

@Component
public class SalesRequisitionPanel extends MagicPanel {

	private static final String GO_TO_SALES_REQUISITIONS_LIST_ACTION_NAME = "goToSalesRequisitionsList";
	
	@Autowired private ItemsTable itemsTable;
	
	@PostConstruct
	public void initialize() {
		setLayout(new GridBagLayout());
		
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;
		c.weightx = c.weighty = 1.0;
		c.gridx = 0;
		c.gridy = 0;
		
		JScrollPane scrollPane = new JScrollPane(itemsTable);
		add(scrollPane, c);
		
		registerKeyBindings();
	}

	private void registerKeyBindings() {
		getInputMap(WHEN_ANCESTOR_OF_FOCUSED_COMPONENT)
			.put(KeyStroke.getKeyStroke(KeyEvent.VK_F9, 0), GO_TO_SALES_REQUISITIONS_LIST_ACTION_NAME);
		getActionMap().put(GO_TO_SALES_REQUISITIONS_LIST_ACTION_NAME, new AbstractAction() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				getMagicFrame().switchToSalesRequisitionsListPanel();
			}
		});
	}
	
}
