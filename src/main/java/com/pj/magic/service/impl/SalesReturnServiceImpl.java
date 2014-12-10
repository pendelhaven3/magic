package com.pj.magic.service.impl;

import java.util.Date;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pj.magic.dao.ProductDao;
import com.pj.magic.dao.SalesReturnDao;
import com.pj.magic.dao.SalesReturnItemDao;
import com.pj.magic.model.Product;
import com.pj.magic.model.SalesReturn;
import com.pj.magic.model.SalesReturnItem;
import com.pj.magic.model.search.SalesReturnSearchCriteria;
import com.pj.magic.service.LoginService;
import com.pj.magic.service.SalesInvoiceService;
import com.pj.magic.service.SalesReturnService;

@Service
public class SalesReturnServiceImpl implements SalesReturnService {

	@Autowired private SalesReturnDao salesReturnDao;
	@Autowired private SalesReturnItemDao salesReturnItemDao;
	@Autowired private SalesInvoiceService salesInvoiceService;
	@Autowired private ProductDao productDao;
	@Autowired private LoginService loginService;
	
	@Override
	public List<SalesReturn> getAllSalesReturns() {
		List<SalesReturn> salesReturns = salesReturnDao.getAll();
		for (SalesReturn salesReturn : salesReturns) {
			salesReturn.setItems(salesReturnItemDao.findAllBySalesReturn(salesReturn));
		}
		return salesReturns;
	}

	@Transactional
	@Override
	public void save(SalesReturn salesReturn) {
		boolean newSalesReturn = (salesReturn.getId() == null);
		salesReturnDao.save(salesReturn);
		if (!newSalesReturn) {
			salesReturnItemDao.deleteAllBySalesReturn(salesReturn);
		}
	}

	@Override
	public SalesReturn getSalesReturn(long id) {
		SalesReturn salesReturn = salesReturnDao.get(id);
		salesReturn.setItems(salesReturnItemDao.findAllBySalesReturn(salesReturn));
		salesReturn.setSalesInvoice(salesInvoiceService.get(salesReturn.getSalesInvoice().getId()));
		return salesReturn;
	}

	@Transactional
	@Override
	public void save(SalesReturnItem item) {
		salesReturnItemDao.save(item);
	}

	@Transactional
	@Override
	public void delete(SalesReturnItem item) {
		salesReturnItemDao.delete(item);
	}

	@Transactional
	@Override
	public void post(SalesReturn salesReturn) {
		SalesReturn updated = salesReturnDao.get(salesReturn.getId());
		updated.setItems(salesReturnItemDao.findAllBySalesReturn(salesReturn));
		
		for (SalesReturnItem item : updated.getItems()) {
			Product product = productDao.get(item.getSalesInvoiceItem().getProduct().getId());
			product.addUnitQuantity(item.getSalesInvoiceItem().getUnit(), item.getQuantity());
			productDao.updateAvailableQuantities(product);
		}
		updated.setPosted(true);
		updated.setPostDate(new Date());
		updated.setPostedBy(loginService.getLoggedInUser());
		salesReturnDao.save(updated);
	}

	@Override
	public List<SalesReturn> search(SalesReturnSearchCriteria criteria) {
		List<SalesReturn> salesReturns = salesReturnDao.search(criteria);
		for (SalesReturn salesReturn : salesReturns) {
			salesReturn.setItems(salesReturnItemDao.findAllBySalesReturn(salesReturn));
		}
		return salesReturns;
 	}
	
}