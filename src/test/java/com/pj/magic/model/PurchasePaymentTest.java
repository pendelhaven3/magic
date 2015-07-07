package com.pj.magic.model;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.util.Arrays;

import org.junit.Test;

public class PurchasePaymentTest {

	@Test
	public void getTotalAmount() {
		ReceivingReceipt receivingReceipt = mock(ReceivingReceipt.class);
		when(receivingReceipt.getTotalNetAmountWithVat()).thenReturn(new BigDecimal("100"));
		
		ReceivingReceipt receivingReceipt2 = mock(ReceivingReceipt.class);
		when(receivingReceipt2.getTotalNetAmountWithVat()).thenReturn(new BigDecimal("200"));
		
		PurchasePaymentReceivingReceipt paymentReceivingReceipt = new PurchasePaymentReceivingReceipt();
		paymentReceivingReceipt.setReceivingReceipt(receivingReceipt);

		PurchasePaymentReceivingReceipt paymentReceivingReceipt2 = new PurchasePaymentReceivingReceipt();
		paymentReceivingReceipt2.setReceivingReceipt(receivingReceipt2);
		
		PurchasePayment payment = new PurchasePayment();
		payment.setReceivingReceipts(Arrays.asList(paymentReceivingReceipt, paymentReceivingReceipt2));
		
		assertEquals(new BigDecimal("300.00"), payment.getTotalAmount());
	}
	
	@Test
	public void getStatus_posted() {
		PurchasePayment payment = new PurchasePayment();
		payment.setPosted(true);
		
		assertEquals("Posted", payment.getStatus());
	}
	
	@Test
	public void getStatus_new() {
		assertEquals("New", new PurchasePayment().getStatus());
	}
	
	@Test
	public void isNew_true() {
		assertTrue(new PurchasePayment().isNew());
	}

	@Test
	public void isNew_false_if_posted() {
		PurchasePayment payment = new PurchasePayment();
		payment.setPosted(true);
		
		assertFalse(payment.isNew());
	}
	
	@Test
	public void isNew_false_if_cancelled() {
		PurchasePayment payment = new PurchasePayment();
		payment.setCancelled(true);
		
		assertFalse(payment.isNew());
	}
	
	@Test
	public void getTotalCashPayments() {
		PurchasePaymentCashPayment cashPayment = new PurchasePaymentCashPayment();
		cashPayment.setAmount(new BigDecimal("100"));
		
		PurchasePaymentCashPayment cashPayment2 = new PurchasePaymentCashPayment();
		cashPayment2.setAmount(new BigDecimal("200"));
		
		PurchasePayment payment = new PurchasePayment();
		payment.setCashPayments(Arrays.asList(cashPayment, cashPayment2));
		
		assertEquals(new BigDecimal("300.00"), payment.getTotalCashPayments());
	}

	@Test
	public void getTotalCreditCardPayments() {
		PurchasePaymentCreditCardPayment creditCardPayment = new PurchasePaymentCreditCardPayment();
		creditCardPayment.setAmount(new BigDecimal("100"));
		
		PurchasePaymentCreditCardPayment creditCardPayment2 = new PurchasePaymentCreditCardPayment();
		creditCardPayment2.setAmount(new BigDecimal("200"));
		
		PurchasePayment payment = new PurchasePayment();
		payment.setCreditCardPayments(Arrays.asList(creditCardPayment, creditCardPayment2));
		
		assertEquals(new BigDecimal("300.00"), payment.getTotalCreditCardPayments());
	}
	
	@Test
	public void getTotalBankTransfers() {
		PurchasePaymentBankTransfer bankTransfer = new PurchasePaymentBankTransfer();
		bankTransfer.setAmount(new BigDecimal("100"));
		
		PurchasePaymentBankTransfer bankTransfer2 = new PurchasePaymentBankTransfer();
		bankTransfer2.setAmount(new BigDecimal("200"));
		
		PurchasePayment payment = new PurchasePayment();
		payment.setBankTransfers(Arrays.asList(bankTransfer, bankTransfer2));
		
		assertEquals(new BigDecimal("300.00"), payment.getTotalBankTransfers());
	}
	
	@Test
	public void getTotalCheckPayments() {
		PurchasePaymentCheckPayment checkPayment = new PurchasePaymentCheckPayment();
		checkPayment.setAmount(new BigDecimal("100"));
		
		PurchasePaymentCheckPayment checkPayment2 = new PurchasePaymentCheckPayment();
		checkPayment2.setAmount(new BigDecimal("200"));
		
		PurchasePayment payment = new PurchasePayment();
		payment.setCheckPayments(Arrays.asList(checkPayment, checkPayment2));
		
		assertEquals(new BigDecimal("300.00"), payment.getTotalCheckPayments());
	}
	
	@Test
	public void getTotalPayments() {
		PurchasePaymentCashPayment cashPayment = new PurchasePaymentCashPayment();
		cashPayment.setAmount(new BigDecimal("100"));
		
		PurchasePaymentCashPayment cashPayment2 = new PurchasePaymentCashPayment();
		cashPayment2.setAmount(new BigDecimal("200"));
		
		PurchasePaymentCreditCardPayment creditCardPayment = new PurchasePaymentCreditCardPayment();
		creditCardPayment.setAmount(new BigDecimal("100"));
		
		PurchasePaymentCreditCardPayment creditCardPayment2 = new PurchasePaymentCreditCardPayment();
		creditCardPayment2.setAmount(new BigDecimal("200"));
		
		PurchasePaymentBankTransfer bankTransfer = new PurchasePaymentBankTransfer();
		bankTransfer.setAmount(new BigDecimal("100"));
		
		PurchasePaymentBankTransfer bankTransfer2 = new PurchasePaymentBankTransfer();
		bankTransfer2.setAmount(new BigDecimal("200"));
		
		PurchasePaymentCheckPayment checkPayment = new PurchasePaymentCheckPayment();
		checkPayment.setAmount(new BigDecimal("100"));
		
		PurchasePaymentCheckPayment checkPayment2 = new PurchasePaymentCheckPayment();
		checkPayment2.setAmount(new BigDecimal("200"));
		
		PurchasePayment payment = new PurchasePayment();
		payment.setCashPayments(Arrays.asList(cashPayment, cashPayment2));
		payment.setCreditCardPayments(Arrays.asList(creditCardPayment, creditCardPayment2));
		payment.setBankTransfers(Arrays.asList(bankTransfer, bankTransfer2));
		payment.setCheckPayments(Arrays.asList(checkPayment, checkPayment2));
		
		assertEquals(new BigDecimal("1200.00"), payment.getTotalPayments());
	}
	
	@Test
	public void getTotalAdjustments() {
		PurchasePaymentPaymentAdjustment adjustment = new PurchasePaymentPaymentAdjustment();
		adjustment.setAmount(new BigDecimal("100"));
		
		PurchasePaymentPaymentAdjustment adjustment2 = new PurchasePaymentPaymentAdjustment();
		adjustment2.setAmount(new BigDecimal("200"));
		
		PurchasePayment payment = new PurchasePayment();
		payment.setPaymentAdjustments(Arrays.asList(adjustment, adjustment2));
		
		assertEquals(new BigDecimal("300.00"), payment.getTotalAdjustments());
	}
	
	@Test
	public void getOverOrShort() {
		ReceivingReceipt receivingReceipt = mock(ReceivingReceipt.class);
		when(receivingReceipt.getTotalNetAmountWithVat()).thenReturn(new BigDecimal("1000"));
		
		ReceivingReceipt receivingReceipt2 = mock(ReceivingReceipt.class);
		when(receivingReceipt2.getTotalNetAmountWithVat()).thenReturn(new BigDecimal("2000"));
		
		PurchasePaymentReceivingReceipt paymentReceivingReceipt = new PurchasePaymentReceivingReceipt();
		paymentReceivingReceipt.setReceivingReceipt(receivingReceipt);

		PurchasePaymentReceivingReceipt paymentReceivingReceipt2 = new PurchasePaymentReceivingReceipt();
		paymentReceivingReceipt2.setReceivingReceipt(receivingReceipt2);
		
		PurchasePaymentCashPayment cashPayment = new PurchasePaymentCashPayment();
		cashPayment.setAmount(new BigDecimal("100"));
		
		PurchasePaymentCashPayment cashPayment2 = new PurchasePaymentCashPayment();
		cashPayment2.setAmount(new BigDecimal("200"));
		
		PurchasePaymentCreditCardPayment creditCardPayment = new PurchasePaymentCreditCardPayment();
		creditCardPayment.setAmount(new BigDecimal("100"));
		
		PurchasePaymentCreditCardPayment creditCardPayment2 = new PurchasePaymentCreditCardPayment();
		creditCardPayment2.setAmount(new BigDecimal("200"));
		
		PurchasePaymentBankTransfer bankTransfer = new PurchasePaymentBankTransfer();
		bankTransfer.setAmount(new BigDecimal("100"));
		
		PurchasePaymentBankTransfer bankTransfer2 = new PurchasePaymentBankTransfer();
		bankTransfer2.setAmount(new BigDecimal("200"));
		
		PurchasePaymentCheckPayment checkPayment = new PurchasePaymentCheckPayment();
		checkPayment.setAmount(new BigDecimal("100"));
		
		PurchasePaymentCheckPayment checkPayment2 = new PurchasePaymentCheckPayment();
		checkPayment2.setAmount(new BigDecimal("200"));
		
		PurchasePaymentPaymentAdjustment adjustment = new PurchasePaymentPaymentAdjustment();
		adjustment.setAmount(new BigDecimal("100"));
		
		PurchasePaymentPaymentAdjustment adjustment2 = new PurchasePaymentPaymentAdjustment();
		adjustment2.setAmount(new BigDecimal("200"));
		
		PurchasePayment payment = new PurchasePayment();
		payment.setReceivingReceipts(Arrays.asList(paymentReceivingReceipt, paymentReceivingReceipt2));
		payment.setCashPayments(Arrays.asList(cashPayment, cashPayment2));
		payment.setCreditCardPayments(Arrays.asList(creditCardPayment, creditCardPayment2));
		payment.setBankTransfers(Arrays.asList(bankTransfer, bankTransfer2));
		payment.setCheckPayments(Arrays.asList(checkPayment, checkPayment2));
		payment.setPaymentAdjustments(Arrays.asList(adjustment, adjustment2));
		
		assertEquals(new BigDecimal("-1500.00"), payment.getOverOrShort());
	}
	
	@Test
	public void getTotalAmountDue() {
		ReceivingReceipt receivingReceipt = mock(ReceivingReceipt.class);
		when(receivingReceipt.getTotalNetAmountWithVat()).thenReturn(new BigDecimal("1000"));
		
		ReceivingReceipt receivingReceipt2 = mock(ReceivingReceipt.class);
		when(receivingReceipt2.getTotalNetAmountWithVat()).thenReturn(new BigDecimal("2000"));
		
		PurchasePaymentReceivingReceipt paymentReceivingReceipt = new PurchasePaymentReceivingReceipt();
		paymentReceivingReceipt.setReceivingReceipt(receivingReceipt);

		PurchasePaymentReceivingReceipt paymentReceivingReceipt2 = new PurchasePaymentReceivingReceipt();
		paymentReceivingReceipt2.setReceivingReceipt(receivingReceipt2);
		
		PurchasePaymentPaymentAdjustment adjustment = new PurchasePaymentPaymentAdjustment();
		adjustment.setAmount(new BigDecimal("100"));
		
		PurchasePaymentPaymentAdjustment adjustment2 = new PurchasePaymentPaymentAdjustment();
		adjustment2.setAmount(new BigDecimal("200"));
		
		PurchasePayment payment = new PurchasePayment();
		payment.setReceivingReceipts(Arrays.asList(paymentReceivingReceipt, paymentReceivingReceipt2));
		payment.setPaymentAdjustments(Arrays.asList(adjustment, adjustment2));
		
		assertEquals(new BigDecimal("2700.00"), payment.getTotalAmountDue());
	}
	
}