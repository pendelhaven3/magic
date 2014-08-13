package com.pj.magic.gui.panels;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.Date;

import javax.annotation.PostConstruct;
import javax.swing.AbstractAction;
import javax.swing.JScrollPane;
import javax.swing.KeyStroke;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.pj.magic.gui.dialog.SelectActionDialog;
import com.pj.magic.gui.tables.ActionsTableModel;
import com.pj.magic.gui.tables.SalesRequisitionsTable;
import com.pj.magic.model.SalesRequisition;
import com.pj.magic.model.User;
import com.pj.magic.service.SalesRequisitionService;

@Component
public class SalesRequisitionsListPanel extends MagicPanel {
	
	private static final String OPEN_SELECT_ACTION_DIALOG_ACTION_NAME = "openSelectActionDialog";
	private static final String BACK_ACTION_NAME = "back";
	
	@Autowired private SalesRequisitionsTable table;
	@Autowired private SelectActionDialog selectActionDialog;
	@Autowired private SalesRequisitionService salesRequisitionService;
	
	@PostConstruct
	public void initialize() {
		layoutComponents();
		registerKeyBindings();
		focusOnComponentWhenThisPanelIsDisplayed(table);
	}

	public void refreshDisplay() {
		table.update();
	}

	public void displaySalesRequisitionDetails(SalesRequisition salesRequisition) {
		getMagicFrame().switchToSalesRequisitionPanel(salesRequisition);
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
			.put(KeyStroke.getKeyStroke(KeyEvent.VK_F10, 0), OPEN_SELECT_ACTION_DIALOG_ACTION_NAME);
		getActionMap().put(OPEN_SELECT_ACTION_DIALOG_ACTION_NAME, new AbstractAction() {
		
			@Override
			public void actionPerformed(ActionEvent e) {
				selectActionDialog.setVisible(true);
				
				String action = selectActionDialog.getSelectedAction();
				if (ActionsTableModel.CREATE_ACTION.equals(action)) {
					SalesRequisition salesRequisition = new SalesRequisition();
					salesRequisition.setCreateDate(new Date());
					salesRequisition.setEncoder(new User(1L)); // TODO: Replace with actual value
					salesRequisitionService.save(salesRequisition);
					
					getMagicFrame().switchToSalesRequisitionPanel(salesRequisition);
				}
			}
		});
		
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
