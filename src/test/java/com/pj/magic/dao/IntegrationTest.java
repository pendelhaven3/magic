package com.pj.magic.dao;

import org.springframework.dao.DuplicateKeyException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;

@ContextConfiguration(locations={
		"classpath:applicationContext.xml",
		"classpath:datasource-test.xml"
//		"classpath:datasource.xml"
		})
public abstract class IntegrationTest extends AbstractTransactionalJUnit4SpringContextTests {

	/**
	 * Insert test Supplier record with ID = 1
	 */
	protected void insertTestSupplier() {
		try {
			jdbcTemplate.update(
					"insert into SUPPLIER (ID, CODE, NAME, VAT_INCLUSIVE) values (1, 'TEST', 'TEST', 'Y')");
		} catch (DuplicateKeyException e) {
			// test supplier already exists
		}
	}
	
	/**
	 * Insert test User record with ID = 1
	 */
	protected void insertTestUser() {
		try {
			jdbcTemplate.update("insert into USER (ID, USERNAME, PASSWORD) values (1, 'TEST', 'TEST')");
		} catch (DuplicateKeyException e) {
			// test user already exists
		}
	}
	
	/**
	 * Insert test Purchase Payment record with ID = 1
	 */
	protected void insertTestPurchasePayment() {
		insertTestSupplier();
		insertTestUser();
		
		jdbcTemplate.update(
				"insert into PURCHASE_PAYMENT (ID, PURCHASE_PAYMENT_NO, SUPPLIER_ID, CREATE_DT, ENCODER) "
				+ " values (1, 1, 1, now(), 1)");
	}
	
	protected void insertTestPurchasePaymentAdjustmentType() {
		jdbcTemplate.update("insert into PURCHASE_PAYMENT_ADJ_TYPE (ID, CODE, DESCRIPTION)"
				+ " values (1, 'CODE', 'DESCRIPTION')");
	}
	
}