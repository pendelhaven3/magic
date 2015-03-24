package com.pj.magic.model;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.util.Arrays;

import org.junit.Test;

public class PaymentSalesInvoiceTest {

	@Test
	public void getAdjustedAmount_adjustedAmountIsSet() {
		PaymentSalesInvoice paymentSalesInvoice = new PaymentSalesInvoice();
		BigDecimal adjustedAmount = new BigDecimal("10");
		paymentSalesInvoice.setAdjustedAmount(adjustedAmount);
		
		assertEquals(adjustedAmount, paymentSalesInvoice.getAdjustedAmount());
	}
	
	@Test
	public void getAdjustedAmount_adjustedAmountIsNotSet() {
		PaymentSalesInvoice paymentSalesInvoice = new PaymentSalesInvoice();
		paymentSalesInvoice.setSalesReturns(Arrays.asList(
				createSalesReturnWithAmount("10"),
				createSalesReturnWithAmount("20")));
		
		assertEquals(new BigDecimal("30.00"), paymentSalesInvoice.getAdjustedAmount());
	}
	
	private SalesReturn createSalesReturnWithAmount(String amount) {
		SalesReturn salesReturn = mock(SalesReturn.class);
		when(salesReturn.getTotalAmount()).thenReturn(new BigDecimal(amount));
		return salesReturn;
	}
	
	@Test
	public void getAmountDue() {
		PaymentSalesInvoice paymentSalesInvoice = new PaymentSalesInvoice();
		paymentSalesInvoice.setSalesInvoice(createSalesInvoiceWithNetAmount("20"));
		paymentSalesInvoice.setAdjustedAmount(new BigDecimal("5"));
		
		assertEquals(new BigDecimal("15"), paymentSalesInvoice.getAmountDue());
	}
	
	private SalesInvoice createSalesInvoiceWithNetAmount(String amount) {
		SalesInvoice salesInvoice = mock(SalesInvoice.class);
		when(salesInvoice.getTotalNetAmount()).thenReturn(new BigDecimal(amount));
		return salesInvoice;
	}
	
}