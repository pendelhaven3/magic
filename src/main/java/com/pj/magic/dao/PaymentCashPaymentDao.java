package com.pj.magic.dao;

import java.util.List;

import com.pj.magic.model.Payment;
import com.pj.magic.model.PaymentCashPayment;

public interface PaymentCashPaymentDao {

	void save(PaymentCashPayment cash);

	List<PaymentCashPayment> findAllByPayment(Payment payment);

	void deleteAllByPayment(Payment payment);

	void delete(PaymentCashPayment cashPayment);
	
}