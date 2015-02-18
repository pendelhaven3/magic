package com.pj.magic.dao;

import java.math.BigDecimal;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.pj.magic.model.PurchasePayment;
import com.pj.magic.model.PurchasePaymentAdjustmentType;
import com.pj.magic.model.PurchasePaymentPaymentAdjustment;

public class PurchasePaymentPaymentAdjustmentDaoTest extends IntegrationTest {
	
	@Autowired private PurchasePaymentPaymentAdjustmentDao paymentAdjustmentDao;
	
	@Test
	public void save_insert() {
		insertTestPurchasePayment();
		insertTestPurchasePaymentAdjustmentType();
		
		PurchasePaymentPaymentAdjustment paymentAdjustment = new PurchasePaymentPaymentAdjustment();
		paymentAdjustment.setParent(new PurchasePayment(1L));
		paymentAdjustment.setAdjustmentType(new PurchasePaymentAdjustmentType(1L));
		paymentAdjustment.setReferenceNumber("1");
		paymentAdjustment.setAmount(new BigDecimal("100"));
		paymentAdjustmentDao.save(paymentAdjustment);
	}

	@Test
	public void save_update() {
		PurchasePaymentPaymentAdjustment paymentAdjustment = new PurchasePaymentPaymentAdjustment(1L);
		paymentAdjustment.setParent(new PurchasePayment(1L));
		paymentAdjustment.setAdjustmentType(new PurchasePaymentAdjustmentType(1L));
		paymentAdjustment.setReferenceNumber("1");
		paymentAdjustment.setAmount(new BigDecimal("100"));
		paymentAdjustmentDao.save(paymentAdjustment);
	}

	@Test
	public void findAllByPurchasePayment() {
		insertTestPurchasePaymentPaymentAdjustment();
		paymentAdjustmentDao.findAllByPurchasePayment(new PurchasePayment(1L));
	}

	private void insertTestPurchasePaymentPaymentAdjustment() {
		insertTestPurchasePayment();
		insertTestPurchasePaymentAdjustmentType();
		
		jdbcTemplate.update("insert into PURCHASE_PAYMENT_PAYMENT_ADJUSTMENT (ID, PURCHASE_PAYMENT_ID,"
				+ " PURCHASE_PAYMENT_ADJ_TYPE_ID, REFERENCE_NO, AMOUNT) values (1, 1, 1, 1, 100)");
	}

	@Test
	public void deleteAllByPurchasePayment() {
		paymentAdjustmentDao.deleteAllByPurchasePayment(new PurchasePayment(1L));
	}

	@Test
	public void delete() {
		paymentAdjustmentDao.delete(new PurchasePaymentPaymentAdjustment(1L));
	}
	
}