package com.pj.magic.service.impl;

import java.util.Date;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pj.magic.dao.CustomerDao;
import com.pj.magic.dao.PaymentAdjustmentDao;
import com.pj.magic.dao.PaymentCashPaymentDao;
import com.pj.magic.dao.PaymentCheckPaymentDao;
import com.pj.magic.dao.PaymentDao;
import com.pj.magic.dao.PaymentSalesInvoiceDao;
import com.pj.magic.dao.SalesInvoiceDao;
import com.pj.magic.dao.SalesInvoiceItemDao;
import com.pj.magic.model.Customer;
import com.pj.magic.model.Payment;
import com.pj.magic.model.PaymentAdjustment;
import com.pj.magic.model.PaymentCashPayment;
import com.pj.magic.model.PaymentCheckPayment;
import com.pj.magic.model.PaymentSalesInvoice;
import com.pj.magic.model.SalesInvoice;
import com.pj.magic.model.search.PaymentSalesInvoiceSearchCriteria;
import com.pj.magic.model.search.PaymentSearchCriteria;
import com.pj.magic.service.LoginService;
import com.pj.magic.service.PaymentService;

@Service
public class PaymentServiceImpl implements PaymentService {

	@Autowired private SalesInvoiceDao salesInvoiceDao;
	@Autowired private SalesInvoiceItemDao salesInvoiceItemDao;
	@Autowired private PaymentDao paymentDao;
	@Autowired private PaymentSalesInvoiceDao paymentSalesInvoiceDao;
	@Autowired private CustomerDao customerDao;
	@Autowired private PaymentCheckPaymentDao paymentCheckPaymentDao;
	@Autowired private PaymentCashPaymentDao paymentCashPaymentDao;
	@Autowired private PaymentAdjustmentDao paymentAdjustmentDao;
	@Autowired private LoginService loginService;
	
	@Override
	public List<SalesInvoice> findAllUnpaidSalesInvoicesByCustomer(Customer customer) {
		List<SalesInvoice> salesInvoices = salesInvoiceDao.findAllUnpaidByCustomer(customer);
		for (SalesInvoice salesInvoice : salesInvoices) {
			salesInvoice.setItems(salesInvoiceItemDao.findAllBySalesInvoice(salesInvoice));
		}
		return salesInvoices;
	}

	@Transactional
	@Override
	public void save(Payment payment) {
		boolean newPayment = (payment.getId() == null);
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
		}
		payment.setCashPayments(paymentCashPaymentDao.findAllByPayment(payment));
		payment.setCheckPayments(paymentCheckPaymentDao.findAllByPayment(payment));
		payment.setAdjustments(paymentAdjustmentDao.findAllByPayment(payment));
	}

	@Override
	public List<Payment> getAllNewPayments() {
		PaymentSearchCriteria criteria = new PaymentSearchCriteria();
		criteria.setPosted(false);
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
	public void delete(Payment payment) {
		paymentCheckPaymentDao.deleteAllByPayment(payment);
		paymentSalesInvoiceDao.deleteAllByPayment(payment);
		paymentCashPaymentDao.deleteAllByPayment(payment);
		paymentAdjustmentDao.deleteAllByPayment(payment);
		paymentDao.delete(payment);
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
	public void delete(PaymentAdjustment adjustment) {
		paymentAdjustmentDao.delete(adjustment);
	}

	@Transactional
	@Override
	public void save(PaymentAdjustment adjustment) {
		paymentAdjustmentDao.save(adjustment);
	}

	@Transactional
	@Override
	public void post(Payment payment) {
		payment.setPosted(true);
		payment.setPostDate(new Date());
		payment.setPostedBy(loginService.getLoggedInUser());
		paymentDao.save(payment);
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
	
}