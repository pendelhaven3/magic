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

import com.pj.magic.gui.tables.SalesInvoicesTable;
import com.pj.magic.model.SalesInvoice;
import com.pj.magic.service.SalesInvoiceService;

@Component
public class SalesInvoicesListPanel extends MagicPanel {

	private static final String BACK_ACTION_NAME = "back";
	
	@Autowired private SalesInvoicesTable table;
	@Autowired private SalesInvoiceService salesInvoiceService;
	
	@PostConstruct
	public void initialize() {
		layoutComponents();
		registerKeyBindings();
		focusOnComponentWhenThisPanelIsDisplayed(table);
	}

	public void refreshDisplay() {
		table.update();
	}

	public void displaySalesInvoiceDetails(SalesInvoice salesInvoice) {
//		getMagicFrame().switchToSalesInvoicesListPanel(salesInvoice);
	}
	
	private void layoutComponents() {
		setLayout(new GridBagLayout());
		
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;
		c.weightx = c.weighty = 1.0;
		c.gridx = 0;
		c.gridy = 0;
		
		JScrollPane scrollPane = new JScrollPane(table);
		add(scrollPane, c);
	}
	
	private void registerKeyBindings() {
		getInputMap(WHEN_ANCESTOR_OF_FOCUSED_COMPONENT)
			.put(KeyStroke.getKeyStroke(KeyEvent.VK_F9, 0), BACK_ACTION_NAME);
		getActionMap().put(BACK_ACTION_NAME, new AbstractAction() {

			@Override
			public void actionPerformed(ActionEvent e) {
				getMagicFrame().switchToMainMenuPanel();
			}
			
		});
	}
	
}
