package com.pj.magic.model;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;

import org.junit.Before;
import org.junit.Test;

public class SalesReturnItemTest {

	private SalesReturnItem item;
	
	@Before
	public void setUp() {
		item = new SalesReturnItem();
	}
	
	@Test
	public void getAmount() {
		SalesInvoiceItem salesInvoiceItem = mock(SalesInvoiceItem.class);
		when(salesInvoiceItem.getDiscountedUnitPrice()).thenReturn(new BigDecimal("1.50"));
		
		item.setSalesInvoiceItem(salesInvoiceItem);
		item.setQuantity(5);
		
		assertEquals(new BigDecimal("7.50"), item.getAmount());
	}
	
	@Test
	public void getAmount_noSalesInvoiceItem() {
		item.setQuantity(5);
		
		assertNull(item.getAmount());
	}

	@Test
	public void getAmount_noQuantity() {
		SalesInvoiceItem salesInvoiceItem = mock(SalesInvoiceItem.class);
		when(salesInvoiceItem.getDiscountedUnitPrice()).thenReturn(new BigDecimal("1.50"));
		
		item.setSalesInvoiceItem(salesInvoiceItem);
		
		assertNull(item.getAmount());
	}

	@Test
	public void getUnitPrice() {
		SalesInvoiceItem salesInvoiceItem = mock(SalesInvoiceItem.class);
		when(salesInvoiceItem.getDiscountedUnitPrice()).thenReturn(new BigDecimal("1.50"));
		
		item.setSalesInvoiceItem(salesInvoiceItem);
		
		assertEquals(new BigDecimal("1.50"), item.getUnitPrice());
	}

	@Test
	public void getUnitPrice_noSalesInvoiceItem() {
		assertNull(item.getUnitPrice());
	}

	@Test
	public void getCost() {
		SalesInvoiceItem salesInvoiceItem = mock(SalesInvoiceItem.class);
		when(salesInvoiceItem.getCost()).thenReturn(new BigDecimal("1.50"));
		
		item.setSalesInvoiceItem(salesInvoiceItem);
		
		assertEquals(new BigDecimal("1.50"), item.getCost());
	}

	@Test
	public void getCost_noSalesInvoiceItem() {
		assertNull(item.getCost());
	}
	
	@Test
	public void getNetCost() {
		SalesInvoiceItem salesInvoiceItem = mock(SalesInvoiceItem.class);
		when(salesInvoiceItem.getCost()).thenReturn(new BigDecimal("1.50"));
		
		item.setSalesInvoiceItem(salesInvoiceItem);
		item.setQuantity(5);
		
		assertEquals(new BigDecimal("7.50"), item.getNetCost());
	}
	
}