package com.pj.magic.model;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;

public class PurchaseReturnTest {
	
	private PurchaseReturn purchaseReturn;
	
	@Before
	public void setUp() {
		purchaseReturn = new PurchaseReturn();
	}

	@Test
	public void getStatus_paid() {
		purchaseReturn.setPaid(true);
		assertEquals("Paid", purchaseReturn.getStatus());
	}
	
	@Test
	public void getStatus_postedUnpaid() {
		purchaseReturn.setPosted(true);
		assertEquals("Posted/Unpaid", purchaseReturn.getStatus());
	}

	@Test
	public void getStatus_new() {
		assertEquals("New", purchaseReturn.getStatus());
	}
	
	@Test
	public void getTotalAmount() {
		PurchaseReturnItem item = mock(PurchaseReturnItem.class);
		when(item.getAmount()).thenReturn(new BigDecimal("100"));
		
		PurchaseReturnItem item2 = mock(PurchaseReturnItem.class);
		when(item2.getAmount()).thenReturn(new BigDecimal("200"));
		
		purchaseReturn.setItems(Arrays.asList(item, item2));
		
		assertEquals(new BigDecimal("300.00"), purchaseReturn.getTotalAmount());
	}

	@Test
	public void getTotalItems() {
		purchaseReturn.setItems(Arrays.asList(new PurchaseReturnItem(), new PurchaseReturnItem()));
		assertEquals(2, purchaseReturn.getTotalItems());
	}
	
	@Test
	public void hasItems_true() {
		purchaseReturn.setItems(Arrays.asList(new PurchaseReturnItem(), new PurchaseReturnItem()));
		assertTrue(purchaseReturn.hasItems());
	}
	
	@Test
	public void hasItems_false() {
		assertFalse(purchaseReturn.hasItems());
	}
	
}