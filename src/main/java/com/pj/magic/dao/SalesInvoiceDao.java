package com.pj.magic.dao;

import com.pj.magic.model.SalesInvoice;

public interface SalesInvoiceDao {

	void save(SalesInvoice salesInvoice);
	
	SalesInvoice get(long id);
	
}
