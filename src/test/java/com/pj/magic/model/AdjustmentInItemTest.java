package com.pj.magic.model;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;

import org.junit.Test;

public class AdjustmentInItemTest {
	
	@Test
	public void getEffectiveCost_costIsSet() {
		BigDecimal cost = new BigDecimal("10.00");
		AdjustmentInItem item = new AdjustmentInItem();
		item.setCost(cost);
		
		assertEquals(cost, item.getEffectiveCost());
	}
	
	@Test
	public void getEffectiveCost_costIsNotSet() {
		BigDecimal cost = new BigDecimal("10.00");
		AdjustmentInItem item = new AdjustmentInItem();
		Product product = mock(Product.class);
		item.setProduct(product);
		
		when(product.getFinalCost(any(String.class))).thenReturn(cost);
		
		assertEquals(cost, item.getEffectiveCost());
	}
	
	@Test
	public void getAmount() {
		AdjustmentInItem item = new AdjustmentInItem();
		item.setProduct(new Product());
		item.setCost(new BigDecimal("10"));
		item.setQuantity(2);
		
		assertEquals(new BigDecimal("20"), item.getAmount());
	}

	@Test
	public void getAmount_productIsNotSet() {
		AdjustmentInItem item = new AdjustmentInItem();
		item.setProduct(new Product());
		
		assertNull(item.getAmount());
	}

	@Test
	public void getAmount_quantityIsNotSet() {
		AdjustmentInItem item = new AdjustmentInItem();
		
		assertNull(item.getAmount());
	}
	
}