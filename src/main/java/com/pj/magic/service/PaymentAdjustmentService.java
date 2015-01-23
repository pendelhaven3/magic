package com.pj.magic.service;

import java.util.List;

import com.pj.magic.model.PaymentAdjustment;

public interface PaymentAdjustmentService {

	void save(PaymentAdjustment paymentAdjustment);
	
	PaymentAdjustment getPaymentAdjustment(long id);
	
	List<PaymentAdjustment> getAllPaymentAdjustments();

	void post(PaymentAdjustment paymentAdjustment);

	void markAsPaid(PaymentAdjustment paymentAdjustment);

	PaymentAdjustment findPaymentAdjustmentByPaymentAdjustmentNumber(long paymentAdjustmentNumber);
	
}