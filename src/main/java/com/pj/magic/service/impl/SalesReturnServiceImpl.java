package com.pj.magic.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pj.magic.dao.SalesReturnDao;
import com.pj.magic.dao.SalesReturnItemDao;
import com.pj.magic.model.SalesReturn;
import com.pj.magic.service.SalesInvoiceService;
import com.pj.magic.service.SalesReturnService;

@Service
public class SalesReturnServiceImpl implements SalesReturnService {

	@Autowired private SalesReturnDao salesReturnDao;
	@Autowired private SalesReturnItemDao salesReturnItemDao;
	@Autowired private SalesInvoiceService salesInvoiceService;
	
	@Override
	public List<SalesReturn> getAllSalesReturns() {
		return salesReturnDao.getAll();
	}

	@Override
	public void save(SalesReturn salesReturn) {
		salesReturnDao.save(salesReturn);
	}

	@Override
	public SalesReturn getSalesReturn(long id) {
		SalesReturn salesReturn = salesReturnDao.get(id);
		salesReturn.setItems(salesReturnItemDao.findAllBySalesReturn(salesReturn));
		salesReturn.setSalesInvoice(salesInvoiceService.get(salesReturn.getSalesInvoice().getId()));
		return salesReturn;
	}
	
}
