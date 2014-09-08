package com.pj.magic.service;

import java.util.List;

import com.pj.magic.model.PurchaseOrder;
import com.pj.magic.model.PurchaseOrderItem;

public interface PurchaseOrderService {

	void save(PurchaseOrder purchaseOrder);
	
	PurchaseOrder getPurchaseOrder(long id);

	void save(PurchaseOrderItem item);

	void delete(PurchaseOrderItem item);

	void delete(PurchaseOrder currentlySelectedPurchaseOrder);

	void post (PurchaseOrder purchaseOrder);
	
	List<PurchaseOrder> getAllNonPostedPurchaseOrders();

	void order(PurchaseOrder purchaseOrder);
	
}
