package com.pj.magic.dao;

import java.util.List;

import com.pj.magic.model.BadPurchaseReturn;
import com.pj.magic.model.search.BadPurchaseReturnSearchCriteria;

public interface BadPurchaseReturnDao {

	BadPurchaseReturn get(long id);

	void save(BadPurchaseReturn badStockReturn);

	List<BadPurchaseReturn> search(BadPurchaseReturnSearchCriteria criteria);

	BadPurchaseReturn findByBadPurchaseReturnNumber(long badPurchaseReturnNumber);

}