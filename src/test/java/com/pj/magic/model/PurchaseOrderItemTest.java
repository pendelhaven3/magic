package com.pj.magic.model;

import static org.junit.Assert.*;

import java.math.BigDecimal;

import org.junit.Test;

public class PurchaseOrderItemTest {

	@Test
	public void getAmount_withActualQuantity() {
		PurchaseOrderItem item = new PurchaseOrderItem();
		item.setQuantity(10);
		item.setActualQuantity(5);
		item.setCost(new BigDecimal("10"));
		
		assertEquals(new BigDecimal("50"), item.getAmount());
	}

	@Test
	public void getAmount_withoutActualQuantity() {
		PurchaseOrderItem item = new PurchaseOrderItem();
		item.setQuantity(10);
		item.setCost(new BigDecimal("10"));
		
		assertEquals(new BigDecimal("100"), item.getAmount());
	}
	
}