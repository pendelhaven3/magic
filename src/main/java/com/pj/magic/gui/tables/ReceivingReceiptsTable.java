package com.pj.magic.gui.tables;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.swing.JTable;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.pj.magic.gui.tables.models.PurchaseOrdersTableModel;
import com.pj.magic.model.PurchaseOrder;
import com.pj.magic.service.PurchaseOrderService;

@Component
public class ReceivingReceiptsTable extends JTable {

	@Autowired private PurchaseOrderService purchaseOrderService;
	@Autowired private PurchaseOrdersTableModel tableModel;
	
	@Autowired
	public ReceivingReceiptsTable(PurchaseOrdersTableModel tableModel) {
		super(tableModel);
	}

	@PostConstruct
	public void initialize() {
		
	}
	
	public void update() {
		List<PurchaseOrder> purchaseOrders = purchaseOrderService.getAllNonPostedPurchaseOrders();
		tableModel.setPurchaseOrders(purchaseOrders);
		if (!purchaseOrders.isEmpty()) {
			changeSelection(0, 0, false, false);
		}
	}

	public PurchaseOrder getCurrentlySelectedPurchaseOrder() {
		return null;
	}

	public void removeCurrentlySelectedRow() {
	}
	
}
