package com.pj.magic.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pj.magic.dao.ProductDao;
import com.pj.magic.dao.SalesInvoiceDao;
import com.pj.magic.dao.SalesInvoiceItemDao;
import com.pj.magic.model.SalesInvoice;
import com.pj.magic.model.SalesInvoiceItem;
import com.pj.magic.service.SalesInvoiceService;

@Service
public class SalesInvoiceServiceImpl implements SalesInvoiceService {

	@Autowired private SalesInvoiceDao salesInvoiceDao;
	@Autowired private SalesInvoiceItemDao salesInvoiceItemDao;
	@Autowired private ProductDao productDao;
	
	@Override
	public void save(SalesInvoice salesInvoice) {
		salesInvoiceDao.save(salesInvoice);
		for (SalesInvoiceItem item : salesInvoice.getItems()) {
			salesInvoiceItemDao.save(item);
		}
	}

	@Override
	public List<SalesInvoice> getAllSalesInvoices() {
		List<SalesInvoice> salesInvoices = salesInvoiceDao.getAll();
		for (SalesInvoice salesInvoice : salesInvoices) {
			loadSalesInvoiceItems(salesInvoice);
		}
		return salesInvoices;
	}

	private void loadSalesInvoiceItems(SalesInvoice salesInvoice) {
		salesInvoice.setItems(salesInvoiceItemDao.findAllBySalesInvoice(salesInvoice));
		for (SalesInvoiceItem item : salesInvoice.getItems()) {
			item.setProduct(productDao.get(item.getProduct().getId()));
		}
	}

}
