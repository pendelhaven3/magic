package com.pj.magic.dao;

import java.math.BigDecimal;
import java.util.List;

import com.pj.magic.model.CreditCard;
import com.pj.magic.model.CreditCardPayment;

public interface CreditCardPaymentDao {

	List<CreditCardPayment> findAllByCreditCard(CreditCard creditCard);

	void save(CreditCardPayment payment);

	CreditCardPayment get(long id);

	BigDecimal getSurplusPayment(CreditCard creditCard);

	void delete(CreditCardPayment payment);
	
}
