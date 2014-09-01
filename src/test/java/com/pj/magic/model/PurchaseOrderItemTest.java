package com.pj.magic.model;

import static org.junit.Assert.*;
import org.junit.Test;

public class PurchaseOrderItemTest {

	@Test
	public void equals() {
		Product product = new Product();
		product.setCode("TEST");
		
		PurchaseOrderItem item1 = new PurchaseOrderItem();
		item1.setProduct(product);
		item1.setUnit(Unit.CASE);
		
		PurchaseOrderItem item2 = new PurchaseOrderItem();
		item2.setProduct(product);
		item2.setUnit(Unit.CASE);
		
		PurchaseOrderItem item3 = new PurchaseOrderItem();
		item3.setProduct(product);
		item3.setUnit(Unit.CARTON);
		
		assertTrue(item1.equals(item2));
		assertFalse(item1.equals(item3));
	}
	
}
