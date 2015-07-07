package com.pj.magic.model;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;

public class PurchaseReturnBadStockTest {

	private PurchaseReturnBadStock purchaseReturnBadStock;
	
	@Before
	public void setUp() {
		purchaseReturnBadStock = new PurchaseReturnBadStock();
	}
	
	@Test
	public void getStatus_posted() {
		purchaseReturnBadStock.setPosted(true);
		assertEquals("Posted", purchaseReturnBadStock.getStatus());
	}

	@Test
	public void getStatus_new() {
		assertEquals("New", purchaseReturnBadStock.getStatus());
	}
	
	@Test
	public void getTotalItems() {
		purchaseReturnBadStock.setItems(
				Arrays.asList(new PurchaseReturnBadStockItem(), new PurchaseReturnBadStockItem()));
		assertEquals(2, purchaseReturnBadStock.getTotalItems());
	}
	
	@Test
	public void hasItems_true() {
		purchaseReturnBadStock.setItems(Arrays.asList(new PurchaseReturnBadStockItem()));
		assertTrue(purchaseReturnBadStock.hasItems());
	}

	@Test
	public void hasItems_false() {
		assertFalse(purchaseReturnBadStock.hasItems());
	}
	
	@Test
	public void getTotalAmount() {
		PurchaseReturnBadStockItem item = mock(PurchaseReturnBadStockItem.class);
		when(item.getAmount()).thenReturn(new BigDecimal("10"));
		
		PurchaseReturnBadStockItem item2 = mock(PurchaseReturnBadStockItem.class);
		when(item2.getAmount()).thenReturn(new BigDecimal("20"));
		
		purchaseReturnBadStock.setItems(Arrays.asList(item, item2));
		
		assertEquals(new BigDecimal("30.00"), purchaseReturnBadStock.getTotalAmount());
	}
	
}