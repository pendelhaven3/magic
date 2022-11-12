package com.pj.magic.dao;

import java.util.List;

import com.pj.magic.model.EcashReceiver;
import com.pj.magic.model.Payment;
import com.pj.magic.model.PaymentEcashPayment;
import com.pj.magic.model.search.PaymentEcashPaymentSearchCriteria;

public interface PaymentEcashPaymentDao {

	void save(PaymentEcashPayment ecashPayment);

	List<PaymentEcashPayment> findAllByPayment(Payment payment);

	void deleteAllByPayment(Payment payment);

	void delete(PaymentEcashPayment ecashPayment);

	List<PaymentEcashPayment> search(PaymentEcashPaymentSearchCriteria criteria);

	PaymentEcashPayment findOneByEcashReceiver(EcashReceiver ecashReceiver);
	
}