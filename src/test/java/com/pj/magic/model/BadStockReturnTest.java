package com.pj.magic.model;

import static org.junit.Assert.*;

import java.math.BigDecimal;
import java.util.Arrays;

import org.apache.commons.lang.StringUtils;
import org.junit.Test;

public class BadStockReturnTest {

	@Test
	public void getTotalItems() {
		BadStockReturn badStockReturn = new BadStockReturn();
		badStockReturn.setItems(Arrays.asList(
				new BadStockReturnItem(),
				new BadStockReturnItem()));
		
		assertEquals(2, badStockReturn.getTotalItems());
	}
	
	@Test
	public void hasItems_true() {
		BadStockReturn badStockReturn = new BadStockReturn();
		badStockReturn.setItems(Arrays.asList(
				new BadStockReturnItem(),
				new BadStockReturnItem()));
		
		assertTrue(badStockReturn.hasItems());
	}
	
	@Test
	public void hasItems_false() {
		assertFalse(new BadStockReturn().hasItems());
	}
	
	@Test
	public void getTotalAmount() {
		BadStockReturn badStockReturn = new BadStockReturn();
		badStockReturn.setItems(Arrays.asList(
				createItem("10", 1),
				createItem("20", 2)));
		
		assertEquals(new BigDecimal("50.00"), badStockReturn.getTotalAmount());
	}
	
	private BadStockReturnItem createItem(String unitPrice, int quantity) {
		return createItem(unitPrice, quantity, null);
	}
	
	private BadStockReturnItem createItem(String unitPrice, int quantity, String cost) {
		BadStockReturnItem item = new BadStockReturnItem();
		item.setProduct(new Product());
		item.setUnitPrice(new BigDecimal(unitPrice));
		item.setQuantity(quantity);
		if (!StringUtils.isEmpty(cost)) {
			item.setCost(new BigDecimal(cost));
		}
		return item;
	}
	
	@Test
	public void getTotalCost() {
		BadStockReturn badStockReturn = new BadStockReturn();
		badStockReturn.setItems(Arrays.asList(
				createItem("10", 1, "8"),
				createItem("20", 2, "16")));
		
		assertEquals(new BigDecimal("40.00"), badStockReturn.getTotalCost());
	}
	
	@Test
	public void getTotalLoss() {
		BadStockReturn badStockReturn = new BadStockReturn();
		badStockReturn.setItems(Arrays.asList(
				createItem("10", 1, "8"),
				createItem("20", 2, "16")));
		
		assertEquals(new BigDecimal("10.00"), badStockReturn.getTotalLoss());
	}
	
}