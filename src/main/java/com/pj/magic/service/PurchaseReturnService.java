package com.pj.magic.service;

import java.util.List;

import com.pj.magic.model.PurchaseReturn;
import com.pj.magic.model.PurchaseReturnItem;
import com.pj.magic.model.search.PurchaseReturnSearchCriteria;

public interface PurchaseReturnService {

	List<PurchaseReturn> getNewPurchaseReturns();

	void save(PurchaseReturn purchaseReturn);

	PurchaseReturn getPurchaseReturn(long id);

	void save(PurchaseReturnItem item);

	void delete(PurchaseReturnItem item);
	
	void post(PurchaseReturn purchaseReturn);

	List<PurchaseReturn> search(PurchaseReturnSearchCriteria criteria);
	
	PurchaseReturn findPurchaseReturnByPurchaseReturnNumber(long purchaseReturnNumber);

	void markAsPaid(PurchaseReturn purchaseReturn);
	
}