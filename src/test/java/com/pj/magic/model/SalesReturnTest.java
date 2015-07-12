package com.pj.magic.model;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;

public class SalesReturnTest {

	private SalesReturn salesReturn;
	
	@Before
	public void setUp() {
		salesReturn = new SalesReturn();
	}
	
	@Test
	public void constructor_test() {
		salesReturn = new SalesReturn(1L);
		
		assertTrue(1L == salesReturn.getId());
	}
	
	@Test
	public void getStatus_cancelled() {
		salesReturn.setCancelled(true);
		
		assertEquals("Cancelled", salesReturn.getStatus());
	}
	
	@Test
	public void getStatus_paid() {
		salesReturn.setPaid(true);
		
		assertEquals("Paid", salesReturn.getStatus());
	}

	@Test
	public void getStatus_posted() {
		salesReturn.setPosted(true);
		
		assertEquals("Posted/Unpaid", salesReturn.getStatus());
	}
	
	@Test
	public void getStatus_new() {
		assertEquals("New", salesReturn.getStatus());
	}
	
	@Test
	public void getTotalAmount() {
		SalesReturnItem item = mock(SalesReturnItem.class);
		when(item.getAmount()).thenReturn(new BigDecimal("10"));
		
		SalesReturnItem item2 = mock(SalesReturnItem.class);
		when(item2.getAmount()).thenReturn(new BigDecimal("20"));
		
		salesReturn.setItems(Arrays.asList(item, item2));
		
		assertEquals(new BigDecimal("30.00"), salesReturn.getTotalAmount());
	}

	@Test
	public void getTotalItems() {
		salesReturn.setItems(Arrays.asList(new SalesReturnItem(), new SalesReturnItem()));
		
		assertEquals(2, salesReturn.getTotalItems());
	}

	@Test
	public void hasItems() {
		salesReturn.getItems().add(new SalesReturnItem());
		
		assertTrue(salesReturn.hasItems());
	}

	@Test
	public void hasItems_false() {
		assertFalse(salesReturn.hasItems());
	}
	
	@Test
	public void getTotalNetCost() {
		SalesReturnItem item = mock(SalesReturnItem.class);
		when(item.getNetCost()).thenReturn(new BigDecimal("10"));
		
		SalesReturnItem item2 = mock(SalesReturnItem.class);
		when(item2.getNetCost()).thenReturn(new BigDecimal("20"));
		
		salesReturn.setItems(Arrays.asList(item, item2));
		
		assertEquals(new BigDecimal("30.00"), salesReturn.getTotalNetCost());
	}
	
	@Test
	public void getTotalNetProfit() {
		SalesReturnItem item = mock(SalesReturnItem.class);
		when(item.getAmount()).thenReturn(new BigDecimal("10"));
		when(item.getNetCost()).thenReturn(new BigDecimal("5"));
		
		SalesReturnItem item2 = mock(SalesReturnItem.class);
		when(item2.getAmount()).thenReturn(new BigDecimal("20"));
		when(item2.getNetCost()).thenReturn(new BigDecimal("10"));
		
		salesReturn.setItems(Arrays.asList(item, item2));
		
		assertEquals(new BigDecimal("15.00"), salesReturn.getTotalNetProfit());
	}
	
	@Test
	public void isNew() {
		assertTrue(salesReturn.isNew());
	}
	
	@Test
	public void isNew_posted() {
		salesReturn.setPosted(true);
		
		assertFalse(salesReturn.isNew());
	}
	
	@Test
	public void isNew_cancelled() {
		salesReturn.setCancelled(true);
		
		assertFalse(salesReturn.isNew());
	}
	
}