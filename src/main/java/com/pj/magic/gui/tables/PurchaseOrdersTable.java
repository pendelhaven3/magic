package com.pj.magic.gui.tables;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.swing.AbstractAction;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.pj.magic.gui.panels.PurchaseOrderListPanel;
import com.pj.magic.gui.tables.models.PurchaseOrdersTableModel;
import com.pj.magic.model.PurchaseOrder;
import com.pj.magic.service.PurchaseOrderService;

@Component
public class PurchaseOrdersTable extends JTable {

	private static final String GO_TO_STOCK_QUANTITY_CONVERSION_ACTION_NAME = "goToPurchaseOrder";
	private static final String DELETE_STOCK_QUANTITY_CONVERSION_ACTION_NAME = "deletePurchaseOrder";

	@Autowired private PurchaseOrderService purchaseOrderService;
	@Autowired private PurchaseOrdersTableModel tableModel;
	
	@Autowired
	public PurchaseOrdersTable(PurchaseOrdersTableModel tableModel) {
		super(tableModel);
	}
	
	@PostConstruct
	public void initialize() {
		registerKeyBindings();
    }
	
	public void update() {
		List<PurchaseOrder> purchaseOrders = purchaseOrderService.getAllNonPostedPurchaseOrders();
		tableModel.setPurchaseOrders(purchaseOrders);
		if (!purchaseOrders.isEmpty()) {
			changeSelection(0, 0, false, false);
		}
	}
	
	public PurchaseOrder getCurrentlySelectedPurchaseOrder() {
		return tableModel.getPurchaseOrder(getSelectedRow());
	}
	
	public void displayPurchaseOrderDetails(PurchaseOrder purchaseOrder) {
		PurchaseOrderListPanel panel = (PurchaseOrderListPanel)
				SwingUtilities.getAncestorOfClass(PurchaseOrderListPanel.class, this);
		panel.displayPurchaseOrderDetails(purchaseOrder);
	}
	
	public void removeCurrentlySelectedRow() {
		int selectedRowIndex = getSelectedRow();
		PurchaseOrder purchaseOrder = getCurrentlySelectedPurchaseOrder();
		purchaseOrderService.delete(purchaseOrder);
		tableModel.remove(purchaseOrder);
		
		if (tableModel.getRowCount() > 0) {
			if (selectedRowIndex == tableModel.getRowCount()) {
				changeSelection(selectedRowIndex - 1, 0, false, false);
			} else {
				changeSelection(selectedRowIndex, 0, false, false);
			}
		}
		
		// TODO: update table as well if any new SQC has been created
	}
	
	public void registerKeyBindings() {
		getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), GO_TO_STOCK_QUANTITY_CONVERSION_ACTION_NAME);
		getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_F3, 0), DELETE_STOCK_QUANTITY_CONVERSION_ACTION_NAME);
		
		getActionMap().put(GO_TO_STOCK_QUANTITY_CONVERSION_ACTION_NAME, new AbstractAction() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (getSelectedRow() != -1) {
					selectPurchaseOrder();
				}
			}
		});
		getActionMap().put(DELETE_STOCK_QUANTITY_CONVERSION_ACTION_NAME, new AbstractAction() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				if (getSelectedRow() != -1) {
					int confirm = JOptionPane.showConfirmDialog(getParent(), "Delete selected stock quantity conversion?");
					if (confirm == JOptionPane.YES_OPTION) {
						removeCurrentlySelectedRow();
					}
				}
			}
		});
		
		addMouseListener(new MouseAdapter() {
			
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2) {
					selectPurchaseOrder();
				}
			}
		});
	}

	protected void selectPurchaseOrder() {
		displayPurchaseOrderDetails(getCurrentlySelectedPurchaseOrder());
	}

}
