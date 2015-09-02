package com.pj.magic.dao;

import java.util.List;

import com.pj.magic.model.CreditCardStatement;

public interface CreditCardStatementDao {

	List<CreditCardStatement> getAll();

	CreditCardStatement get(long id);
	
}
