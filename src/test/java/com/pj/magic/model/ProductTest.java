package com.pj.magic.model;

import static org.junit.Assert.*;
import org.junit.Test;

public class ProductTest {

	private Product product = new Product();
	
	@Test
	public void subtractUnitQuantity() {
		product.getUnitQuantities().add(new UnitQuantity(Unit.CASE, 10));
		product.getUnitQuantities().add(new UnitQuantity(Unit.CARTON, 10));
		product.subtractUnitQuantity(Unit.CASE, 1);
		product.subtractUnitQuantity(Unit.CARTON, 2);
		
		assertEquals(9, product.getUnitQuantities().get(0).getQuantity());
		assertEquals(8, product.getUnitQuantities().get(1).getQuantity());
	}
	
	@Test
	public void addUnitQuantity() {
		product.getUnitQuantities().add(new UnitQuantity(Unit.CASE, 10));
		product.addUnitQuantity(Unit.CASE, 10);
		
		assertEquals(20, product.getUnitQuantities().get(0).getQuantity());
	}
	
}
