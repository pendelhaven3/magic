package com.pj.magic.dao;

import java.util.List;

import com.pj.magic.model.SalesReturn;
import com.pj.magic.model.SalesReturnItem;

public interface SalesReturnItemDao {

	List<SalesReturnItem> findAllBySalesReturn(SalesReturn salesReturn);

	void save(SalesReturnItem item);

	void delete(SalesReturnItem item);
	
}
