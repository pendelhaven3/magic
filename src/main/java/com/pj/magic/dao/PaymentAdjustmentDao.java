package com.pj.magic.dao;

import java.util.List;

import com.pj.magic.model.PaymentAdjustment;

public interface PaymentAdjustmentDao {

	PaymentAdjustment get(long id);

	void save(PaymentAdjustment paymentAdjustment);

	List<PaymentAdjustment> getAll();

	PaymentAdjustment findByPaymentAdjustmentNumber(long paymentAdjustmentNumber);

}