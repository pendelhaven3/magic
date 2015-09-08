package com.pj.magic.service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import com.pj.magic.exception.NotEnoughSurplusPaymentException;
import com.pj.magic.model.CreditCard;
import com.pj.magic.model.CreditCardPayment;
import com.pj.magic.model.CreditCardStatement;
import com.pj.magic.model.CreditCardStatementItem;

public interface CreditCardService {

	void save(CreditCard creditCard);
	
	CreditCard getCreditCard(long id);
	
	List<CreditCard> getAllCreditCards();
	
	List<CreditCardStatement> getAllCreditCardStatements();

	CreditCardStatement getCreditCardStatement(long id);

	void save(CreditCardStatement statement);

	List<CreditCardPayment> getCreditCardPayments(CreditCard creditCard);

	void save(CreditCardPayment payment);

	CreditCardPayment getCreditCardPayment(long id);

	void markAsPaid(List<CreditCardStatementItem> items, Date paidDate) throws NotEnoughSurplusPaymentException;

	void markAsUnpaid(List<CreditCardStatementItem> items);

	void delete(CreditCardStatementItem item);

	BigDecimal getSurplusPayment(CreditCard creditCard);
	
}