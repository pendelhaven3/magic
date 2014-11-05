package com.pj.magic.dao;

import java.util.List;

import com.pj.magic.model.PurchaseOrder;
import com.pj.magic.model.Supplier;

public interface PurchaseOrderDao {

	PurchaseOrder get(long id);

	void save(PurchaseOrder purchaseOrder);

	void delete(PurchaseOrder purchaseOrder);

	List<PurchaseOrder> search(PurchaseOrder criteria);
	
	List<PurchaseOrder> findAllBySupplier(Supplier supplier);
	
}
