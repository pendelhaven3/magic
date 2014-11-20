package com.pj.magic.dao;

import java.util.List;

import com.pj.magic.model.Customer;
import com.pj.magic.model.SalesInvoice;
import com.pj.magic.model.search.SalesInvoiceSearchCriteria;

public interface SalesInvoiceDao {

	void save(SalesInvoice salesInvoice);
	
	SalesInvoice get(long id);

	List<SalesInvoice> getAll();

	List<SalesInvoice> search(SalesInvoiceSearchCriteria criteria);
	
	List<SalesInvoice> findAllUnpaidByCustomer(Customer customer);

	SalesInvoice findBySalesInvoiceNumber(long salesInvoiceNumber);
	
}
