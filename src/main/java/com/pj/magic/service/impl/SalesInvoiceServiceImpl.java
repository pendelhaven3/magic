package com.pj.magic.service.impl;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pj.magic.dao.CustomerDao;
import com.pj.magic.dao.ProductDao;
import com.pj.magic.dao.SalesInvoiceDao;
import com.pj.magic.dao.SalesInvoiceItemDao;
import com.pj.magic.dao.SalesRequisitionDao;
import com.pj.magic.dao.SalesRequisitionItemDao;
import com.pj.magic.model.SalesInvoice;
import com.pj.magic.model.SalesInvoiceItem;
import com.pj.magic.model.SalesRequisition;
import com.pj.magic.model.SalesRequisitionItem;
import com.pj.magic.service.LoginService;
import com.pj.magic.service.SalesInvoiceService;

@Service
public class SalesInvoiceServiceImpl implements SalesInvoiceService {

	@Autowired private SalesInvoiceDao salesInvoiceDao;
	@Autowired private SalesInvoiceItemDao salesInvoiceItemDao;
	@Autowired private ProductDao productDao;
	@Autowired private CustomerDao customerDao;
	@Autowired private SalesRequisitionDao salesRequisitionDao;
	@Autowired private SalesRequisitionItemDao salesRequisitionItemDao;
	@Autowired private LoginService loginService;
	
	@Transactional
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
			loadSalesInvoiceDetails(salesInvoice);
		}
		return salesInvoices;
	}

	private void loadSalesInvoiceDetails(SalesInvoice salesInvoice) {
		salesInvoice.setItems(salesInvoiceItemDao.findAllBySalesInvoice(salesInvoice));
		for (SalesInvoiceItem item : salesInvoice.getItems()) {
			item.setProduct(productDao.get(item.getProduct().getId()));
		}
		salesInvoice.setCustomer(customerDao.get(salesInvoice.getCustomer().getId()));
	}

	@Override
	public SalesInvoice get(long id) {
		SalesInvoice salesInvoice = salesInvoiceDao.get(id);
		loadSalesInvoiceDetails(salesInvoice);
		return salesInvoice;
	}

	@Transactional
	@Override
	public SalesRequisition createSalesRequisitionFromSalesInvoice(SalesInvoice salesInvoice) {
		SalesRequisition salesRequisition = salesInvoice.createSalesRequisition();
		salesRequisition.setCreateDate(new Date());
		salesRequisition.setEncoder(loginService.getLoggedInUser());
		salesRequisitionDao.save(salesRequisition);
		
		for (SalesRequisitionItem item : salesRequisition.getItems()) {
			salesRequisitionItemDao.save(item);
		}
		
		return salesRequisition;
	}

}
