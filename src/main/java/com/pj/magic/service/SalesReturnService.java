package com.pj.magic.service;

import java.util.List;

import com.pj.magic.model.SalesReturn;
import com.pj.magic.model.SalesReturnItem;

public interface SalesReturnService {

	List<SalesReturn> getAllSalesReturns();

	void save(SalesReturn salesReturn);

	SalesReturn getSalesReturn(long id);

	void save(SalesReturnItem item);

	void delete(SalesReturnItem item);
	
}
