package com.pj.magic.model;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.junit.Before;
import org.junit.Test;

public class ReceivingReceiptItemTest {

	private ReceivingReceiptItem item;
	
	@Before
	public void setUp() {
		item = new ReceivingReceiptItem();
	}
	
	@Test
	public void getAmount() {
		item.setCost(new BigDecimal("1.50"));
		item.setQuantity(5);
		
		assertEquals(new BigDecimal("7.50"), item.getAmount());
	}
	
	@Test
	public void getNetAmount() {
		item.setCost(new BigDecimal("20"));
		item.setQuantity(5);
		item.setDiscount1(new BigDecimal("5"));
		item.setDiscount2(new BigDecimal("5"));
		item.setDiscount3(new BigDecimal("5"));
		item.setFlatRateDiscount(new BigDecimal("10.00"));
		
		assertEquals(new BigDecimal("75.74"), item.getNetAmount().setScale(2, RoundingMode.HALF_UP));
	}

	@Test
	public void getNetAmount_noDiscounts() {
		item.setCost(new BigDecimal("20"));
		item.setQuantity(5);
		
		assertEquals(new BigDecimal("100.00"), item.getNetAmount().setScale(2, RoundingMode.HALF_UP));
	}

	@Test
	public void getDiscountedAmount() {
		item.setCost(new BigDecimal("20"));
		item.setQuantity(5);
		item.setDiscount1(new BigDecimal("5"));
		item.setDiscount2(new BigDecimal("5"));
		item.setDiscount3(new BigDecimal("5"));
		item.setFlatRateDiscount(new BigDecimal("10.00"));
		
		assertEquals(new BigDecimal("24.26"), item.getDiscountedAmount().setScale(2, RoundingMode.HALF_UP));
	}

	@Test
	public void getFinalCost() {
		item.setCost(new BigDecimal("20"));
		item.setQuantity(5);
		item.setDiscount1(new BigDecimal("5"));
		item.setDiscount2(new BigDecimal("5"));
		item.setDiscount3(new BigDecimal("5"));
		item.setFlatRateDiscount(new BigDecimal("10.00"));
		
		assertEquals(new BigDecimal("15.15"), item.getFinalCost().setScale(2, RoundingMode.HALF_UP));
	}

	@Test
	public void compareTo() {
		Product product = mock(Product.class);
		when(product.compareTo(any(Product.class))).thenReturn(1);
		
		item.setProduct(product);
		
		ReceivingReceiptItem item2 = new ReceivingReceiptItem();
		item2.setProduct(new Product());
		
		assertEquals(1, item.compareTo(item2));
	}
	
	@Test
	public void compareTo_sameProduct() {
		Product product = mock(Product.class);
		when(product.compareTo(any(Product.class))).thenReturn(0);
		
		item.setProduct(product);
		item.setUnit(Unit.CASE);
		
		ReceivingReceiptItem item2 = new ReceivingReceiptItem();
		item2.setProduct(new Product());
		item2.setUnit(Unit.CARTON);
		
		assertEquals(1, item.compareTo(item2));
	}
	
	@Test
	public void getFinalCostWithVat() {
		ReceivingReceipt receivingReceipt = mock(ReceivingReceipt.class);
		when(receivingReceipt.getVatMultiplier()).thenReturn(new BigDecimal("1.12"));
		
		item.setParent(receivingReceipt);
		item.setCost(new BigDecimal("20"));
		item.setQuantity(5);
		item.setDiscount1(new BigDecimal("5"));
		item.setDiscount2(new BigDecimal("5"));
		item.setDiscount3(new BigDecimal("5"));
		item.setFlatRateDiscount(new BigDecimal("10.00"));
		
		assertEquals(new BigDecimal("16.97"), item.getFinalCostWithVat().setScale(2, RoundingMode.HALF_UP));
	}
	
}