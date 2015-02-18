package com.pj.magic.dao;

import static org.junit.Assert.*;

import java.util.Date;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.pj.magic.model.PurchasePayment;
import com.pj.magic.model.Supplier;
import com.pj.magic.model.User;
import com.pj.magic.model.search.PurchasePaymentSearchCriteria;

public class PurchasePaymentDaoTest extends IntegrationTest {
	
	@Autowired private PurchasePaymentDao purchasePaymentDao;
	
	@Test
	public void save_insert() {
		insertTestSupplier();
		insertTestUser();
		
		PurchasePayment purchasePayment = new PurchasePayment();
		purchasePayment.setSupplier(new Supplier(1L));
		purchasePayment.setEncoder(new User(1L));
		purchasePayment.setPosted(true);
		purchasePayment.setPostDate(new Date());
		purchasePayment.setPostedBy(new User(1L));
		purchasePaymentDao.save(purchasePayment);
	}
	
	@Test
	public void save_update() {
		insertTestPurchasePayment();
		
		PurchasePayment purchasePayment = new PurchasePayment(1L);
		purchasePayment.setSupplier(new Supplier(1L));
		purchasePayment.setEncoder(new User(1L));
		purchasePaymentDao.save(purchasePayment);
	}
	
	@Test
	public void get() {
		insertTestPurchasePayment();
		
		PurchasePayment purchasePayment = purchasePaymentDao.get(1L);
		assertNotNull(purchasePayment);
	}

	@Test
	public void search() {
		PurchasePaymentSearchCriteria criteria = new PurchasePaymentSearchCriteria();
		criteria.setPosted(false);
		criteria.setSupplier(new Supplier(1L));
		criteria.setPostDate(new Date());
		criteria.setPaymentNumber(1L);
		criteria.setCancelled(false);
		
		purchasePaymentDao.search(criteria);
	}
	
}