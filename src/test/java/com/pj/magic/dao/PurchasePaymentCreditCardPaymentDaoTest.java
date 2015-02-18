package com.pj.magic.dao;

import java.math.BigDecimal;
import java.util.Date;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.pj.magic.model.CreditCard;
import com.pj.magic.model.PurchasePayment;
import com.pj.magic.model.PurchasePaymentCreditCardPayment;
import com.pj.magic.model.Supplier;
import com.pj.magic.model.search.PurchasePaymentCreditCardPaymentSearchCriteria;

public class PurchasePaymentCreditCardPaymentDaoTest extends IntegrationTest {
	
	@Autowired private PurchasePaymentCreditCardPaymentDao creditCardPaymentDao;
	
	@Test
	public void save_insert() {
		insertTestPurchasePayment();
		insertTestCreditCard();
		
		PurchasePaymentCreditCardPayment creditCardPayment = new PurchasePaymentCreditCardPayment();
		creditCardPayment.setParent(new PurchasePayment(1L));
		creditCardPayment.setAmount(new BigDecimal("100"));
		creditCardPayment.setCreditCard(new CreditCard(1L));
		creditCardPayment.setApprovalCode("APPROVAL CODE");
		creditCardPayment.setTransactionDate(new Date());
		creditCardPaymentDao.save(creditCardPayment);
	}

	private void insertTestCreditCard() {
		jdbcTemplate.update("insert into CREDIT_CARD (ID, USER, BANK, CARD_NUMBER)"
				+ " values (1, 'USER', 'BANK', 'CARD NO')");
	}

	@Test
	public void save_update() {
		insertTestCreditCardPayment();
		
		PurchasePaymentCreditCardPayment creditCardPayment = new PurchasePaymentCreditCardPayment(1L);
		creditCardPayment.setAmount(new BigDecimal("100"));
		creditCardPayment.setCreditCard(new CreditCard(1L));
		creditCardPayment.setApprovalCode("APPROVAL CODE");
		creditCardPayment.setTransactionDate(new Date());
		creditCardPaymentDao.save(creditCardPayment);
	}

	private void insertTestCreditCardPayment() {
		insertTestPurchasePayment();
		insertTestCreditCard();
		
		jdbcTemplate.update("insert into SUPP_PAYMENT_CREDITCARD_PYMNT"
				+ " (ID, SUPPLIER_PAYMENT_ID, AMOUNT, CREDIT_CARD_ID, TRANSACTION_DT, APPROVAL_CODE)"
				+ " values"
				+ " (1, 1, 100, 1, now(), 'APPROVAL CODE')");
	}

	@Test
	public void findAllByPurchasePayment() {
		insertTestCreditCardPayment();
		
		creditCardPaymentDao.findAllByPurchasePayment(new PurchasePayment(1L));
	}

	@Test
	public void delete() {
		creditCardPaymentDao.delete(new PurchasePaymentCreditCardPayment(1L));
	}

	@Test
	public void search() {
		PurchasePaymentCreditCardPaymentSearchCriteria criteria = new PurchasePaymentCreditCardPaymentSearchCriteria();
		criteria.setPosted(false);
		criteria.setFromDate(new Date());	
		criteria.setToDate(new Date());
		criteria.setSupplier(new Supplier(1L));
		
		creditCardPaymentDao.search(criteria);
	}
	
}