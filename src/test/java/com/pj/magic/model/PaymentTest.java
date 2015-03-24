package com.pj.magic.model;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.util.Arrays;

import org.junit.Test;

public class PaymentTest {

	@Test
	public void getTotalAmountDue() {
		Payment payment = new Payment();
		payment.setSalesInvoices(Arrays.asList(
				createPaymentSalesInvoiceWithAmount("10"),
				createPaymentSalesInvoiceWithAmount("20")));
		
		assertEquals(new BigDecimal("30.00"), payment.getTotalAmountDue());
	}
	
	private PaymentSalesInvoice createPaymentSalesInvoiceWithAmount(String amount) {
		PaymentSalesInvoice paymentSalesInvoice = mock(PaymentSalesInvoice.class);
		when(paymentSalesInvoice.getAmountDue()).thenReturn(new BigDecimal(amount));
		return paymentSalesInvoice;
	}

	@Test
	public void getTotalCheckPayments() {
		Payment payment = new Payment();
		payment.setCheckPayments(Arrays.asList(
				createPaymentCheckPaymentWithAmount("10"),
				createPaymentCheckPaymentWithAmount("20")));
		
		assertEquals(new BigDecimal("30.00"), payment.getTotalCheckPayments());
	}
	
	private PaymentCheckPayment createPaymentCheckPaymentWithAmount(String amount) {
		PaymentCheckPayment paymentCheckPayment = new PaymentCheckPayment();
		paymentCheckPayment.setAmount(new BigDecimal(amount));
		return paymentCheckPayment;
	}
	
	@Test
	public void getTotalCashPayments() {
		Payment payment = new Payment();
		payment.setCashPayments(Arrays.asList(
				createPaymentCashPaymentWithAmount("10"),
				createPaymentCashPaymentWithAmount("20")));
		
		assertEquals(new BigDecimal("30.00"), payment.getTotalCashPayments());
	}
	
	private PaymentCashPayment createPaymentCashPaymentWithAmount(String amount) {
		PaymentCashPayment paymentCashPayment = new PaymentCashPayment();
		paymentCashPayment.setAmount(new BigDecimal(amount));
		return paymentCashPayment;
	}
	
	@Test
	public void getTotalPayments() {
		Payment payment = new Payment();
		payment.setCashPayments(Arrays.asList(
				createPaymentCashPaymentWithAmount("10"),
				createPaymentCashPaymentWithAmount("20")));
		payment.setCheckPayments(Arrays.asList(
				createPaymentCheckPaymentWithAmount("30"),
				createPaymentCheckPaymentWithAmount("40")));
		
		assertEquals(new BigDecimal("100.00"), payment.getTotalPayments());
	}
	
	@Test
	public void getTotalAdjustments() {
		Payment payment = new Payment();
		payment.setAdjustments(Arrays.asList(
				createPaymentAdjustmentWithAmount("10"),
				createPaymentAdjustmentWithAmount("20")));
		
		assertEquals(new BigDecimal("30.00"), payment.getTotalAdjustments());
	}
	
	private PaymentPaymentAdjustment createPaymentAdjustmentWithAmount(String amount) {
		PaymentPaymentAdjustment paymentAdjustment = new PaymentPaymentAdjustment();
		paymentAdjustment.setAmount(new BigDecimal(amount));
		return paymentAdjustment;
	}
	
	@Test
	public void getOverOrShort() {
		Payment payment = new Payment();
		payment.setSalesInvoices(Arrays.asList(
				createPaymentSalesInvoiceWithAmount("100"),
				createPaymentSalesInvoiceWithAmount("200")));
		payment.setCashPayments(Arrays.asList(
				createPaymentCashPaymentWithAmount("10"),
				createPaymentCashPaymentWithAmount("20")));
		payment.setCheckPayments(Arrays.asList(
				createPaymentCheckPaymentWithAmount("30"),
				createPaymentCheckPaymentWithAmount("40")));
		payment.setAdjustments(Arrays.asList(
				createPaymentAdjustmentWithAmount("10"),
				createPaymentAdjustmentWithAmount("20")));
		
		assertEquals(new BigDecimal("-170.00"), payment.getOverOrShort());
	}
	
	@Test
	public void getTotalNetAmount() {
		Payment payment = new Payment();
		payment.setSalesInvoices(Arrays.asList(
				createPaymentSalesInvoiceWithTotalNetAmount("10"),
				createPaymentSalesInvoiceWithTotalNetAmount("20")));
		
		assertEquals(new BigDecimal("30.00"), payment.getTotalNetAmount());
	}
	
	private PaymentSalesInvoice createPaymentSalesInvoiceWithTotalNetAmount(String amount) {
		PaymentSalesInvoice paymentSalesInvoice = new PaymentSalesInvoice();
		SalesInvoice salesInvoice = mock(SalesInvoice.class);
		when(salesInvoice.getTotalNetAmount()).thenReturn(new BigDecimal(amount));
		paymentSalesInvoice.setSalesInvoice(salesInvoice);
		return paymentSalesInvoice;
	}

	@Test
	public void getTotalAdjustedAmount() {
		Payment payment = new Payment();
		payment.setSalesInvoices(Arrays.asList(
				createPaymentSalesInvoiceWithAdjustedAmount("10"),
				createPaymentSalesInvoiceWithAdjustedAmount("20")));
		
		assertEquals(new BigDecimal("30.00"), payment.getTotalAdjustedAmount());
	}
	
	private PaymentSalesInvoice createPaymentSalesInvoiceWithAdjustedAmount(String amount) {
		PaymentSalesInvoice paymentSalesInvoice = new PaymentSalesInvoice();
		paymentSalesInvoice.setAdjustedAmount(new BigDecimal(amount));
		return paymentSalesInvoice;
	}
	
}