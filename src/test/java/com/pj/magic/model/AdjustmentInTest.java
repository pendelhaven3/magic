package com.pj.magic.model;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;

public class AdjustmentInTest {

	private AdjustmentIn adjustmentIn;
	
	@Before
	public void setUp() {
		adjustmentIn = new AdjustmentIn();
	}
	
	@Test
	public void getTotalAmount() {
		AdjustmentInItem item = mock(AdjustmentInItem.class);
		when(item.getAmount()).thenReturn(new BigDecimal("10"));
		
		AdjustmentInItem item2 = mock(AdjustmentInItem.class);
		when(item2.getAmount()).thenReturn(new BigDecimal("20"));
		
		adjustmentIn.setItems(Arrays.asList(item, item2));
		
		assertEquals(new BigDecimal("30.00"), adjustmentIn.getTotalAmount());
	}
	
	@Test
	public void hasItems_true() {
		adjustmentIn.setItems(Arrays.asList(new AdjustmentInItem(), new AdjustmentInItem()));
		
		assertTrue(adjustmentIn.hasItems());
	}

	@Test
	public void hasItems_false() {
		assertFalse(adjustmentIn.hasItems());
	}
	
	@Test
	public void getTotalItems() {
		adjustmentIn.setItems(Arrays.asList(
				new AdjustmentInItem(),
				new AdjustmentInItem()));
		
		assertEquals(2, adjustmentIn.getTotalItems());
	}
	
	@Test
	public void getStatus_posted() {
		adjustmentIn.setPosted(true);
		
		assertEquals("Posted", adjustmentIn.getStatus());
	}

	@Test
	public void getStatus_new() {
		assertEquals("New", adjustmentIn.getStatus());
	}
	
}