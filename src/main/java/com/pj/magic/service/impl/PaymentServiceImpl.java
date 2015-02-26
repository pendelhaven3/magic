package com.pj.magic.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pj.magic.dao.BadStockReturnDao;
import com.pj.magic.dao.CustomerDao;
import com.pj.magic.dao.NoMoreStockAdjustmentDao;
import com.pj.magic.dao.PaymentAdjustmentDao;
import com.pj.magic.dao.PaymentCashPaymentDao;
import com.pj.magic.dao.PaymentCheckPaymentDao;
import com.pj.magic.dao.PaymentDao;
import com.pj.magic.dao.PaymentPaymentAdjustmentDao;
import com.pj.magic.dao.PaymentSalesInvoiceDao;
import com.pj.magic.dao.PaymentTerminalAssignmentDao;
import com.pj.magic.dao.SalesInvoiceDao;
import com.pj.magic.dao.SalesInvoiceItemDao;
import com.pj.magic.dao.SalesReturnDao;
import com.pj.magic.model.AdjustmentType;
import com.pj.magic.model.BadStockReturn;
import com.pj.magic.model.Customer;
import com.pj.magic.model.NoMoreStockAdjustment;
import com.pj.magic.model.Payment;
import com.pj.magic.model.PaymentAdjustment;
import com.pj.magic.model.PaymentCashPayment;
import com.pj.magic.model.PaymentCheckPayment;
import com.pj.magic.model.PaymentPaymentAdjustment;
import com.pj.magic.model.PaymentSalesInvoice;
import com.pj.magic.model.PaymentTerminalAssignment;
import com.pj.magic.model.SalesInvoice;
import com.pj.magic.model.SalesReturn;
import com.pj.magic.model.User;
import com.pj.magic.model.search.PaymentCashPaymentSearchCriteria;
import com.pj.magic.model.search.PaymentCheckPaymentSearchCriteria;
import com.pj.magic.model.search.PaymentSalesInvoiceSearchCriteria;
import com.pj.magic.model.search.PaymentSearchCriteria;
import com.pj.magic.model.search.SalesInvoiceSearchCriteria;
import com.pj.magic.model.search.SalesReturnSearchCriteria;
import com.pj.magic.service.LoginService;
import com.pj.magic.service.PaymentService;
import com.pj.magic.service.SalesReturnService;

@Service
public class PaymentServiceImpl implements PaymentService {

	@Autowired private SalesInvoiceItemDao salesInvoiceItemDao;
	@Autowired private PaymentDao paymentDao;
	@Autowired private PaymentSalesInvoiceDao paymentSalesInvoiceDao;
	@Autowired private CustomerDao customerDao;
	@Autowired private PaymentCheckPaymentDao paymentCheckPaymentDao;
	@Autowired private PaymentCashPaymentDao paymentCashPaymentDao;
	@Autowired private PaymentPaymentAdjustmentDao paymentPaymentAdjustmentDao;
	@Autowired private PaymentTerminalAssignmentDao paymentTerminalAssignmentDao;
	@Autowired private SalesReturnDao salesReturnDao;
	@Autowired private LoginService loginService;
	@Autowired private SalesReturnService salesReturnService;
	@Autowired private BadStockReturnDao badStockReturnDao;
	@Autowired private NoMoreStockAdjustmentDao noMoreStockAdjustmentDao;
	@Autowired private PaymentAdjustmentDao paymentAdjustmentDao;
	@Autowired private SalesInvoiceDao salesInvoiceDao;
	
	@Transactional
	@Override
	public void save(Payment payment) {
		boolean newPayment = (payment.getId() == null);
		if (newPayment) {
			payment.setEncoder(loginService.getLoggedInUser());
		}
		paymentDao.save(payment);
		if (!newPayment) {
			paymentSalesInvoiceDao.deleteAllByPayment(payment);
		}
	}

	@Override
	public Payment getPayment(long id) {
		Payment payment = paymentDao.get(id);
		loadPaymentDetails(payment);
		return payment;
	}
	
	private void loadPaymentDetails(Payment payment) {
		payment.setSalesInvoices(paymentSalesInvoiceDao.findAllByPayment(payment));
		for (PaymentSalesInvoice salesInvoice : payment.getSalesInvoices()) {
			salesInvoice.getSalesInvoice().setItems(
					salesInvoiceItemDao.findAllBySalesInvoice(salesInvoice.getSalesInvoice()));
			if (payment.isNew()) {
				salesInvoice.setSalesReturns(
						findPostedSalesReturnsBySalesInvoice(salesInvoice.getSalesInvoice()));
			} else {
				salesInvoice.setSalesReturns(
						salesReturnService.findAllPaymentSalesReturns(payment, salesInvoice.getSalesInvoice()));
			}
		}
		payment.setCashPayments(paymentCashPaymentDao.findAllByPayment(payment));
		payment.setCheckPayments(paymentCheckPaymentDao.findAllByPayment(payment));
		payment.setAdjustments(paymentPaymentAdjustmentDao.findAllByPayment(payment));
	}

	private List<SalesReturn> findPostedSalesReturnsBySalesInvoice(SalesInvoice salesInvoice) {
		SalesReturnSearchCriteria criteria = new SalesReturnSearchCriteria();
		criteria.setSalesInvoice(salesInvoice);
		criteria.setPosted(true);
		criteria.setPaid(false);
		
		return salesReturnService.search(criteria);
	}

	@Override
	public List<Payment> getAllNewPayments() {
		PaymentSearchCriteria criteria = new PaymentSearchCriteria();
		criteria.setPosted(false);
		criteria.setCancelled(false);
		return searchPayments(criteria);
	}

	@Transactional
	@Override
	public void save(PaymentSalesInvoice paymentSalesInvoice) {
		paymentSalesInvoiceDao.save(paymentSalesInvoice);
	}

	@Override
	public List<PaymentSalesInvoice> findAllPaymentSalesInvoicesByPayment(Payment payment) {
		List<PaymentSalesInvoice> paymentSalesInvoices = paymentSalesInvoiceDao.findAllByPayment(payment);
		for (PaymentSalesInvoice salesInvoice : paymentSalesInvoices) {
			salesInvoice.getSalesInvoice().setItems(
					salesInvoiceItemDao.findAllBySalesInvoice(salesInvoice.getSalesInvoice()));
		}
		return paymentSalesInvoices;
	}

	@Transactional
	@Override
	public void save(PaymentCheckPayment check) {
		paymentCheckPaymentDao.save(check);
	}

	@Transactional
	@Override
	public void delete(PaymentSalesInvoice paymentSalesInvoice) {
		paymentSalesInvoiceDao.delete(paymentSalesInvoice);
	}

	@Transactional
	@Override
	public void save(PaymentCashPayment cashPayment) {
		paymentCashPaymentDao.save(cashPayment);
	}

	@Transactional
	@Override
	public void delete(PaymentCheckPayment checkPayment) {
		paymentCheckPaymentDao.delete(checkPayment);
	}

	@Transactional
	@Override
	public void delete(PaymentCashPayment cashPayment) {
		paymentCashPaymentDao.delete(cashPayment);
	}

	@Transactional
	@Override
	public void delete(PaymentPaymentAdjustment adjustment) {
		paymentPaymentAdjustmentDao.delete(adjustment);
	}

	@Transactional
	@Override
	public void save(PaymentPaymentAdjustment adjustment) {
		paymentPaymentAdjustmentDao.save(adjustment);
	}

	@Transactional
	@Override
	public void post(Payment payment) {
		User user = loginService.getLoggedInUser();
		PaymentTerminalAssignment paymentTerminalAssignment = paymentTerminalAssignmentDao.findByUser(user);
		if (paymentTerminalAssignment == null) {
			throw new RuntimeException("User " + user.getUsername() + " is not assigned to payment terminal");
		}
		
		Payment updated = getPayment(payment.getId());
		updated.setPosted(true);
		updated.setPostDate(new Date());
		updated.setPostedBy(user);
		updated.setPaymentTerminal(paymentTerminalAssignment.getPaymentTerminal());
		paymentDao.save(updated);
		
		for (PaymentSalesInvoice salesInvoice : updated.getSalesInvoices()) {
			for (SalesReturn salesReturn : salesInvoice.getSalesReturns()) {
				salesReturnDao.savePaymentSalesReturn(updated, salesReturn);
				
				salesReturn.setPaid(true);
				salesReturn.setPaidDate(new Date());
				salesReturn.setPaidBy(loginService.getLoggedInUser());
				salesReturn.setPaymentTerminal(paymentTerminalAssignment.getPaymentTerminal());
				salesReturnDao.save(salesReturn);
			}
		}
		
		for (PaymentPaymentAdjustment adjustment : updated.getAdjustments()) {
			if (adjustment.getReferenceNumber() == null) {
				continue;
			}
			
			long referenceNumber = Long.valueOf(adjustment.getReferenceNumber());
			switch (adjustment.getAdjustmentType().getCode()) {
			case AdjustmentType.SALES_RETURN_CODE:
				SalesReturn salesReturn = salesReturnDao.findBySalesReturnNumber(referenceNumber);
				if (salesReturn.isPaid()) {
					throw new RuntimeException("Sales Return " + salesReturn.getSalesReturnNumber() + " is already paid");
				}
				
				salesReturn.setPaid(true);
				salesReturn.setPaidDate(new Date());
				salesReturn.setPaidBy(loginService.getLoggedInUser());
				salesReturn.setPaymentTerminal(paymentTerminalAssignment.getPaymentTerminal());
				salesReturnDao.save(salesReturn);
				break;
			case AdjustmentType.BAD_STOCK_RETURN_CODE:
				BadStockReturn badStockReturn = badStockReturnDao.findByBadStockReturnNumber(referenceNumber);
				if (badStockReturn.isPaid()) {
					throw new RuntimeException("Bad Stock Return " + badStockReturn.getBadStockReturnNumber() + " is already paid");
				}
				
				badStockReturn.setPaid(true);
				badStockReturn.setPaidDate(new Date());
				badStockReturn.setPaidBy(loginService.getLoggedInUser());
				badStockReturn.setPaymentTerminal(paymentTerminalAssignment.getPaymentTerminal());
				badStockReturnDao.save(badStockReturn);
				break;
			case AdjustmentType.NO_MORE_STOCK_ADJUSTMENT_CODE:
				NoMoreStockAdjustment noMoreStockAdjustment = noMoreStockAdjustmentDao.findByNoMoreStockAdjustmentNumber(referenceNumber);
				if (noMoreStockAdjustment.isPaid()) {
					throw new RuntimeException("No More Stock Adjustment " + 
						noMoreStockAdjustment.getNoMoreStockAdjustmentNumber() + " is already paid");
				}
				
				noMoreStockAdjustment.setPaid(true);
				noMoreStockAdjustment.setPaidDate(new Date());
				noMoreStockAdjustment.setPaidBy(loginService.getLoggedInUser());
				noMoreStockAdjustment.setPaymentTerminal(paymentTerminalAssignment.getPaymentTerminal());
				noMoreStockAdjustmentDao.save(noMoreStockAdjustment);
				break;
			default:
				PaymentAdjustment paymentAdjustment = 
					paymentAdjustmentDao.findByPaymentAdjustmentNumber(referenceNumber);
				if (paymentAdjustment.isPaid()) {
					throw new RuntimeException("Payment Adjustment " + 
							paymentAdjustment.getPaymentAdjustmentNumber() + " is already paid");
				}
				
				paymentAdjustment.setPaid(true);
				paymentAdjustment.setPaidDate(new Date());
				paymentAdjustment.setPaidBy(loginService.getLoggedInUser());
				paymentAdjustment.setPaymentTerminal(paymentTerminalAssignment.getPaymentTerminal());
				paymentAdjustmentDao.save(paymentAdjustment);
				break;
			}
		}
	}

	@Override
	public List<Payment> searchPayments(PaymentSearchCriteria criteria) {
		return paymentDao.search(criteria);
	}

	@Override
	public List<PaymentSalesInvoice> searchPaymentSalesInvoices(
			PaymentSalesInvoiceSearchCriteria criteria) {
		List<PaymentSalesInvoice> paymentSalesInvoices = paymentSalesInvoiceDao.search(criteria);
		for (PaymentSalesInvoice salesInvoice : paymentSalesInvoices) {
			salesInvoice.getSalesInvoice().setItems(
					salesInvoiceItemDao.findAllBySalesInvoice(salesInvoice.getSalesInvoice()));
		}
		return paymentSalesInvoices;
	}

	@Transactional
	@Override
	public void cancel(Payment payment) {
		payment.setCancelled(true);
		payment.setCancelDate(new Date());
		payment.setCancelledBy(loginService.getLoggedInUser());
		paymentDao.save(payment);
	}

	@Override
	public List<PaymentCashPayment> searchPaymentCashPayments(
			PaymentCashPaymentSearchCriteria criteria) {
		return paymentCashPaymentDao.search(criteria);
	}

	@Override
	public List<PaymentCheckPayment> searchPaymentCheckPayments(
			PaymentCheckPaymentSearchCriteria criteria) {
		return paymentCheckPaymentDao.search(criteria);
	}

	@Override
	public List<PaymentSalesInvoice> findAllUnpaidSalesInvoices(Customer customer) {
		SalesInvoiceSearchCriteria criteria = new SalesInvoiceSearchCriteria();
		criteria.setCustomer(customer);
		criteria.setPaid(false);
		criteria.setOrderBy("a.TRANSACTION_DT, a.SALES_INVOICE_NO");
		
		List<SalesInvoice> salesInvoices = salesInvoiceDao.search(criteria);
		for (SalesInvoice salesInvoice : salesInvoices) {
			salesInvoice.setItems(salesInvoiceItemDao.findAllBySalesInvoice(salesInvoice));
		}
		
		List<PaymentSalesInvoice> paymentSalesInvoices = new ArrayList<>();
		for (SalesInvoice salesInvoice : salesInvoices) {
			PaymentSalesInvoice paymentSalesInvoice = new PaymentSalesInvoice();
			paymentSalesInvoice.setSalesInvoice(salesInvoice);
			paymentSalesInvoice.setSalesReturns(findPostedSalesReturns(salesInvoice));
			paymentSalesInvoices.add(paymentSalesInvoice);
		}
		
		return paymentSalesInvoices;
	}

	private List<SalesReturn> findPostedSalesReturns(SalesInvoice salesInvoice) {
		SalesReturnSearchCriteria criteria = new SalesReturnSearchCriteria();
		criteria.setSalesInvoice(salesInvoice);
		criteria.setPosted(true);
		return salesReturnService.search(criteria);
	}
	
}