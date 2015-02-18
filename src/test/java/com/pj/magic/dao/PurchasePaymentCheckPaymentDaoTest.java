package com.pj.magic.dao;

import java.math.BigDecimal;
import java.util.Date;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.pj.magic.model.PurchasePayment;
import com.pj.magic.model.PurchasePaymentCheckPayment;
import com.pj.magic.model.Supplier;
import com.pj.magic.model.search.PurchasePaymentCheckPaymentSearchCriteria;

public class PurchasePaymentCheckPaymentDaoTest extends IntegrationTest {
	
	@Autowired private PurchasePaymentCheckPaymentDao checkPaymentDao;
	
	@Test
	public void save_insert() {
		insertTestPurchasePayment();
		
		PurchasePaymentCheckPayment checkPayment = new PurchasePaymentCheckPayment();
		checkPayment.setParent(new PurchasePayment(1L));
		checkPayment.setBank("BANK");
		checkPayment.setAmount(new BigDecimal("100"));
		checkPayment.setCheckDate(new Date());
		checkPayment.setCheckNumber("CHECK_NO");
		checkPaymentDao.save(checkPayment);
	}

	@Test
	public void save_update() {
		insertTestCheckPayment();
		
		PurchasePaymentCheckPayment checkPayment = new PurchasePaymentCheckPayment(1L);
		checkPayment.setBank("BANK");
		checkPayment.setAmount(new BigDecimal("100"));
		checkPayment.setCheckDate(new Date());
		checkPayment.setCheckNumber("CHECK_NO");
		checkPaymentDao.save(checkPayment);
	}

	private void insertTestCheckPayment() {
		insertTestPurchasePayment();
		
		jdbcTemplate.update("insert into SUPP_PAYMENT_CHECK_PYMNT"
				+ " (ID, SUPPLIER_PAYMENT_ID, AMOUNT, BANK, CHECK_DT, CHECK_NO)"
				+ " values"
				+ " (1, 1, 100, 'BANK', now(), 'CHECK_NO')");
	}

	@Test
	public void findAllByPurchasePayment() {
		insertTestCheckPayment();
		
		checkPaymentDao.findAllByPurchasePayment(new PurchasePayment(1L));
	}

	@Test
	public void delete() {
		checkPaymentDao.delete(new PurchasePaymentCheckPayment(1L));
	}

	@Test
	public void search() {
		PurchasePaymentCheckPaymentSearchCriteria criteria = new PurchasePaymentCheckPaymentSearchCriteria();
		criteria.setPosted(false);
		criteria.setFromDate(new Date());	
		criteria.setToDate(new Date());
		criteria.setSupplier(new Supplier(1L));
		
		checkPaymentDao.search(criteria);
	}
	
}