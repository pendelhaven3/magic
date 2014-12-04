package com.pj.magic.service.impl;

import java.util.Collections;
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
import com.pj.magic.model.Customer;
import com.pj.magic.model.Product;
import com.pj.magic.model.SalesInvoice;
import com.pj.magic.model.SalesInvoiceItem;
import com.pj.magic.model.SalesRequisition;
import com.pj.magic.model.SalesRequisitionItem;
import com.pj.magic.model.search.SalesInvoiceSearchCriteria;
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
		salesInvoice.setPostDate(new Date());
		salesInvoice.setPostedBy(loginService.getLoggedInUser());
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
		Collections.sort(salesInvoice.getItems());
		salesInvoice.setCustomer(customerDao.get(salesInvoice.getCustomer().getId())); // TODO: Review next
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
		salesRequisition.setTransactionDate(salesInvoice.getTransactionDate());
		salesRequisitionDao.save(salesRequisition);
		
		for (SalesRequisitionItem item : salesRequisition.getItems()) {
			salesRequisitionItemDao.save(item);
		}
		
		return salesRequisition;
	}

	@Transactional
	@Override
	public void mark(SalesInvoice salesInvoice) {
		salesInvoice.setMarked(true);
		salesInvoice.setMarkDate(new Date());
		salesInvoice.setMarkedBy(loginService.getLoggedInUser());
		salesInvoiceDao.save(salesInvoice);
	}

	@Transactional
	@Override
	public void cancel(SalesInvoice salesInvoice) {
		salesInvoice.setCancelled(true);
		salesInvoice.setCancelDate(new Date());
		salesInvoice.setCancelledBy(loginService.getLoggedInUser());
		salesInvoiceDao.save(salesInvoice);
		
		for (SalesInvoiceItem item : salesInvoice.getItems()) {
			Product product = productDao.get(item.getProduct().getId());
			product.addUnitQuantity(item.getUnit(), item.getQuantity());
			productDao.updateAvailableQuantities(product);
		}
	}

	@Override
	public List<SalesInvoice> getNewSalesInvoices() {
		SalesInvoiceSearchCriteria criteria = new SalesInvoiceSearchCriteria();
		criteria.setMarked(false);
		criteria.setCancelled(false);
		
		List<SalesInvoice> salesInvoices = salesInvoiceDao.search(criteria);
		for (SalesInvoice salesInvoice : salesInvoices) {
			salesInvoice.setItems(salesInvoiceItemDao.findAllBySalesInvoice(salesInvoice));
		}
		return salesInvoices;
	}

	@Transactional
	@Override
	public void markOrCancelSalesInvoices(List<SalesInvoice> salesInvoices) {
		for (SalesInvoice salesInvoice : salesInvoices) {
			if (salesInvoice.isMarked()) {
				mark(salesInvoice);
			} else if (salesInvoice.isCancelled()) {
				cancel(salesInvoice);
			}
		}
	}

	@Override
	public void save(SalesInvoiceItem item) {
		salesInvoiceItemDao.save(item);
	}

	@Override
	public List<SalesInvoice> search(SalesInvoiceSearchCriteria criteria) {
		List<SalesInvoice> salesInvoices = salesInvoiceDao.search(criteria);
		for (SalesInvoice salesInvoice : salesInvoices) {
			salesInvoice.setItems(salesInvoiceItemDao.findAllBySalesInvoice(salesInvoice));
		}
		return salesInvoices;
	}

	@Override
	public List<SalesInvoice> getAllNewSalesInvoices() {
		SalesInvoiceSearchCriteria criteria = new SalesInvoiceSearchCriteria();
		criteria.setMarked(false);
		criteria.setCancelled(false);
		
		List<SalesInvoice> salesInvoices = salesInvoiceDao.search(criteria);
		for (SalesInvoice salesInvoice : salesInvoices) {
			salesInvoice.setItems(salesInvoiceItemDao.findAllBySalesInvoice(salesInvoice));
		}
		return salesInvoices;
	}

	@Override
	public SalesInvoice findBySalesInvoiceNumber(long salesInvoiceNumber) {
		SalesInvoice salesInvoice = salesInvoiceDao.findBySalesInvoiceNumber(salesInvoiceNumber);
		salesInvoice.setItems(salesInvoiceItemDao.findAllBySalesInvoice(salesInvoice));
		return salesInvoice;
	}

	@Override
	public List<SalesInvoice> findAllSalesInvoicesForPaymentByCustomer(Customer customer) {
		List<SalesInvoice> salesInvoices = salesInvoiceDao.findAllForPaymentByCustomer(customer);
		for (SalesInvoice salesInvoice : salesInvoices) {
			salesInvoice.setItems(salesInvoiceItemDao.findAllBySalesInvoice(salesInvoice));
		}
		return salesInvoices;
	}

	@Override
	public List<SalesInvoice> findAllUnpaidSalesInvoices() {
		List<SalesInvoice> salesInvoices = salesInvoiceDao.findAllUnpaid();
		for (SalesInvoice salesInvoice : salesInvoices) {
			salesInvoice.setItems(salesInvoiceItemDao.findAllBySalesInvoice(salesInvoice));
		}
		return salesInvoices;
	}
	
}