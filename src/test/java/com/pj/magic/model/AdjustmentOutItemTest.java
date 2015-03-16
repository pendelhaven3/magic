package com.pj.magic.model;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;

import org.junit.Test;

public class AdjustmentOutItemTest {
	
	@Test
	public void getEffectiveUnitPrice_unitPricetIsSet() {
		BigDecimal cost = new BigDecimal("10.00");
		AdjustmentOutItem item = new AdjustmentOutItem();
		item.setUnitPrice(cost);
		
		assertEquals(cost, item.getEffectiveUnitPrice());
	}
	
	@Test
	public void getEffectiveUnitPrice_unitPriceIsNotSet() {
		BigDecimal cost = new BigDecimal("10.00");
		AdjustmentOutItem item = new AdjustmentOutItem();
		Product product = mock(Product.class);
		item.setProduct(product);
		
		when(product.getUnitPrice(any(String.class))).thenReturn(cost);
		
		assertEquals(cost, item.getEffectiveUnitPrice());
	}
	
	@Test
	public void getAmount() {
		AdjustmentOutItem item = new AdjustmentOutItem();
		item.setProduct(new Product());
		item.setUnitPrice(new BigDecimal("10"));
		item.setQuantity(2);
		
		assertEquals(new BigDecimal("20"), item.getAmount());
	}

	@Test
	public void getAmount_productIsNotSet() {
		AdjustmentOutItem item = new AdjustmentOutItem();
		item.setProduct(new Product());
		
		assertNull(item.getAmount());
	}

	@Test
	public void getAmount_quantityIsNotSet() {
		AdjustmentOutItem item = new AdjustmentOutItem();
		
		assertNull(item.getAmount());
	}
	
	@Test
	public void isQuantityValid() {
		AdjustmentOutItem item = new AdjustmentOutItem();
		Product product = mock(Product.class);
		item.setProduct(product);
		item.setUnit(Unit.CASE);
		item.setQuantity(1);
		
		when(product.hasAvailableUnitQuantity(any(String.class), anyInt())).thenReturn(true);
		
		assertTrue(item.isQuantityValid());
	}
	
}