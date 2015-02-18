package com.pj.magic.dao;

import java.math.BigDecimal;
import java.util.Date;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.pj.magic.model.PurchasePayment;
import com.pj.magic.model.PurchasePaymentCashPayment;
import com.pj.magic.model.User;
import com.pj.magic.model.search.PurchasePaymentCashPaymentSearchCriteria;

public class PurchasePaymentCashPaymentDaoTest extends IntegrationTest {
	
	@Autowired private PurchasePaymentCashPaymentDao cashPaymentDao;
	
	@Test
	public void save_insert() {
		insertTestPurchasePayment();
		
		PurchasePaymentCashPayment cashPayment = new PurchasePaymentCashPayment();
		cashPayment.setParent(new PurchasePayment(1L));
		cashPayment.setPaidBy(new User(1L));
		cashPayment.setAmount(new BigDecimal("100"));
		cashPayment.setPaidDate(new Date());
		cashPaymentDao.save(cashPayment);
	}

	@Test
	public void save_update() {
		insertTestCashPayment();
		
		PurchasePaymentCashPayment cashPayment = new PurchasePaymentCashPayment(1L);
		cashPayment.setPaidBy(new User(1L));
		cashPayment.setAmount(new BigDecimal("100"));
		cashPayment.setPaidDate(new Date());
		cashPaymentDao.save(cashPayment);
	}

	private void insertTestCashPayment() {
		insertTestPurchasePayment();
		
		jdbcTemplate.update("insert into SUPP_PAYMENT_CASH_PYMNT"
				+ " (ID, SUPPLIER_PAYMENT_ID, AMOUNT, PAID_DT, PAID_BY)"
				+ " values"
				+ " (1, 1, 100, now(), 1)");
	}

	@Test
	public void findAllByPurchasePayment() {
		insertTestCashPayment();
		
		cashPaymentDao.findAllByPurchasePayment(new PurchasePayment(1L));
	}

	@Test
	public void delete() {
		cashPaymentDao.delete(new PurchasePaymentCashPayment(1L));
	}

	@Test
	public void search() {
		PurchasePaymentCashPaymentSearchCriteria criteria = new PurchasePaymentCashPaymentSearchCriteria();
		criteria.setPosted(false);
		criteria.setFromDate(new Date());
		criteria.setToDate(new Date());
		
		cashPaymentDao.search(criteria);
	}
	
}