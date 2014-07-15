package com.pj.magic.gui.panels;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.annotation.PostConstruct;
import javax.swing.AbstractAction;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.KeyStroke;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;

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
	private JLabel salesRequisitionNumberField;
	private JLabel customerNameField;
	private JLabel createDateField;
	private JLabel encoderField;
	private JLabel totalAmountField;
	
	@PostConstruct
	public void initialize() {
		layoutComponents();
		registerKeyBindings();
		focusOnThisComponentWhenThisPanelIsDisplayed(itemsTable);
		updateTotalAmountFieldWhenItemsTableChanges();
	}
	
	private void updateTotalAmountFieldWhenItemsTableChanges() {
		itemsTable.getModel().addTableModelListener(new TableModelListener() {
			
			@Override
			public void tableChanged(TableModelEvent e) {
				totalAmountField.setText(salesRequisition.getTotalAmount().toString());
			}
		});
	}

	public void refreshDisplay(SalesRequisition salesRequisition) {
		this.salesRequisition = salesRequisition;
		salesRequisitionNumberField.setText(salesRequisition.getSalesRequisitionNumber().toString());
		customerNameField.setText(salesRequisition.getCustomerName());
		createDateField.setText(LabelUtil.formatDate(salesRequisition.getCreateDate()));
		encoderField.setText(salesRequisition.getEncoder());
		totalAmountField.setText(salesRequisition.getTotalAmount().toString());
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
	
	private void layoutComponents() {
		setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();

		// first row
		
		c.weightx = c.weighty = 0.0;
		c.fill = GridBagConstraints.NONE;
		c.gridx = 0;
		c.gridy = 0;
		c.anchor = GridBagConstraints.WEST;
		add(LabelUtil.createFiller(50, 30), c);

		c.weightx = c.weighty = 0.0;
		c.fill = GridBagConstraints.NONE;
		c.gridx = 1;
		c.gridy = 0;
		c.anchor = GridBagConstraints.WEST;
		add(LabelUtil.createLabel(150, "SR No.:"), c);
		
		c.weightx = c.weighty = 0.0;
		c.gridx = 2;
		c.gridy = 0;
		c.anchor = GridBagConstraints.WEST;
		salesRequisitionNumberField = LabelUtil.createLabel(200, "");
		add(salesRequisitionNumberField, c);
		
		c.weightx = c.weighty = 0.0;
		c.fill = GridBagConstraints.NONE;
		c.gridx = 3;
		c.gridy = 0;
		c.anchor = GridBagConstraints.WEST;
		add(LabelUtil.createLabel(100, "Create Date:"), c);
		
		c.weightx = 1.0;
		c.weighty = 0.0;
		c.gridx = 4;
		c.gridy = 0;
		c.anchor = GridBagConstraints.WEST;
		createDateField = LabelUtil.createLabel(150, "");
		add(createDateField, c);
		
		// second row
		
		c.weightx = c.weighty = 0.0;
		c.fill = GridBagConstraints.NONE;
		c.gridx = 1;
		c.gridy = 1;
		c.anchor = GridBagConstraints.WEST;
		add(LabelUtil.createLabel(150, "Customer Name:"), c);
		
		c.weightx = c.weighty = 0.0;
		c.gridx = 2;
		c.gridy = 1;
		c.anchor = GridBagConstraints.WEST;
		customerNameField = LabelUtil.createLabel(200, "");
		add(customerNameField, c);
		
		c.weightx = c.weighty = 0.0;
		c.fill = GridBagConstraints.NONE;
		c.gridx = 3;
		c.gridy = 1;
		c.anchor = GridBagConstraints.WEST;
		add(LabelUtil.createLabel(100, "Encoder:"), c);
		
		c.weightx = 1.0;
		c.weighty = 0.0;
		c.gridx = 4;
		c.gridy = 1;
		c.anchor = GridBagConstraints.WEST;
		encoderField = LabelUtil.createLabel(150, "");
		add(encoderField, c);
		
		// third row
		
		c.weightx = c.weighty = 0.0;
		c.fill = GridBagConstraints.NONE;
		c.gridx = 0;
		c.gridy = 2;
		c.anchor = GridBagConstraints.WEST;
		add(LabelUtil.createFiller(50, 10), c);
		
		// fourth row
		
		c.fill = GridBagConstraints.BOTH;
		c.weightx = c.weighty = 1.0;
		c.gridx = 0;
		c.gridy = 3;
		c.gridwidth = 5;
		c.anchor = GridBagConstraints.CENTER;
		JScrollPane scrollPane = new JScrollPane(itemsTable);
		scrollPane.setPreferredSize(new Dimension(600, 100));
		add(scrollPane, c);

		// fifth row
		
		c.weightx = c.weighty = 0.0;
		c.fill = GridBagConstraints.NONE;
		c.gridx = 3;
		c.gridy = 4;
		c.gridwidth = 1;
		c.anchor = GridBagConstraints.WEST;
		add(LabelUtil.createLabel(100, "Total Amount:"), c);
		
		c.weightx = 1.0;
		c.weighty = 0.0;
		c.gridx = 4;
		c.gridy = 4;
		c.anchor = GridBagConstraints.WEST;
		totalAmountField = LabelUtil.createLabel(150, "");
		add(totalAmountField, c);
	}
	
}
