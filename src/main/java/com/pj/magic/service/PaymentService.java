package com.pj.magic.service;

import java.util.List;

import com.pj.magic.model.Customer;
import com.pj.magic.model.Payment;
import com.pj.magic.model.PaymentCheckPayment;
import com.pj.magic.model.PaymentSalesInvoice;
import com.pj.magic.model.SalesInvoice;

public interface PaymentService {

	List<SalesInvoice> findAllUnpaidSalesInvoicesByCustomer(Customer customer);

	void save(Payment payment);

	Payment getPayment(long id);

	List<Payment> getAllNewPayments();

	void save(PaymentSalesInvoice paymentSalesInvoice);
	
	List<PaymentSalesInvoice> findAllPaymentSalesInvoicesByPayment(Payment payment);

	void save(PaymentCheckPayment check);

	void delete(Payment payment);

	void delete(PaymentSalesInvoice paymentSalesInvoice);
	
}
