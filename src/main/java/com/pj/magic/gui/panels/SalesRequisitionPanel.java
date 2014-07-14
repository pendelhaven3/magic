package com.pj.magic.gui.panels;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.annotation.PostConstruct;
import javax.swing.AbstractAction;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.KeyStroke;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.pj.magic.gui.tables.SalesRequisitionItemsTable;
import com.pj.magic.model.SalesRequisition;
import com.pj.magic.util.LabelUtil;

@Component
public class SalesRequisitionPanel extends MagicPanel {

	private static final String GO_TO_SALES_REQUISITIONS_LIST_ACTION_NAME = "goToSalesRequisitionsList";
	
	@Autowired private SalesRequisitionItemsTable itemsTable;
	
	private SalesRequisition salesRequisition;
	private JLabel salesRequisitionNumberField = new JLabel("1234567890");
	private JLabel customerNameField = new JLabel("Customer Name");
	
	@PostConstruct
	public void initialize() {
		setLayout(new GridBagLayout());
		
		GridBagConstraints c = new GridBagConstraints();

		c.weightx = c.weighty = 0.0;
		c.fill = GridBagConstraints.NONE;
		c.gridx = 0;
		c.gridy = 0;
		c.anchor = GridBagConstraints.WEST;
		add(LabelUtil.createFiller(100, 30), c);

		c.weightx = c.weighty = 0.0;
		c.fill = GridBagConstraints.NONE;
		c.gridx = 1;
		c.gridy = 0;
		c.anchor = GridBagConstraints.WEST;
		JLabel label1 = LabelUtil.createLabel(150, "Sales Requisition No.:");
		add(label1, c);
		
		c.weightx = 1.0;
		c.weighty = 0.0;
		c.gridx = 2;
		c.gridy = 0;
		c.anchor = GridBagConstraints.WEST;
		salesRequisitionNumberField = LabelUtil.createLabel(150, "");
		add(salesRequisitionNumberField, c);
		
		c.weightx = c.weighty = 0.0;
		c.fill = GridBagConstraints.NONE;
		c.gridx = 0;
		c.gridy = 1;
		c.anchor = GridBagConstraints.WEST;
		add(LabelUtil.createFiller(100, 30), c);

		c.weightx = c.weighty = 0.0;
		c.fill = GridBagConstraints.NONE;
		c.gridx = 1;
		c.gridy = 1;
		c.anchor = GridBagConstraints.WEST;
		JLabel label2 = LabelUtil.createLabel(150, "Customer Name:");
		add(label2, c);
		
		c.weightx = 1.0;
		c.weighty = 0.0;
		c.gridx = 2;
		c.gridy = 1;
		c.anchor = GridBagConstraints.WEST;
		customerNameField = LabelUtil.createLabel(150, "");
		add(customerNameField, c);
		
		c.weightx = c.weighty = 0.0;
		c.fill = GridBagConstraints.NONE;
		c.gridx = 0;
		c.gridy = 2;
		c.anchor = GridBagConstraints.WEST;
		add(LabelUtil.createFiller(100, 10), c);
		
		c.fill = GridBagConstraints.BOTH;
		c.weightx = c.weighty = 1.0;
		c.gridx = 0;
		c.gridy = 3;
		c.gridwidth = 3;
		c.anchor = GridBagConstraints.CENTER;
		
		JScrollPane scrollPane = new JScrollPane(itemsTable);
		add(scrollPane, c);
		
		registerKeyBindings();
		focusOnThisComponentWhenThisPanelIsDisplayed(itemsTable);
	}
	
	public void refreshDisplay(SalesRequisition salesRequisition) {
		this.salesRequisition = salesRequisition;
		salesRequisitionNumberField.setText(salesRequisition.getSalesRequisitionNumber().toString());
		customerNameField.setText(salesRequisition.getCustomerName());
		itemsTable.setSalesRequisition(salesRequisition);
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
