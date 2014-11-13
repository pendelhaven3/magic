package com.pj.magic.service.impl;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.transaction.Transactional;

import org.apache.commons.lang.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pj.magic.dao.CustomerDao;
import com.pj.magic.dao.PaymentDao;
import com.pj.magic.dao.PaymentItemDao;
import com.pj.magic.dao.SalesInvoiceDao;
import com.pj.magic.dao.SalesInvoiceItemDao;
import com.pj.magic.model.Customer;
import com.pj.magic.model.Payment;
import com.pj.magic.model.PaymentItem;
import com.pj.magic.model.SalesInvoice;
import com.pj.magic.service.PaymentService;

@Service
public class PaymentServiceImpl implements PaymentService {

	@Autowired private SalesInvoiceDao salesInvoiceDao;
	@Autowired private SalesInvoiceItemDao salesInvoiceItemDao;
	@Autowired private PaymentDao paymentDao;
	@Autowired private PaymentItemDao paymentItemDao;
	@Autowired private CustomerDao customerDao;
	
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
		payment.setPaymentDate(new Date());
		paymentDao.save(payment);
		for (PaymentItem item : payment.getItems()) {
			paymentItemDao.save(item);
		}
	}

	@Override
	public Payment getPayment(long id) {
		Payment payment = paymentDao.get(id);
		loadPaymentDetails(payment);
		return payment;
	}
	
	private void loadPaymentDetails(Payment payment) {
		payment.setItems(paymentItemDao.findAllByPayment(payment));
		for (PaymentItem item : payment.getItems()) {
			item.getSalesInvoice().setItems(
					salesInvoiceItemDao.findAllBySalesInvoice(item.getSalesInvoice()));
		}
		payment.setCustomer(customerDao.get(payment.getCustomer().getId()));
	}

	@Override
	public List<Payment> getAllPaymentsForToday() {
		List<Payment> payments = paymentDao.findAllByPaymentDate(DateUtils.truncate(new Date(), Calendar.DATE));
		for (Payment payment : payments) {
			loadPaymentDetails(payment);
		}
		return payments;
	}

}