package com.pj.magic.model;

import static org.junit.Assert.*;

import java.math.BigDecimal;

import org.junit.Before;
import org.junit.Test;

public class PurchaseReturnBadStockItemTest {

	private PurchaseReturnBadStockItem item;
	
	@Before
	public void setUp() {
		item = new PurchaseReturnBadStockItem();
	}
	
	@Test
	public void getAmount() {
		item.setQuantity(5);
		item.setUnitCost(new BigDecimal("1.50"));
		
		assertEquals(new BigDecimal("7.50"), item.getAmount());
	}

	@Test
	public void getAmount_noQuantity() {
		item.setUnitCost(new BigDecimal("1.50"));
		
		assertNull(item.getAmount());
	}

	@Test
	public void getAmount_noUnitCost() {
		item.setQuantity(5);
		
		assertNull(item.getAmount());
	}
	
}