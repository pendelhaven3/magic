package com.pj.magic.service.impl;

import java.util.Date;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pj.magic.dao.PaymentTerminalAssignmentDao;
import com.pj.magic.dao.ProductDao;
import com.pj.magic.dao.SalesReturnDao;
import com.pj.magic.dao.SalesReturnItemDao;
import com.pj.magic.model.Payment;
import com.pj.magic.model.PaymentTerminalAssignment;
import com.pj.magic.model.Product;
import com.pj.magic.model.SalesInvoice;
import com.pj.magic.model.SalesReturn;
import com.pj.magic.model.SalesReturnItem;
import com.pj.magic.model.User;
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
	@Autowired private PaymentTerminalAssignmentDao paymentTerminalAssignmentDao;
	
	@Override
	public List<SalesReturn> getNewSalesReturns() {
		SalesReturnSearchCriteria criteria = new SalesReturnSearchCriteria();
		criteria.setPosted(false);
		
		List<SalesReturn> salesReturns = salesReturnDao.search(criteria);
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

	@Override
	public List<SalesReturn> findPostedSalesReturnsBySalesInvoice(SalesInvoice salesInvoice) {
		SalesReturnSearchCriteria criteria = new SalesReturnSearchCriteria();
		criteria.setSalesInvoice(salesInvoice);
		criteria.setPosted(true);
		
		return search(criteria);
	}

	@Override
	public List<SalesReturn> findAllPaymentSalesReturns(Payment payment, SalesInvoice salesInvoice) {
		SalesReturnSearchCriteria criteria = new SalesReturnSearchCriteria();
		criteria.setPayment(payment);
		criteria.setSalesInvoice(salesInvoice);
		
		return search(criteria);
	}

	@Transactional
	@Override
	public void markAsPaid(SalesReturn salesReturn) {
		User user = loginService.getLoggedInUser();
		PaymentTerminalAssignment paymentTerminalAssignment = paymentTerminalAssignmentDao.findByUser(user);
		if (paymentTerminalAssignment == null) {
			throw new RuntimeException("User " + user.getUsername() + " is not assigned to payment terminal");
		}
		
		SalesReturn updated = salesReturnDao.get(salesReturn.getId());
		updated.setPaid(true);
		updated.setPaidDate(new Date());
		updated.setPaidBy(loginService.getLoggedInUser());
		updated.setPaymentTerminal(paymentTerminalAssignment.getPaymentTerminal());
		salesReturnDao.save(updated);
	}

}