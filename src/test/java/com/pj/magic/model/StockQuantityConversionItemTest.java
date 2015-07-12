package com.pj.magic.model;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import org.junit.Before;
import org.junit.Test;

public class StockQuantityConversionItemTest {

	private StockQuantityConversionItem item;
	
	@Before
	public void setUp() {
		item = new StockQuantityConversionItem();
	}
	
	@Test
	public void getConvertedQuantity_valueSet() {
		item.setConvertedQuantity(5);
		
		assertEquals(5, item.getConvertedQuantity());
	}
	
	@Test
	public void getConvertedQuantity() {
		Product product = new Product();
		product.getUnitConversions().add(new UnitConversion(Unit.PIECES, 1));
		product.getUnitConversions().add(new UnitConversion(Unit.CARTON, 6));
		product.getUnitConversions().add(new UnitConversion(Unit.CASE, 36));
		
		item.setProduct(product);
		item.setFromUnit(Unit.CASE);
		item.setQuantity(2);
		item.setToUnit(Unit.CARTON);
		
		assertEquals(12, item.getConvertedQuantity());
	}

	@Test
	public void compareTo() {
		Product product = mock(Product.class);
		when(product.compareTo(any(Product.class))).thenReturn(1);
		
		item.setProduct(product);
		
		StockQuantityConversionItem item2 = new StockQuantityConversionItem();
		item2.setProduct(new Product());
		
		assertEquals(1, item.compareTo(item2));
	}
	
	@Test
	public void hashCode_test() {
		item.setProduct(new Product());
		item.setFromUnit(Unit.CASE);
		item.setToUnit(Unit.PIECES);
		
		assertEquals(4281315, item.hashCode());
	}
	
	@Test
	public void equals_null() {
		assertFalse(item.equals(null));
	}
	
	@Test
	public void equals_differentClass() {
		assertFalse(item.equals(new Object()));
	}

	@Test
	public void equals() {
		item.setProduct(new Product());
		item.getProduct().setCode("CODE");
		item.setFromUnit(Unit.CASE);
		item.setToUnit(Unit.PIECES);
		
		StockQuantityConversionItem item2 = new StockQuantityConversionItem();
		item2.setProduct(new Product());
		item2.getProduct().setCode("CODE");
		item2.setFromUnit(Unit.CASE);
		item2.setToUnit(Unit.PIECES);
		
		assertTrue(item.equals(item2));
	}
	
}