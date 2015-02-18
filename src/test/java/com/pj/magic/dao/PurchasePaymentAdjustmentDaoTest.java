package com.pj.magic.dao;

import static org.junit.Assert.*;

import java.math.BigDecimal;
import java.util.Date;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.pj.magic.model.PurchasePaymentAdjustment;
import com.pj.magic.model.PurchasePaymentAdjustmentType;
import com.pj.magic.model.Supplier;
import com.pj.magic.model.search.PurchasePaymentAdjustmentSearchCriteria;

public class PurchasePaymentAdjustmentDaoTest extends IntegrationTest {
	
	@Autowired private PurchasePaymentAdjustmentDao paymentAdjustmentDao;
	
	@Test
	public void get() {
		insertTestPurchasePaymentAdjustment();
		paymentAdjustmentDao.get(1L);
	}

	@Test
	public void save_insert() {
		insertTestSupplier();
		insertTestPurchasePaymentAdjustmentType();
		
		PurchasePaymentAdjustment paymentAdjustment = new PurchasePaymentAdjustment();
		paymentAdjustment.setAdjustmentType(new PurchasePaymentAdjustmentType(1L));
		paymentAdjustment.setAmount(new BigDecimal("100"));
		paymentAdjustment.setPurchasePaymentAdjustmentNumber(1L);
		paymentAdjustment.setSupplier(new Supplier(1L));
		paymentAdjustmentDao.save(paymentAdjustment);
	}

	@Test
	public void save_update() {
		insertTestPurchasePaymentAdjustment();
		
		PurchasePaymentAdjustment paymentAdjustment = new PurchasePaymentAdjustment(1L);
		paymentAdjustment.setAdjustmentType(new PurchasePaymentAdjustmentType(1L));
		paymentAdjustment.setAmount(new BigDecimal("100"));
		paymentAdjustment.setPurchasePaymentAdjustmentNumber(1L);
		paymentAdjustment.setSupplier(new Supplier(1L));
		paymentAdjustmentDao.save(paymentAdjustment);
	}

	@Test
	public void findByPurchasePaymentAdjustmentNumber() {
		insertTestPurchasePaymentAdjustment();
		assertNotNull(paymentAdjustmentDao.findByPurchasePaymentAdjustmentNumber(1L));
	}

	@Test
	public void search() {
		PurchasePaymentAdjustmentSearchCriteria criteria = new PurchasePaymentAdjustmentSearchCriteria();
		criteria.setPaymentAdjustmentNumber(1L);
		criteria.setSupplier(new Supplier(1L));
		criteria.setPosted(false);
		criteria.setPostDate(new Date());
		criteria.setPostDateFrom(new Date());
		criteria.setPostDateTo(new Date());
		
		paymentAdjustmentDao.search(criteria);
	}
	
	private void insertTestPurchasePaymentAdjustment() {
		insertTestSupplier();
		insertTestPurchasePaymentAdjustmentType();
		
		jdbcTemplate.update("insert into PURCHASE_PAYMENT_ADJUSTMENT"
				+ " (ID, PURCHASE_PAYMENT_ADJUSTMENT_NO, SUPPLIER_ID,"
				+ " PURCHASE_PAYMENT_ADJ_TYPE_ID, AMOUNT) values"
				+ " (1, 1, 1, 1, 100)");
	}
	
}