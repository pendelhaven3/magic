package com.pj.magic.service;

import java.util.List;

import com.pj.magic.model.SalesInvoice;

public interface SalesInvoiceService {

	List<SalesInvoice> getAllSalesInvoices();
	
	void save(SalesInvoice salesInvoice);
	
}
