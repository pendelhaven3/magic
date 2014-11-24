package com.pj.magic.dao;

import java.util.List;

import com.pj.magic.model.Payment;
import com.pj.magic.model.PaymentSalesInvoice;

public interface PaymentSalesInvoiceDao {

	void save(PaymentSalesInvoice paymentSalesInvoice);

	List<PaymentSalesInvoice> findAllByPayment(Payment payment);

	void deleteAllByPayment(Payment payment);

	void delete(PaymentSalesInvoice paymentSalesInvoice);
	
}
