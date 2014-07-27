package com.pj.magic.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pj.magic.dao.SalesInvoiceDao;
import com.pj.magic.dao.SalesInvoiceItemDao;
import com.pj.magic.model.SalesInvoice;
import com.pj.magic.model.SalesInvoiceItem;
import com.pj.magic.service.SalesInvoiceService;

@Service
public class SalesInvoiceServiceImpl implements SalesInvoiceService {

	@Autowired private SalesInvoiceDao salesInvoiceDao;
	@Autowired private SalesInvoiceItemDao salesInvoiceItemDao;
	
	@Override
	public void save(SalesInvoice salesInvoice) {
		salesInvoiceDao.save(salesInvoice);
		for (SalesInvoiceItem item : salesInvoice.getItems()) {
			salesInvoiceItemDao.save(item);
		}
	}

}
