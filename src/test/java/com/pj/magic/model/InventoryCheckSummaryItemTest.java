package com.pj.magic.model;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;

import org.junit.Test;

public class InventoryCheckSummaryItemTest {

	@Test
	public void getQuantityDifference() {
		InventoryCheckSummaryItem item = new InventoryCheckSummaryItem();
		item.setUnit(Unit.CASE);
		item.setQuantity(2);
		
		Product product = mock(Product.class);
		when(product.getUnitQuantity(item.getUnit())).thenReturn(1);
		item.setProduct(product);
		
		assertEquals(1, item.getQuantityDifference());
	}
	
	@Test
	public void getBeginningValue() {
		InventoryCheckSummaryItem item = new InventoryCheckSummaryItem();
		item.setUnit(Unit.CASE);
		
		BigDecimal beginningValue = new BigDecimal("10");
		Product product = mock(Product.class);
		when(product.getTotalValue(item.getUnit())).thenReturn(beginningValue);
		item.setProduct(product);
		
		assertEquals(beginningValue, item.getBeginningValue());
	}
	
	@Test
	public void getActualValue() {
		InventoryCheckSummaryItem item = new InventoryCheckSummaryItem();
		item.setUnit(Unit.CASE);
		item.setQuantity(2);
		
		Product product = mock(Product.class);
		when(product.getFinalCost(item.getUnit())).thenReturn(new BigDecimal("10"));
		item.setProduct(product);
		
		assertEquals(new BigDecimal("20"), item.getActualValue());
	}
	
	@Test
	public void getCost() {
		InventoryCheckSummaryItem item = new InventoryCheckSummaryItem();
		item.setUnit(Unit.CASE);
		
		BigDecimal finalCost = new BigDecimal("10");
		Product product = mock(Product.class);
		when(product.getFinalCost(item.getUnit())).thenReturn(finalCost);
		item.setProduct(product);
		
		assertEquals(finalCost, item.getCost());
	}
	
	@Test
	public void getBeginningInventory() {
		InventoryCheckSummaryItem item = new InventoryCheckSummaryItem();
		item.setUnit(Unit.CASE);
		
		int beginningInventory = 10;
		Product product = mock(Product.class);
		when(product.getUnitQuantity(item.getUnit())).thenReturn(beginningInventory);
		item.setProduct(product);
		
		assertEquals(beginningInventory, item.getBeginningInventory());
	}
}