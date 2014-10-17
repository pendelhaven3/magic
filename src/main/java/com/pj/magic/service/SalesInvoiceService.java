package com.pj.magic.service;

import java.util.List;

import com.pj.magic.model.SalesInvoice;
import com.pj.magic.model.SalesRequisition;

public interface SalesInvoiceService {

	List<SalesInvoice> getAllSalesInvoices();
	
	void save(SalesInvoice salesInvoice);

	SalesInvoice get(long id);
	
	SalesRequisition createSalesRequisitionFromSalesInvoice(SalesInvoice salesInvoice);
	
	void post(SalesInvoice salesInvoice);
	
	void cancel(SalesInvoice salesInvoice);
	
}
