package com.pj.magic.model;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;

import org.junit.Before;
import org.junit.Test;

public class SalesRequisitionItemTest {

	private SalesRequisitionItem item;
	
	@Before
	public void setUp() {
		item = new SalesRequisitionItem();
	}
	
	@Test
	public void isQuantityValid() {
		String unit = Unit.CASE;
		int quantity = 5;
		
		Product product = mock(Product.class);
		when(product.hasAvailableUnitQuantity(unit, quantity)).thenReturn(true);
		
		item.setProduct(product);
		item.setUnit(unit);
		item.setQuantity(quantity);
		
		assertTrue(item.isQuantityValid());
	}
	
	@Test
	public void isValid() {
		String unit = Unit.CASE;
		int quantity = 5;
		
		Product product = mock(Product.class);
		when(product.hasUnit(unit)).thenReturn(true);
		when(product.hasAvailableUnitQuantity(unit, quantity)).thenReturn(true);
		
		item.setProduct(product);
		item.setUnit(unit);
		item.setQuantity(quantity);
		
		assertTrue(item.isValid());
	}
	
	@Test
	public void isValid_noProduct() {
		String unit = Unit.CASE;
		int quantity = 5;
		
		item.setUnit(unit);
		item.setQuantity(quantity);
		
		assertFalse(item.isValid());
	}
	
	@Test
	public void isValid_productHasNoUnit() {
		String unit = Unit.CASE;
		int quantity = 5;
		
		Product product = mock(Product.class);
		when(product.hasUnit(unit)).thenReturn(false);
		when(product.hasAvailableUnitQuantity(unit, quantity)).thenReturn(true);
		
		item.setProduct(product);
		item.setUnit(unit);
		item.setQuantity(quantity);
		
		assertFalse(item.isValid());
	}

	@Test
	public void isValid_noQuantity() {
		String unit = Unit.CASE;
		
		Product product = mock(Product.class);
		when(product.hasUnit(unit)).thenReturn(false);
		when(product.hasAvailableUnitQuantity(anyString(), anyInt())).thenReturn(true);
		
		item.setProduct(product);
		item.setUnit(unit);
		
		assertFalse(item.isValid());
	}
	
	@Test
	public void isValid_notEnoughAvailableQuantityForUnit() {
		String unit = Unit.CASE;
		int quantity = 5;
		
		Product product = mock(Product.class);
		when(product.hasUnit(unit)).thenReturn(true);
		when(product.hasAvailableUnitQuantity(unit, quantity)).thenReturn(false);
		
		item.setProduct(product);
		item.setUnit(unit);
		item.setQuantity(quantity);
		
		assertFalse(item.isValid());
	}
	
	@Test
	public void getUnitPrice() {
		String unit = Unit.CASE;
		
		Product product = new Product();
		product.getUnits().add(unit);
		product.getUnitPrices().add(new UnitPrice(unit, new BigDecimal("10")));
		
		item.setProduct(product);
		item.setUnit(unit);
		
		assertEquals(new BigDecimal("10"), item.getUnitPrice());
	}
	
	@Test
	public void getUnitPrice_noProduct() {
		item.setUnit(Unit.CASE);
		
		assertNull(item.getUnitPrice());
	}

	@Test
	public void getUnitPrice_productHasNoUnit() {
		String unit = Unit.CASE;
		
		Product product = new Product();
		product.getUnitPrices().add(new UnitPrice(unit, new BigDecimal("10")));
		
		item.setProduct(product);
		item.setUnit(unit);
		
		assertNull(item.getUnitPrice());
	}

	@Test
	public void getUnitPrice_productHasNoUnitPrice() {
		String unit = Unit.CASE;
		
		Product product = new Product();
		product.getUnits().add(unit);
		product.getUnitPrices().add(new UnitPrice(Unit.CARTON, new BigDecimal("10")));
	
		item.setProduct(product);
		item.setUnit(unit);
		
		assertNull(item.getUnitPrice());
	}
	
	@Test
	public void getAmount() {
		String unit = Unit.CASE;
		
		Product product = new Product();
		product.getUnits().add(unit);
		product.getUnitPrices().add(new UnitPrice(unit, new BigDecimal("10")));
		
		item.setProduct(product);
		item.setUnit(unit);
		item.setQuantity(5);
		
		assertEquals(new BigDecimal("50"), item.getAmount());
	}

	@Test
	public void getAmount_noProduct() {
		String unit = Unit.CASE;
		
		item.setUnit(unit);
		item.setQuantity(5);
		
		assertNull(item.getAmount());
	}

	@Test
	public void getAmount_noQuantity() {
		String unit = Unit.CASE;
		
		Product product = new Product();
		product.getUnits().add(unit);
		product.getUnitPrices().add(new UnitPrice(unit, new BigDecimal("10")));
		
		item.setProduct(product);
		item.setUnit(unit);
		
		assertNull(item.getAmount());
	}
	
	@Test
	public void hashCode_test() {
		item.setProduct(new Product());
		item.setUnit(Unit.CASE);
		
		assertEquals(113575, item.hashCode());
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
	public void equals_test() {
		Product product = new Product();
		product.setCode("CODE");
		
		String unit = Unit.CASE;
		
		item.setProduct(product);
		item.setUnit(unit);
		
		SalesRequisitionItem item2 = new SalesRequisitionItem();
		item2.setProduct(product);
		item2.setUnit(unit);
		
		assertTrue(item.equals(item2));
	}
	
}