package com.pj.magic.dao;

import java.util.List;

import com.pj.magic.model.Payment;
import com.pj.magic.model.PaymentCheckPayment;

public interface PaymentCheckPaymentDao {

	void save(PaymentCheckPayment check);

	List<PaymentCheckPayment> findAllByPayment(Payment payment);
	
}
