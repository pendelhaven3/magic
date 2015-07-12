package com.pj.magic.model;

import static org.junit.Assert.*;

import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;

public class StockQuantityConversionTest {

	private StockQuantityConversion stockQuantityConversion;
	
	@Before
	public void setUp() {
		stockQuantityConversion = new StockQuantityConversion();
	}
	
	@Test
	public void constructor_test() {
		stockQuantityConversion = new StockQuantityConversion(1L);
		
		assertTrue(1L == stockQuantityConversion.getId());
	}
	
	@Test
	public void getTotalNumberOfItems() {
		stockQuantityConversion.setItems(
				Arrays.asList(new StockQuantityConversionItem(), new StockQuantityConversionItem()));
		
		assertEquals(2, stockQuantityConversion.getTotalNumberOfItems());
	}
	
	@Test
	public void hasItems() {
		stockQuantityConversion.getItems().add(new StockQuantityConversionItem());
		
		assertTrue(stockQuantityConversion.hasItems());
	}
	
	@Test
	public void hasItems_false() {
		assertFalse(stockQuantityConversion.hasItems());
	}
	
	@Test
	public void getStatus_posted() {
		stockQuantityConversion.setPosted(true);
		
		assertEquals("Yes", stockQuantityConversion.getStatus());
	}

	@Test
	public void getStatus_notPosted() {
		assertEquals("No", stockQuantityConversion.getStatus());
	}
	
}
