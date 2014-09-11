package com.pj.magic.gui.panels;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JScrollPane;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.pj.magic.gui.component.MagicToolBar;
import com.pj.magic.gui.component.MagicToolBarButton;
import com.pj.magic.gui.tables.ReceivingReceiptsTable;
import com.pj.magic.model.PurchaseOrder;
import com.pj.magic.util.ComponentUtil;

@Component
public class ReceivingReceiptListPanel extends AbstractMagicPanel implements ActionListener {
	
	private static final String NEW_PURCHASE_ORDER_ACTION_NAME = "newPurchaseOrder";
	private static final String DELETE_PURCHASE_ORDER_ACTION_NAME = "deletePurchaseOrder";
	
	@Autowired private ReceivingReceiptsTable table;
	
	@Override
	public void initializeComponents() {
		focusOnComponentWhenThisPanelIsDisplayed(table);
	}

	public void updateDisplay() {
		table.update();
	}

	public void displayPurchaseOrderDetails(PurchaseOrder stockQuantityConversion) {
		getMagicFrame().switchToPurchaseOrderPanel(stockQuantityConversion);
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
			.put(KeyStroke.getKeyStroke(KeyEvent.VK_F4, 0), NEW_PURCHASE_ORDER_ACTION_NAME);
		getActionMap().put(NEW_PURCHASE_ORDER_ACTION_NAME, new AbstractAction() {
		
			@Override
			public void actionPerformed(ActionEvent e) {
				switchToNewPurchaseOrderPanel();
			}
		});		
		
		getInputMap(WHEN_ANCESTOR_OF_FOCUSED_COMPONENT)
			.put(KeyStroke.getKeyStroke(KeyEvent.VK_F3, 0), DELETE_PURCHASE_ORDER_ACTION_NAME);
		getActionMap().put(DELETE_PURCHASE_ORDER_ACTION_NAME, new AbstractAction() {
	
			@Override
			public void actionPerformed(ActionEvent e) {
				deletePurchaseOrder();
			}
		});		
	}
	
	protected void switchToNewPurchaseOrderPanel() {
		getMagicFrame().switchToPurchaseOrderPanel(new PurchaseOrder());
	}

	@Override
	protected void doOnBack() {
		getMagicFrame().switchToMainMenuPanel();
	}
	
	private JToolBar createToolBar() {
		MagicToolBar toolBar = new MagicToolBar();
		addBackButton(toolBar);
		
		JButton addButton = new MagicToolBarButton("plus", "New (F4)");
		addButton.setActionCommand(NEW_PURCHASE_ORDER_ACTION_NAME);
		addButton.addActionListener(this);
		toolBar.add(addButton);
		
		JButton deleteButton = new MagicToolBarButton("minus", "Delete (F3)");
		deleteButton.setActionCommand(DELETE_PURCHASE_ORDER_ACTION_NAME);
		deleteButton.addActionListener(this);
		toolBar.add(deleteButton);
		
		return toolBar;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		switch (e.getActionCommand()) {
		case NEW_PURCHASE_ORDER_ACTION_NAME:
			switchToNewPurchaseOrderPanel();
			break;
		case DELETE_PURCHASE_ORDER_ACTION_NAME:
			deletePurchaseOrder();
			break;
		}
	}

	private void deletePurchaseOrder() {
		if (table.getSelectedRow() != -1) {
			PurchaseOrder selected = table.getCurrentlySelectedPurchaseOrder();
			if (selected.isPosted()) {
				showErrorMessage("Cannot delete a stock quantity conversion that is already posted!");
				return;
			}
			if (confirm("Are you sure you want to delete this stock quantity conversion?")) {
				table.removeCurrentlySelectedRow();
			}
		}
	}

}
