package com.pj.magic.dao;

import java.util.List;

import com.pj.magic.model.Payment;
import com.pj.magic.model.PaymentItem;

public interface PaymentItemDao {

	void save(PaymentItem item);

	List<PaymentItem> findAllByPayment(Payment payment);
	
}
