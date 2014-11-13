package com.pj.magic.service;

import java.util.List;

import com.pj.magic.model.Customer;
import com.pj.magic.model.Payment;
import com.pj.magic.model.SalesInvoice;

public interface PaymentService {

	List<SalesInvoice> findAllUnpaidSalesInvoicesByCustomer(Customer customer);

	void save(Payment payment);

	Payment getPayment(long id);

	List<Payment> getAllPaymentsForToday();
	
}
