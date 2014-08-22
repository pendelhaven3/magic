package com.pj.magic.gui.panels;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.Date;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JScrollPane;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.pj.magic.gui.component.MagicToolBar;
import com.pj.magic.gui.component.MagicToolBarButton;
import com.pj.magic.gui.dialog.SelectActionDialog;
import com.pj.magic.gui.tables.SalesRequisitionsTable;
import com.pj.magic.model.SalesRequisition;
import com.pj.magic.model.User;
import com.pj.magic.service.SalesRequisitionService;
import com.pj.magic.util.ComponentUtil;

@Component
public class SalesRequisitionListPanel extends AbstractMagicPanel implements ActionListener {
	
	private static final String NEW_SALES_REQUISITION_ACTION_NAME = "newSalesRequisition";
	private static final String NEW_SALES_REQUISITION_ACTION_COMMAND_NAME = "newSalesRequisition";
	
	@Autowired private SalesRequisitionsTable table;
	@Autowired private SelectActionDialog selectActionDialog;
	@Autowired private SalesRequisitionService salesRequisitionService;
	
	@Override
	public void initializeComponents() {
		focusOnComponentWhenThisPanelIsDisplayed(table);
	}

	public void updateDisplay() {
		table.update();
	}

	public void displaySalesRequisitionDetails(SalesRequisition salesRequisition) {
		getMagicFrame().switchToSalesRequisitionPanel(salesRequisition);
	}
	
	@Override
	protected void layoutComponents() {
		setLayout(new GridBagLayout());
		int currentRow = 0;
		GridBagConstraints c = new GridBagConstraints();
		
		c.weightx = 1.0;
		c.weighty = 0.0;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 0;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		add(createToolBar(), c);

		currentRow++;
		
		c.fill = GridBagConstraints.NONE;
		c.weightx = c.weighty = 0.0;
		c.gridx = 0;
		c.gridy = currentRow;
		add(ComponentUtil.createFiller(1, 5), c);
		
		currentRow++;
		
		c.fill = GridBagConstraints.BOTH;
		c.weightx = c.weighty = 1.0;
		c.gridx = 0;
		c.gridy = currentRow;
		
		JScrollPane scrollPane = new JScrollPane(table);
		add(scrollPane, c);
	}
	
	@Override
	protected void registerKeyBindings() {
		getInputMap(WHEN_ANCESTOR_OF_FOCUSED_COMPONENT)
			.put(KeyStroke.getKeyStroke(KeyEvent.VK_F4, 0), NEW_SALES_REQUISITION_ACTION_NAME);
		getActionMap().put(NEW_SALES_REQUISITION_ACTION_NAME, new AbstractAction() {
		
			@Override
			public void actionPerformed(ActionEvent e) {
				switchToNewSalesRequisitionPanel();
			}
		});		
	}
	
	protected void switchToNewSalesRequisitionPanel() {
		SalesRequisition salesRequisition = new SalesRequisition();
		salesRequisition.setCreateDate(new Date());
		salesRequisition.setEncoder(new User(1L)); // TODO: Use current user later
		salesRequisitionService.save(salesRequisition);
		
		getMagicFrame().switchToSalesRequisitionPanel(salesRequisition);
	}

	@Override
	protected void doOnBack() {
		getMagicFrame().switchToMainMenuPanel();
	}
	
	private JToolBar createToolBar() {
		MagicToolBar toolBar = new MagicToolBar();
		addBackButton(toolBar);
		
		JButton postButton = new MagicToolBarButton("plus", "New (F4)");
		postButton.setActionCommand(NEW_SALES_REQUISITION_ACTION_COMMAND_NAME);
		postButton.addActionListener(this);
		
		toolBar.add(postButton);
		return toolBar;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		switch (e.getActionCommand()) {
		case NEW_SALES_REQUISITION_ACTION_COMMAND_NAME:
			switchToNewSalesRequisitionPanel();
			break;
		}
	}

}
