package com.pj.magic.model;

import static org.junit.Assert.*;

import java.math.BigDecimal;
import java.util.Arrays;

import org.junit.Test;

public class AdjustmentInTest {

	@Test
	public void getTotalAmount() {
		AdjustmentIn adjustmentIn = new AdjustmentIn();
		adjustmentIn.setItems(Arrays.asList(
				createItem("10", 1),
				createItem("20", 2)));
		
		assertEquals(new BigDecimal("50.00"), adjustmentIn.getTotalAmount());
	}
	
	private AdjustmentInItem createItem(String cost, int quantity) {
		AdjustmentInItem item = new AdjustmentInItem();
		item.setProduct(new Product());
		item.setCost(new BigDecimal(cost));
		item.setQuantity(quantity);
		return item;
	}
	
	@Test
	public void hasItems_true() {
		AdjustmentIn adjustmentIn = new AdjustmentIn();
		adjustmentIn.setItems(Arrays.asList(
				createItem("10", 1),
				createItem("20", 2)));
		
		assertTrue(adjustmentIn.hasItems());
	}

	@Test
	public void hasItems_false() {
		assertFalse(new AdjustmentIn().hasItems());
	}
	
	@Test
	public void getTotalItems() {
		AdjustmentIn adjustmentIn = new AdjustmentIn();
		adjustmentIn.setItems(Arrays.asList(
				new AdjustmentInItem(),
				new AdjustmentInItem()));
		
		assertEquals(2, adjustmentIn.getTotalItems());
	}
	
}