package com.pj.magic.service.impl;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pj.magic.dao.CreditCardDao;
import com.pj.magic.dao.CreditCardStatementDao;
import com.pj.magic.dao.CreditCardStatementItemDao;
import com.pj.magic.model.CreditCard;
import com.pj.magic.model.CreditCardStatement;
import com.pj.magic.model.CreditCardStatementItem;
import com.pj.magic.service.CreditCardService;

@Service
public class CreditCardServiceImpl implements CreditCardService {

	@Autowired private CreditCardDao creditCardDao;
	@Autowired private CreditCardStatementDao creditCardStatementDao;
	@Autowired private CreditCardStatementItemDao creditCardStatementItemDao;
	
	@Transactional
	@Override
	public void save(CreditCard creditCard) {
		creditCardDao.save(creditCard);
	}

	@Override
	public CreditCard getCreditCard(long id) {
		return creditCardDao.get(id);
	}

	@Override
	public List<CreditCard> getAllCreditCards() {
		return creditCardDao.getAll();
	}

	@Override
	public List<CreditCardStatement> getAllCreditCardStatements() {
		return creditCardStatementDao.getAll();
	}

	@Override
	public CreditCardStatement getCreditCardStatement(long id) {
		CreditCardStatement statement = creditCardStatementDao.get(id);
		statement.setItems(creditCardStatementItemDao.findAllByCreditCardStatement(statement));
		return statement;
	}

	@Transactional
	@Override
	public void save(CreditCardStatement statement) {
		creditCardStatementDao.save(statement);
		for (CreditCardStatementItem item : statement.getItems()) {
			creditCardStatementItemDao.save(item);
		}
	}

}