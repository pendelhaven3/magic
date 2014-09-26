package com.pj.magic.dao;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.pj.magic.model.PaymentTerm;
import com.pj.magic.model.PurchaseOrder;
import com.pj.magic.model.Supplier;
import com.pj.magic.model.User;

public class PurchaseOrderDaoTest extends DaoTest {

	@Autowired private PurchaseOrderDao purchaseOrderDao;
	
	private static final String INSERT_TEST_SUPPLIER_SQL =
			"insert into SUPPLIER (ID, CODE, NAME) values (1, 'ABENSON', 'ABENSON AVANT')";
	
	private static final String INSERT_TEST_USER_SQL =
			"insert into USER (ID, USERNAME, PASSWORD) values (1, 'PJ', 'TEST')";
	
	private static final String INSERT_TEST_PAYMENT_TERM_SQL =
			"insert into PAYMENT_TERM (ID, NAME, NUMBER_OF_DAYS) values (1, 'COD', 0)";
	
	private Supplier testSupplier = new Supplier(1L);
	private User testUser = new User(1L);
	private PaymentTerm testPaymentTerm = new PaymentTerm(1L);
	
	@Before
	public void setUp() {
		jdbcTemplate.update(INSERT_TEST_SUPPLIER_SQL);
		jdbcTemplate.update(INSERT_TEST_USER_SQL);
	}
	
	@Test
	public void insert_withoutPaymentTerm() {
		PurchaseOrder purchaseOrder = new PurchaseOrder();
		purchaseOrder.setSupplier(testSupplier);
		purchaseOrder.setCreatedBy(testUser);
		purchaseOrderDao.save(purchaseOrder);
		
		assertNotNull(purchaseOrder.getId());
		assertNotNull(purchaseOrder.getPurchaseOrderNumber());
		assertEquals(1, (int)jdbcTemplate.queryForObject(
				"select count(*) from PURCHASE_ORDER where ID = ? and SUPPLIER_ID = 1 and CREATED_BY = 1", 
				Integer.class, purchaseOrder.getId()));
	}

	@Test
	public void insert_withPaymentTerm() {
		jdbcTemplate.update(INSERT_TEST_PAYMENT_TERM_SQL);
		
		PurchaseOrder purchaseOrder = new PurchaseOrder();
		purchaseOrder.setSupplier(testSupplier);
		purchaseOrder.setPaymentTerm(testPaymentTerm);
		purchaseOrder.setCreatedBy(testUser);
		purchaseOrderDao.save(purchaseOrder);
		
		assertNotNull(purchaseOrder.getId());
		assertNotNull(purchaseOrder.getPurchaseOrderNumber());
		assertEquals(1, (int)jdbcTemplate.queryForObject(
				"select count(*) from PURCHASE_ORDER where ID = ? and SUPPLIER_ID = 1 and CREATED_BY = 1"
				+ " and PAYMENT_TERM_ID = 1", 
				Integer.class, purchaseOrder.getId()));
	}

}
