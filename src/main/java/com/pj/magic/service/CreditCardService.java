package com.pj.magic.service;

import java.util.List;

import com.pj.magic.model.CreditCard;
import com.pj.magic.model.CreditCardStatement;

public interface CreditCardService {

	void save(CreditCard creditCard);
	
	CreditCard getCreditCard(long id);
	
	List<CreditCard> getAllCreditCards();
	
	List<CreditCardStatement> getAllCreditCardStatements();
	
}