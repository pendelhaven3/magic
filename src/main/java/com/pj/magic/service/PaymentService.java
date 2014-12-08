package com.pj.magic.service;

import java.util.List;

import com.pj.magic.model.Payment;
import com.pj.magic.model.PaymentAdjustment;
import com.pj.magic.model.PaymentCashPayment;
import com.pj.magic.model.PaymentCheckPayment;
import com.pj.magic.model.PaymentSalesInvoice;
import com.pj.magic.model.search.PaymentSalesInvoiceSearchCriteria;
import com.pj.magic.model.search.PaymentSearchCriteria;

public interface PaymentService {

	void save(Payment payment);

	Payment getPayment(long id);

	List<Payment> getAllNewPayments();

	void save(PaymentSalesInvoice paymentSalesInvoice);
	
	List<PaymentSalesInvoice> findAllPaymentSalesInvoicesByPayment(Payment payment);

	void save(PaymentCheckPayment check);

	void delete(PaymentSalesInvoice paymentSalesInvoice);

	void save(PaymentCashPayment cashPayment);

	void delete(PaymentCheckPayment checkPayment);

	void delete(PaymentCashPayment cashPayment);

	void delete(PaymentAdjustment adjustment);

	void save(PaymentAdjustment adjustment);

	void post(Payment payment);

	List<Payment> searchPayments(PaymentSearchCriteria criteria);

	List<PaymentSalesInvoice> searchPaymentSalesInvoices(PaymentSalesInvoiceSearchCriteria criteria);
	
	void cancel(Payment payment);
	
}