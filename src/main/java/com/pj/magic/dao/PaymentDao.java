package com.pj.magic.dao;

import java.util.Date;
import java.util.List;

import com.pj.magic.model.Payment;

public interface PaymentDao {

	void save(Payment payment);

	Payment get(long id);

	List<Payment> findAllByPaymentDate(Date truncate);
	
}
