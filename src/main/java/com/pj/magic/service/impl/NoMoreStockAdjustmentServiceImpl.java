package com.pj.magic.service.impl;

import java.util.Date;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pj.magic.dao.NoMoreStockAdjustmentDao;
import com.pj.magic.dao.NoMoreStockAdjustmentItemDao;
import com.pj.magic.dao.PaymentTerminalAssignmentDao;
import com.pj.magic.dao.ProductDao;
import com.pj.magic.exception.AlreadyPaidException;
import com.pj.magic.exception.AlreadyPostedException;
import com.pj.magic.model.NoMoreStockAdjustment;
import com.pj.magic.model.NoMoreStockAdjustmentItem;
import com.pj.magic.model.Payment;
import com.pj.magic.model.PaymentTerminalAssignment;
import com.pj.magic.model.SalesInvoice;
import com.pj.magic.model.User;
import com.pj.magic.model.search.NoMoreStockAdjustmentSearchCriteria;
import com.pj.magic.service.LoginService;
import com.pj.magic.service.NoMoreStockAdjustmentService;
import com.pj.magic.service.SalesInvoiceService;

@Service
public class NoMoreStockAdjustmentServiceImpl implements NoMoreStockAdjustmentService {

	@Autowired private NoMoreStockAdjustmentDao noMoreStockAdjustmentDao;
	@Autowired private NoMoreStockAdjustmentItemDao noMoreStockAdjustmentItemDao;
	@Autowired private SalesInvoiceService salesInvoiceService;
	@Autowired private ProductDao productDao;
	@Autowired private LoginService loginService;
	@Autowired private PaymentTerminalAssignmentDao paymentTerminalAssignmentDao;
	
	@Override
	public List<NoMoreStockAdjustment> getNewNoMoreStockAdjustments() {
		NoMoreStockAdjustmentSearchCriteria criteria = new NoMoreStockAdjustmentSearchCriteria();
		criteria.setPosted(false);
		
		List<NoMoreStockAdjustment> noMoreStockAdjustments = noMoreStockAdjustmentDao.search(criteria);
		for (NoMoreStockAdjustment noMoreStockAdjustment : noMoreStockAdjustments) {
			noMoreStockAdjustment.setItems(noMoreStockAdjustmentItemDao.findAllByNoMoreStockAdjustment(noMoreStockAdjustment));
		}
		return noMoreStockAdjustments;
	}

	@Transactional
	@Override
	public void save(NoMoreStockAdjustment noMoreStockAdjustment) {
		boolean newNoMoreStockAdjustment = (noMoreStockAdjustment.getId() == null);
		noMoreStockAdjustmentDao.save(noMoreStockAdjustment);
		if (!newNoMoreStockAdjustment) {
			noMoreStockAdjustmentItemDao.deleteAllByNoMoreStockAdjustment(noMoreStockAdjustment);
		}
	}

	@Override
	public NoMoreStockAdjustment getNoMoreStockAdjustment(long id) {
		NoMoreStockAdjustment noMoreStockAdjustment = noMoreStockAdjustmentDao.get(id);
		if (noMoreStockAdjustment != null) {
			noMoreStockAdjustment.setItems(noMoreStockAdjustmentItemDao.findAllByNoMoreStockAdjustment(noMoreStockAdjustment));
			noMoreStockAdjustment.setSalesInvoice(salesInvoiceService.get(noMoreStockAdjustment.getSalesInvoice().getId()));
		}
		return noMoreStockAdjustment;
	}

	@Transactional
	@Override
	public void save(NoMoreStockAdjustmentItem item) {
		noMoreStockAdjustmentItemDao.save(item);
	}

	@Transactional
	@Override
	public void delete(NoMoreStockAdjustmentItem item) {
		noMoreStockAdjustmentItemDao.delete(item);
	}

	@Transactional
	@Override
	public void post(NoMoreStockAdjustment noMoreStockAdjustment) {
		NoMoreStockAdjustment updated = noMoreStockAdjustmentDao.get(noMoreStockAdjustment.getId());
		
		if (updated.isPosted()) {
			throw new AlreadyPostedException("No More Stock Adjustment already posted. NMS No.: " + 
					noMoreStockAdjustment.getNoMoreStockAdjustmentNumber());
		}
		
		updated.setPosted(true);
		updated.setPostDate(new Date());
		updated.setPostedBy(loginService.getLoggedInUser());
		noMoreStockAdjustmentDao.save(updated);
	}

	@Override
	public List<NoMoreStockAdjustment> search(NoMoreStockAdjustmentSearchCriteria criteria) {
		List<NoMoreStockAdjustment> noMoreStockAdjustments = noMoreStockAdjustmentDao.search(criteria);
		for (NoMoreStockAdjustment noMoreStockAdjustment : noMoreStockAdjustments) {
			noMoreStockAdjustment.setItems(noMoreStockAdjustmentItemDao.findAllByNoMoreStockAdjustment(noMoreStockAdjustment));
		}
		return noMoreStockAdjustments;
 	}

	@Override
	public List<NoMoreStockAdjustment> findPostedNoMoreStockAdjustmentsBySalesInvoice(SalesInvoice salesInvoice) {
		NoMoreStockAdjustmentSearchCriteria criteria = new NoMoreStockAdjustmentSearchCriteria();
		criteria.setSalesInvoice(salesInvoice);
		criteria.setPosted(true);
		
		return search(criteria);
	}

	@Override
	public List<NoMoreStockAdjustment> findAllPaymentNoMoreStockAdjustments(Payment payment, SalesInvoice salesInvoice) {
		NoMoreStockAdjustmentSearchCriteria criteria = new NoMoreStockAdjustmentSearchCriteria();
		criteria.setPayment(payment);
		criteria.setSalesInvoice(salesInvoice);
		
		return search(criteria);
	}

	@Transactional
	@Override
	public void markAsPaid(NoMoreStockAdjustment noMoreStockAdjustment) {
		NoMoreStockAdjustment updated = noMoreStockAdjustmentDao.get(noMoreStockAdjustment.getId());
		if (updated.isPaid()) {
			throw new AlreadyPaidException("No More Stock Adjustment already paid. NMS No.: " + 
					noMoreStockAdjustment.getNoMoreStockAdjustmentNumber());
		}
		
		User user = loginService.getLoggedInUser();
		PaymentTerminalAssignment paymentTerminalAssignment = paymentTerminalAssignmentDao.findByUser(user);
		if (paymentTerminalAssignment == null) {
			throw new RuntimeException("User " + user.getUsername() + " is not assigned to payment terminal");
		}
		
		updated.setPaid(true);
		updated.setPaidDate(new Date());
		updated.setPaidBy(loginService.getLoggedInUser());
		updated.setPaymentTerminal(paymentTerminalAssignment.getPaymentTerminal());
		noMoreStockAdjustmentDao.save(updated);
	}

	@Override
	public NoMoreStockAdjustment findNoMoreStockAdjustmentByNoMoreStockAdjustmentNumber(long noMoreStockAdjustmentNumber) {
		NoMoreStockAdjustment noMoreStockAdjustment = noMoreStockAdjustmentDao.findByNoMoreStockAdjustmentNumber(noMoreStockAdjustmentNumber);
		if (noMoreStockAdjustment != null) {
			noMoreStockAdjustment.setItems(noMoreStockAdjustmentItemDao.findAllByNoMoreStockAdjustment(noMoreStockAdjustment));
			noMoreStockAdjustment.setSalesInvoice(salesInvoiceService.get(noMoreStockAdjustment.getSalesInvoice().getId()));
		}
		return noMoreStockAdjustment;
	}

	@Override
	public List<NoMoreStockAdjustment> getUnpaidNoMoreStockAdjustments() {
		NoMoreStockAdjustmentSearchCriteria criteria = new NoMoreStockAdjustmentSearchCriteria();
		criteria.setPaid(false);
		
		List<NoMoreStockAdjustment> noMoreStockAdjustments = noMoreStockAdjustmentDao.search(criteria);
		for (NoMoreStockAdjustment noMoreStockAdjustment : noMoreStockAdjustments) {
			noMoreStockAdjustment.setItems(noMoreStockAdjustmentItemDao.findAllByNoMoreStockAdjustment(noMoreStockAdjustment));
		}
		return noMoreStockAdjustments;
	}

}