package com.pj.magic.dao;

import java.util.List;

import com.pj.magic.model.BadPurchaseReturn;
import com.pj.magic.model.BadPurchaseReturnItem;

public interface BadPurchaseReturnItemDao {

	void save(BadPurchaseReturnItem item);
	
	List<BadPurchaseReturnItem> findAllByBadPurchaseReturn(BadPurchaseReturn badPurchaseReturn);

	void delete(BadPurchaseReturnItem item);

}