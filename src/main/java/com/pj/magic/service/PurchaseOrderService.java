package com.pj.magic.service;

import java.util.List;

import com.pj.magic.model.PurchaseOrder;
import com.pj.magic.model.PurchaseOrderItem;
import com.pj.magic.model.ReceivingReceipt;

public interface PurchaseOrderService {

	void save(PurchaseOrder purchaseOrder);
	
	PurchaseOrder getPurchaseOrder(long id);

	void save(PurchaseOrderItem item);

	void delete(PurchaseOrderItem item);

	void delete(PurchaseOrder currentlySelectedPurchaseOrder);

	ReceivingReceipt post (PurchaseOrder purchaseOrder);
	
	List<PurchaseOrder> getAllNonPostedPurchaseOrders();

	void markAsDelivered(PurchaseOrder purchaseOrder);
	
}
