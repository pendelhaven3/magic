package com.pj.magic.service.impl;

import java.math.RoundingMode;
import java.util.Date;
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
import com.pj.magic.exception.SalesRequisitionItemNotEnoughStocksException;
import com.pj.magic.exception.SalesRequisitionItemPostException;
import com.pj.magic.exception.SalesRequisitionPostException;
import com.pj.magic.model.Product;
import com.pj.magic.model.SalesInvoice;
import com.pj.magic.model.SalesRequisition;
import com.pj.magic.model.SalesRequisitionItem;
import com.pj.magic.service.LoginService;
import com.pj.magic.service.SalesInvoiceService;
import com.pj.magic.service.SalesRequisitionService;
import com.pj.magic.service.SystemService;

@Service
public class SalesRequisitionServiceImpl implements SalesRequisitionService {

	@Autowired private SalesRequisitionDao salesRequisitionDao;
	@Autowired private SalesRequisitionItemDao salesRequisitionItemDao;
	@Autowired private ProductDao productDao;
	@Autowired private SalesInvoiceService salesInvoiceService;
	@Autowired private CustomerDao customerDao;
	@Autowired private UserDao userDao;
	@Autowired private LoginService loginService;
	@Autowired private SystemService systemService;
	
	@Transactional
	@Override
	public void save(SalesRequisition salesRequisition) {
		boolean isNew = (salesRequisition.getId() == null);
		if (isNew) {
			salesRequisition.setCreateDate(new Date());
			salesRequisition.setTransactionDate(salesRequisition.getCreateDate());
			salesRequisition.setEncoder(loginService.getLoggedInUser());
		}
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
			item.setProduct(productDao.findByIdAndPricingScheme(
					item.getProduct().getId(), salesRequisition.getPricingScheme()));
		}
		if (salesRequisition.getCustomer() != null) {
			salesRequisition.setCustomer(customerDao.get(salesRequisition.getCustomer().getId())); // TODO: Review this
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

	@Transactional(propagation=Propagation.REQUIRES_NEW)
	@Override
	public SalesInvoice post(SalesRequisition salesRequisition)
			throws SalesRequisitionPostException {
		SalesRequisitionPostException exception = new SalesRequisitionPostException();
		
		SalesRequisition updated = getSalesRequisition(salesRequisition.getId());
		for (SalesRequisitionItem item : updated.getItems()) {
			if (item.getProduct().hasNoSellingPrice(item.getUnit())) {
				exception.add(new SalesRequisitionItemPostException(item, "No selling price"));
			} else if (item.getProduct().hasSellingPriceLessThanCost(item.getUnit())) {
				exception.add(new SalesRequisitionItemPostException(item, "Selling price less than cost"));
			}
				
			// [PJ 08/06/2014] Do not update product quantity inside sales requisition object
			// because it has to be "rolled back" manually when an exception happens during posting
			Product product = productDao.get(item.getProduct().getId());
			if (!product.hasAvailableUnitQuantity(item.getUnit(), item.getQuantity())) {
				exception.add(new SalesRequisitionItemNotEnoughStocksException(item));
			} else if (exception.isEmpty()) { // No need to update anymore if validation error already encountered
				product.subtractUnitQuantity(item.getUnit(), item.getQuantity());
				productDao.updateAvailableQuantities(product);
			}
		}
		
		if (!exception.isEmpty()) {
			throw exception;
		}
		
		updated.setPosted(true);
		salesRequisitionDao.save(updated);

		SalesInvoice salesInvoice = updated.createSalesInvoice();
		salesInvoice.setPostedBy(loginService.getLoggedInUser());
		salesInvoice.setVatAmount(salesInvoice.getTotalNetAmount().multiply(systemService.getVatRate())
				.setScale(2, RoundingMode.HALF_UP));
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
