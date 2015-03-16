package com.pj.magic.model;

import static org.junit.Assert.*;

import java.math.BigDecimal;

import org.junit.Test;

public class BadStockReturnItemTest {

	@Test
	public void getAmount() {
		BadStockReturnItem item = new BadStockReturnItem();
		item.setUnitPrice(new BigDecimal("10"));
		item.setQuantity(2);
		
		assertEquals(new BigDecimal("20"), item.getAmount());
	}
	
	@Test
	public void getAmount_quantityIsNull() {
		BadStockReturnItem item = new BadStockReturnItem();
		item.setUnitPrice(new BigDecimal("10"));
		
		assertNull(item.getAmount());
	}

	@Test
	public void getAmount_unitPriceIsNull() {
		BadStockReturnItem item = new BadStockReturnItem();
		item.setQuantity(2);
		
		assertNull(item.getAmount());
	}

	@Test
	public void getTotalCost() {
		BadStockReturnItem item = new BadStockReturnItem();
		item.setCost(new BigDecimal("8"));
		item.setQuantity(2);
		
		assertEquals(new BigDecimal("16"), item.getTotalCost());
	}
	
	@Test
	public void getTotalLoss() {
		BadStockReturnItem item = new BadStockReturnItem();
		item.setUnitPrice(new BigDecimal("10"));
		item.setQuantity(2);
		item.setCost(new BigDecimal("8"));
		
		assertEquals(new BigDecimal("4"), item.getTotalLoss());
	}
	
}