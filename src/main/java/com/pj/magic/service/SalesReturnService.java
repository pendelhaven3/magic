package com.pj.magic.service;

import java.util.List;

import com.pj.magic.model.SalesReturn;

public interface SalesReturnService {

	List<SalesReturn> getAllSalesReturns();

	void save(SalesReturn salesReturn);

	SalesReturn getSalesReturn(long id);
	
}
