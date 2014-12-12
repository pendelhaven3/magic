package com.pj.magic.service;

import java.util.List;

import com.pj.magic.model.SalesInvoice;
import com.pj.magic.model.SalesReturn;
import com.pj.magic.model.SalesReturnItem;
import com.pj.magic.model.search.SalesReturnSearchCriteria;

public interface SalesReturnService {

	List<SalesReturn> getAllSalesReturns();

	void save(SalesReturn salesReturn);

	SalesReturn getSalesReturn(long id);

	void save(SalesReturnItem item);

	void delete(SalesReturnItem item);
	
	void post(SalesReturn salesReturn);

	List<SalesReturn> search(SalesReturnSearchCriteria criteria);
	
	List<SalesReturn> findPostedSalesReturnsBySalesInvoice(SalesInvoice salesInvoice);

}