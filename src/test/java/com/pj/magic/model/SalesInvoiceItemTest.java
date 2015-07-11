package com.pj.magic.model;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.junit.Before;
import org.junit.Test;

public class SalesInvoiceItemTest {

	private SalesInvoiceItem item;
	
	@Before
	public void setUp() {
		item = new SalesInvoiceItem();
	}
	
	@Test
	public void constructor_salesInvoiceItem() {
		SalesInvoiceItem item2 = new SalesInvoiceItem();
		item2.setProduct(new Product());
		item2.setUnit(Unit.CASE);
		item2.setQuantity(5);
		item2.setUnitPrice(new BigDecimal("10"));
		item2.setDiscount1(new BigDecimal("5"));
		item2.setDiscount2(new BigDecimal("3"));
		item2.setDiscount3(new BigDecimal("2"));
		item2.setFlatRateDiscount(new BigDecimal("20"));
		
		item = new SalesInvoiceItem(item2);
		
		assertSame(item2.getProduct(), item.getProduct());
		assertEquals(item2.getUnit(), item.getUnit());
		assertEquals(item2.getQuantity(), item.getQuantity());
		assertEquals(item2.getUnitPrice(), item.getUnitPrice());
		assertEquals(item2.getDiscount1(), item.getDiscount1());
		assertEquals(item2.getDiscount2(), item.getDiscount2());
		assertEquals(item2.getDiscount3(), item.getDiscount3());
		assertEquals(item2.getFlatRateDiscount(), item.getFlatRateDiscount());
	}
	
	@Test
	public void getAmount() {
		item.setUnitPrice(new BigDecimal("10"));
		item.setQuantity(5);
		
		assertEquals(new BigDecimal("50.00"), item.getAmount());
	}
	
	@Test
	public void compareTo_differentProducts() {
		Product product = mock(Product.class);
		when(product.compareTo(any(Product.class))).thenReturn(1);
		
		SalesInvoiceItem item1 = new SalesInvoiceItem();
		item1.setProduct(product);
		
		SalesInvoiceItem item2 = new SalesInvoiceItem();
		item2.setProduct(new Product());
		
		assertEquals(1, item1.compareTo(item2));
	}

	@Test
	public void compare_sameProducts() {
		Product product = mock(Product.class);
		when(product.compareTo(any(Product.class))).thenReturn(0);
		
		SalesInvoiceItem item1 = new SalesInvoiceItem();
		item1.setProduct(product);
		item1.setUnit(Unit.CASE);
		
		SalesInvoiceItem item2 = new SalesInvoiceItem();
		item2.setProduct(product);
		item2.setUnit(Unit.CARTON);
		
		assertEquals(1, item1.compareTo(item2));
	}
	
	@Test
	public void getNetAmount() {
		item.setUnitPrice(new BigDecimal("20"));
		item.setQuantity(5);
		item.setDiscount1(new BigDecimal("5"));
		item.setDiscount2(new BigDecimal("5"));
		item.setDiscount3(new BigDecimal("5"));
		item.setFlatRateDiscount(new BigDecimal("10.00"));
		
		assertEquals(new BigDecimal("75.74"), item.getNetAmount().setScale(2, RoundingMode.HALF_UP));
	}
	
	@Test
	public void getNetAmount_noDiscounts() {
		item.setUnitPrice(new BigDecimal("20"));
		item.setQuantity(5);
		
		assertEquals(new BigDecimal("100.00"), item.getNetAmount().setScale(2, RoundingMode.HALF_UP));
	}
	
	@Test
	public void getDiscountAmount() {
		item.setUnitPrice(new BigDecimal("20"));
		item.setQuantity(5);
		item.setDiscount1(new BigDecimal("5"));
		item.setDiscount2(new BigDecimal("5"));
		item.setDiscount3(new BigDecimal("5"));
		item.setFlatRateDiscount(new BigDecimal("10.00"));
		
		assertEquals(new BigDecimal("24.26"), item.getDiscountedAmount().setScale(2, RoundingMode.HALF_UP));
	}
	
	@Test
	public void hashCode_test() {
		item.setId(1L);
		
		assertEquals(630, item.hashCode());
	}

	@Test
	public void equals_null() {
		assertFalse(item.equals(null));
	}

	@Test
	public void equals_differentClass() {
		assertFalse(item.equals(new Object()));
	}
	
	@Test
	public void equals_test() {
		item.setId(1L);
		
		assertTrue(item.equals(new SalesInvoiceItem(1L)));
	}
	
	@Test
	public void getNetCost() {
		item.setCost(new BigDecimal("20"));
		item.setQuantity(5);
		
		assertEquals(new BigDecimal("100"), item.getNetCost());
	}

	@Test
	public void getDiscountedUnitPrice() {
		item.setUnitPrice(new BigDecimal("20"));
		item.setQuantity(5);
		item.setDiscount1(new BigDecimal("5"));
		item.setDiscount2(new BigDecimal("5"));
		item.setDiscount3(new BigDecimal("5"));
		item.setFlatRateDiscount(new BigDecimal("10.00"));
		
		assertEquals(new BigDecimal("15.15"), item.getDiscountedUnitPrice().setScale(2, RoundingMode.HALF_UP));
	}

	@Test
	public void getDiscountedUnitPrice_noDiscounts() {
		item.setUnitPrice(new BigDecimal("20"));
		item.setQuantity(5);
		
		assertEquals(new BigDecimal("20.00"), item.getDiscountedUnitPrice().setScale(2, RoundingMode.HALF_UP));
	}
	
}