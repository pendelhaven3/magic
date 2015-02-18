package com.pj.magic.dao;

import java.math.BigDecimal;
import java.util.Date;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.pj.magic.model.PurchasePayment;
import com.pj.magic.model.PurchasePaymentBankTransfer;
import com.pj.magic.model.Supplier;
import com.pj.magic.model.search.PurchasePaymentBankTransferSearchCriteria;

public class PurchasePaymentBankTransferDaoTest extends IntegrationTest {
	
	@Autowired private PurchasePaymentBankTransferDao bankTransferDao;
	
	@Test
	public void save_insert() {
		insertTestPurchasePayment();
		
		PurchasePaymentBankTransfer bankTransfer = new PurchasePaymentBankTransfer();
		bankTransfer.setParent(new PurchasePayment(1L));
		bankTransfer.setBank("BANK");
		bankTransfer.setAmount(new BigDecimal("100"));
		bankTransfer.setTransferDate(new Date());
		bankTransfer.setReferenceNumber("REF_NO");
		bankTransferDao.save(bankTransfer);
	}

	@Test
	public void save_update() {
		insertTestBankTransfer();
		
		PurchasePaymentBankTransfer bankTransfer = new PurchasePaymentBankTransfer(1L);
		bankTransfer.setParent(new PurchasePayment(1L));
		bankTransfer.setBank("BANK");
		bankTransfer.setAmount(new BigDecimal("100"));
		bankTransfer.setTransferDate(new Date());
		bankTransfer.setReferenceNumber("REF_NO");
		bankTransferDao.save(bankTransfer);
	}

	private void insertTestBankTransfer() {
		insertTestPurchasePayment();
		
		jdbcTemplate.update("insert into PURCHASE_PAYMENT_BANK_TRANSFER"
				+ " (ID, PURCHASE_PAYMENT_ID, BANK, AMOUNT, TRANSFER_DT, REFERENCE_NO)"
				+ " values"
				+ " (1, 1, 'BANK', 100, now(), 'REF_NO')");
	}

	@Test
	public void findAllByPurchasePayment() {
		insertTestBankTransfer();
		
		bankTransferDao.findAllByPurchasePayment(new PurchasePayment(1L));
	}

	@Test
	public void delete() {
		bankTransferDao.delete(new PurchasePaymentBankTransfer(1L));
	}

	@Test
	public void search() {
		PurchasePaymentBankTransferSearchCriteria criteria = new PurchasePaymentBankTransferSearchCriteria();
		criteria.setPosted(false);
		criteria.setSupplier(new Supplier(1L));
		criteria.setFromDate(new Date());
		criteria.setToDate(new Date());
		
		bankTransferDao.search(criteria);
	}
	
}