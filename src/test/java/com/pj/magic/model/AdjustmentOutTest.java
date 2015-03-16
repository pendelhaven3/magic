package com.pj.magic.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;
import java.util.Arrays;

import org.junit.Test;

public class AdjustmentOutTest {

	@Test
	public void getTotalAmount() {
		AdjustmentOut adjustmentOut = new AdjustmentOut();
		adjustmentOut.setItems(Arrays.asList(
				createItem("10", 1),
				createItem("20", 2)));
		
		assertEquals(new BigDecimal("50.00"), adjustmentOut.getTotalAmount());
	}
	
	private AdjustmentOutItem createItem(String cost, int quantity) {
		AdjustmentOutItem item = new AdjustmentOutItem();
		item.setProduct(new Product());
		item.setUnitPrice(new BigDecimal(cost));
		item.setQuantity(quantity);
		return item;
	}
	
	@Test
	public void hasItems_true() {
		AdjustmentOut adjustmentOut = new AdjustmentOut();
		adjustmentOut.setItems(Arrays.asList(
				createItem("10", 1),
				createItem("20", 2)));
		
		assertTrue(adjustmentOut.hasItems());
	}

	@Test
	public void hasItems_false() {
		assertFalse(new AdjustmentOut().hasItems());
	}
	
	@Test
	public void getTotalItems() {
		AdjustmentOut adjustmentOut = new AdjustmentOut();
		adjustmentOut.setItems(Arrays.asList(
				new AdjustmentOutItem(),
				new AdjustmentOutItem()));
		
		assertEquals(2, adjustmentOut.getTotalItems());
	}
	
}