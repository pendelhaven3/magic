package com.pj.magic.service.impl;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pj.magic.Constants;
import com.pj.magic.dao.CreditCardDao;
import com.pj.magic.dao.CreditCardPaymentDao;
import com.pj.magic.dao.CreditCardStatementDao;
import com.pj.magic.dao.CreditCardStatementItemDao;
import com.pj.magic.exception.NotEnoughSurplusPaymentException;
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
		boolean isNew = statement.isNew();
		creditCardStatementDao.save(statement);
		if (isNew) {
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
	public void markAsPaid(List<CreditCardStatementItem> items, Date paidDate) 
			throws NotEnoughSurplusPaymentException {
		if (items.isEmpty()) {
			return;
		}
		
		BigDecimal totalPaidAmount = getTotalAmount(items);
		BigDecimal totalSurplusPayments = getSurplusPayment(items.get(0).getParent().getCreditCard());
		
		if (totalPaidAmount.compareTo(totalSurplusPayments) > 0) {
			throw new NotEnoughSurplusPaymentException();
		}
		
		for (CreditCardStatementItem item : items) {
			item.setPaid(true);
			item.setPaidDate(paidDate);
			creditCardStatementItemDao.save(item);
		}
	}

	private static BigDecimal getTotalAmount(List<CreditCardStatementItem> items) {
		BigDecimal total = Constants.ZERO;
		for (CreditCardStatementItem item : items) {
			total = total.add(item.getCreditCardPayment().getAmount());
		}
		return total;
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

	@Transactional
	@Override
	public void delete(CreditCardStatementItem item) {
		creditCardStatementItemDao.delete(item);
	}

	@Override
	public BigDecimal getSurplusPayment(CreditCard creditCard) {
		return creditCardPaymentDao.getSurplusPayment(creditCard);
	}

	@Transactional
	@Override
	public void save(CreditCardStatementItem item) {
		creditCardStatementItemDao.save(item);
	}

	@Override
	public List<CreditCardStatement> findAllStatementsByCreditCard(CreditCard creditCard) {
		List<CreditCardStatement> statements = creditCardStatementDao.findAllByCreditCard(creditCard);
		for (CreditCardStatement statement : statements) {
			statement.setItems(creditCardStatementItemDao.findAllByCreditCardStatement(statement));
		}
		return statements;
	}

	@Transactional
	@Override
	public void delete(CreditCardPayment payment) {
		creditCardPaymentDao.delete(payment);
	}

	@Override
	public CreditCardStatement findStatementByCreditCardAndStatementDate(
			CreditCard creditCard, Date statementDate) {
		return creditCardStatementDao.findByCreditCardAndStatementDate(creditCard, statementDate);
	}

}