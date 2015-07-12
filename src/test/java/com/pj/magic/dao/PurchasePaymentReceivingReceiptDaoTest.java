package com.pj.magic.dao;


import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.pj.magic.model.PurchasePayment;
import com.pj.magic.model.PurchasePaymentReceivingReceipt;
import com.pj.magic.model.ReceivingReceipt;

@Ignore
public class PurchasePaymentReceivingReceiptDaoTest extends IntegrationTest {
	
	@Autowired private PurchasePaymentReceivingReceiptDao paymentReceivingReceiptDao;
	
	@Test
	public void insert() {
		insertTestReceivingReceipt();
		insertTestPurchasePayment();
		
		PurchasePaymentReceivingReceipt paymentAdjustment = new PurchasePaymentReceivingReceipt();
		paymentAdjustment.setParent(new PurchasePayment(1L));
		paymentAdjustment.setReceivingReceipt(new ReceivingReceipt(1L));
		paymentReceivingReceiptDao.insert(paymentAdjustment);
	}

	private void insertTestReceivingReceipt() {
		insertTestPurchaseOrder();
		
		jdbcTemplate.update("insert into RECEIVING_RECEIPT (ID, RECEIVING_RECEIPT_NO, SUPPLIER_ID,"
				+ " PAYMENT_TERM_ID, RELATED_PURCHASE_ORDER_NO, VAT_INCLUSIVE, VAT_RATE, RECEIVED_DT,"
				+ " RECEIVED_BY) values"
				+ " (1, 1, 1, 1, 1, 'Y', 0.12, now(), 1)");
	}

	private void insertTestPurchaseOrder() {
		insertTestSupplier();
		insertTestPaymentTerm();
		insertTestUser();
		
		jdbcTemplate.update("insert into PURCHASE_ORDER (ID, PURCHASE_ORDER_NO, SUPPLIER_ID, CREATED_BY,"
				+ " PAYMENT_TERM_ID) values (1, 1, 1, 1, 1)");
	}

	private void insertTestPaymentTerm() {
		jdbcTemplate.update("insert into PAYMENT_TERM (ID, NAME, NUMBER_OF_DAYS) values (1, 'TEST', 0)");
	}
	
	@Test
	public void findAllByPurchasePayment() {
		insertTestPurchasePaymentReceivingReceipt();
		paymentReceivingReceiptDao.findAllByPurchasePayment(new PurchasePayment(1L));
	}

	private void insertTestPurchasePaymentReceivingReceipt() {
		insertTestReceivingReceipt();
		insertTestPurchasePayment();
		
		jdbcTemplate.update("insert into PURCHASE_PAYMENT_RECEIVING_RECEIPT (ID, PURCHASE_PAYMENT_ID,"
				+ " RECEIVING_RECEIPT_ID) values (1, 1, 1)");
	}

	@Test
	public void deleteAllByPurchasePayment() {
		paymentReceivingReceiptDao.deleteAllByPurchasePayment(new PurchasePayment(1L));
	}

	@Test
	public void delete() {
		paymentReceivingReceiptDao.delete(new PurchasePaymentReceivingReceipt(1L));
	}
	
}