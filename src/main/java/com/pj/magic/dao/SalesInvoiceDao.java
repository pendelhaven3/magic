package com.pj.magic.dao;

import java.util.List;

import com.pj.magic.model.SalesInvoice;

public interface SalesInvoiceDao {

	void save(SalesInvoice salesInvoice);
	
	SalesInvoice get(long id);

	List<SalesInvoice> getAll();
	
}
