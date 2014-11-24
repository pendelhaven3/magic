package com.pj.magic.dao;

import java.util.List;

import com.pj.magic.model.Payment;
import com.pj.magic.model.PaymentAdjustment;

public interface PaymentAdjustmentDao {

	void save(PaymentAdjustment adjustment);

	List<PaymentAdjustment> findAllByPayment(Payment payment);

	void deleteAllByPayment(Payment payment);

	void delete(PaymentAdjustment adjustment);
	
}
