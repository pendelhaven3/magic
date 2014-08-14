package com.pj.magic.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.pj.magic.dao.CustomerDao;
import com.pj.magic.dao.ProductDao;
import com.pj.magic.dao.SalesRequisitionDao;
import com.pj.magic.dao.SalesRequisitionItemDao;
import com.pj.magic.dao.UserDao;
import com.pj.magic.exception.NotEnoughStocksException;
import com.pj.magic.model.Product;
import com.pj.magic.model.SalesInvoice;
import com.pj.magic.model.SalesRequisition;
import com.pj.magic.model.SalesRequisitionItem;
import com.pj.magic.service.SalesInvoiceService;
import com.pj.magic.service.SalesRequisitionService;

@Service
public class SalesRequisitionServiceImpl implements SalesRequisitionService {

	@Autowired private SalesRequisitionDao salesRequisitionDao;
	@Autowired private SalesRequisitionItemDao salesRequisitionItemDao;
	@Autowired private ProductDao productDao;
	@Autowired private SalesInvoiceService salesInvoiceService;
	@Autowired private CustomerDao customerDao;
	@Autowired private UserDao userDao;
	
	@Transactional
	@Override
	public void save(SalesRequisition salesRequisition) {
		salesRequisitionDao.save(salesRequisition);
	}

	@Override
	public SalesRequisition getSalesRequisition(long id) {
		SalesRequisition salesRequisition = salesRequisitionDao.get(id);
		loadSalesRequisitionDetails(salesRequisition);
		return salesRequisition;
	}
	
	private void loadSalesRequisitionDetails(SalesRequisition salesRequisition) {
		salesRequisition.setItems(salesRequisitionItemDao.findAllBySalesRequisition(salesRequisition));
		for (SalesRequisitionItem item : salesRequisition.getItems()) {
			item.setProduct(productDao.get(item.getProduct().getId()));
		}
		if (salesRequisition.getCustomer() != null) {
			salesRequisition.setCustomer(customerDao.get(salesRequisition.getCustomer().getId()));
		}
		salesRequisition.setEncoder(userDao.get(salesRequisition.getEncoder().getId()));
	}

	@Transactional
	@Override
	public void save(SalesRequisitionItem item) {
		salesRequisitionItemDao.save(item);
	}

	@Transactional
	@Override
	public void delete(SalesRequisitionItem item) {
		salesRequisitionItemDao.delete(item);
	}

	@Transactional
	@Override
	public void delete(SalesRequisition salesRequisition) {
		salesRequisitionItemDao.deleteAllBySalesRequisition(salesRequisition);
		salesRequisitionDao.delete(salesRequisition);
	}

	@Transactional(propagation=Propagation.REQUIRES_NEW) // TODO: Look for other transactional methods
	@Override
	public SalesInvoice post(SalesRequisition salesRequisition) throws NotEnoughStocksException {
		for (SalesRequisitionItem item : salesRequisition.getItems()) {
			// [PJ 08/06/2014] Do not update product quantity inside sales requisition object
			// because it has to be "rolled back" manually when an exception happens during posting
			Product product = productDao.get(item.getProduct().getId());
			if (!product.hasAvailableUnitQuantity(item.getUnit(), item.getQuantity())) {
				throw new NotEnoughStocksException(item);
			} else {
				product.subtractUnitQuantity(item.getUnit(), item.getQuantity());
				productDao.updateAvailableQuantities(product);
			}
		}
		salesRequisition.setPosted(true);
		salesRequisitionDao.save(salesRequisition);

		SalesInvoice salesInvoice = salesRequisition.createSalesInvoice();
		salesInvoiceService.save(salesInvoice);
		return salesInvoice;
	}

	@Override
	public List<SalesRequisition> getAllNonPostedSalesRequisitions() {
		SalesRequisition criteria = new SalesRequisition();
		criteria.setPosted(false);
		
		List<SalesRequisition> salesRequisitions = salesRequisitionDao.search(criteria);
		for (SalesRequisition salesRequisition : salesRequisitions) {
			loadSalesRequisitionDetails(salesRequisition);
		}
		return salesRequisitions;
	}
	
}
