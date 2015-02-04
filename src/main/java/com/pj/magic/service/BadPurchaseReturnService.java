package com.pj.magic.service;

import java.util.List;

import com.pj.magic.model.BadPurchaseReturn;
import com.pj.magic.model.BadPurchaseReturnItem;
import com.pj.magic.model.search.BadPurchaseReturnSearchCriteria;

public interface BadPurchaseReturnService {

	void save(BadPurchaseReturn badPurchaseReturn);
	
	BadPurchaseReturn getBadPurchaseReturn(long id);

	List<BadPurchaseReturn> getAllNewBadPurchaseReturns();

	void save(BadPurchaseReturnItem item);

	void delete(BadPurchaseReturnItem item);

	void post(BadPurchaseReturn badPurchaseReturn);

	BadPurchaseReturn findBadPurchaseReturnByBadPurchaseReturnNumber(long badPurchaseReturnNumber);

	List<BadPurchaseReturn> search(BadPurchaseReturnSearchCriteria criteria);
	
}