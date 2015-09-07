package com.pj.magic.service.impl;

import java.util.Date;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pj.magic.dao.CreditCardDao;
import com.pj.magic.dao.CreditCardPaymentDao;
import com.pj.magic.dao.CreditCardStatementDao;
import com.pj.magic.dao.CreditCardStatementItemDao;
import com.pj.magic.model.CreditCard;
import com.pj.magic.model.CreditCardPayment;
import com.pj.magic.model.CreditCardStatement;
import com.pj.magic.model.CreditCardStatementItem;
import com.pj.magic.service.CreditCardService;

@Service
public class CreditCardServiceImpl implements CreditCardService {

	@Autowired private CreditCardDao creditCardDao;
	@Autowired private CreditCardStatementDao creditCardStatementDao;
	@Autowired private CreditCardStatementItemDao creditCardStatementItemDao;
	@Autowired private CreditCardPaymentDao creditCardPaymentDao;
	
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
		List<CreditCardStatement> statements = creditCardStatementDao.getAll();
		for (CreditCardStatement statement : statements) {
			statement.setItems(creditCardStatementItemDao.findAllByCreditCardStatement(statement));
		}
		return statements;
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
		if (statement.isNew()) {
			for (CreditCardStatementItem item : statement.getItems()) {
				creditCardStatementItemDao.save(item);
			}
		}
	}

	@Override
	public List<CreditCardPayment> getCreditCardPayments(CreditCard creditCard) {
		return creditCardPaymentDao.findAllByCreditCard(creditCard);
	}

	@Transactional
	@Override
	public void save(CreditCardPayment payment) {
		creditCardPaymentDao.save(payment);
	}

	@Override
	public CreditCardPayment getCreditCardPayment(long id) {
		return creditCardPaymentDao.get(id);
	}

	@Transactional
	@Override
	public void markAsPaid(List<CreditCardStatementItem> items, Date paidDate) {
		for (CreditCardStatementItem item : items) {
			item.setPaid(true);
			item.setPaidDate(paidDate);
			creditCardStatementItemDao.save(item);
		}
	}

	@Transactional
	@Override
	public void markAsUnpaid(List<CreditCardStatementItem> items) {
		for (CreditCardStatementItem item : items) {
			item.setPaid(false);
			item.setPaidDate(null);
			creditCardStatementItemDao.save(item);
		}
	}

}