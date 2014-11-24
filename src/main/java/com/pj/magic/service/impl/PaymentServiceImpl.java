package com.pj.magic.service.impl;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pj.magic.dao.CustomerDao;
import com.pj.magic.dao.PaymentCheckPaymentDao;
import com.pj.magic.dao.PaymentDao;
import com.pj.magic.dao.PaymentSalesInvoiceDao;
import com.pj.magic.dao.SalesInvoiceDao;
import com.pj.magic.dao.SalesInvoiceItemDao;
import com.pj.magic.model.Customer;
import com.pj.magic.model.Payment;
import com.pj.magic.model.PaymentCheckPayment;
import com.pj.magic.model.PaymentSalesInvoice;
import com.pj.magic.model.SalesInvoice;
import com.pj.magic.model.search.PaymentSearchCriteria;
import com.pj.magic.service.PaymentService;

@Service
public class PaymentServiceImpl implements PaymentService {

	@Autowired private SalesInvoiceDao salesInvoiceDao;
	@Autowired private SalesInvoiceItemDao salesInvoiceItemDao;
	@Autowired private PaymentDao paymentDao;
	@Autowired private PaymentSalesInvoiceDao paymentSalesInvoiceDao;
	@Autowired private CustomerDao customerDao;
	@Autowired private PaymentCheckPaymentDao paymentCheckPaymentDao;
	
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
		paymentDao.save(payment);
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
		payment.setChecks(paymentCheckPaymentDao.findAllByPayment(payment));
	}

	@Override
	public List<Payment> getAllNewPayments() {
		PaymentSearchCriteria criteria = new PaymentSearchCriteria();
		criteria.setPosted(false);
		return paymentDao.search(criteria);
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
		paymentDao.delete(payment);
	}

	@Transactional
	@Override
	public void delete(PaymentSalesInvoice paymentSalesInvoice) {
		paymentSalesInvoiceDao.delete(paymentSalesInvoice);
	}
	
}