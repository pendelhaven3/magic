package com.pj.magic.dao;

import java.util.List;

import com.pj.magic.model.PurchaseOrder;

public interface PurchaseOrderDao {

	PurchaseOrder get(long id);

	void save(PurchaseOrder purchaseOrder);

	void delete(PurchaseOrder purchaseOrder);

	List<PurchaseOrder> search(PurchaseOrder criteria);
	
}
