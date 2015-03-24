package com.pj.magic.model;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.util.Arrays;

import org.junit.Test;

public class NoMoreStockAdjustmentTest {

	@Test
	public void getTotalAmount() {
		NoMoreStockAdjustment noMoreStockAdjustment = new NoMoreStockAdjustment();
		noMoreStockAdjustment.setItems(Arrays.asList(
				createItem("10", 1),
				createItem("20", 2)));
		
		assertEquals(new BigDecimal("50.00"), noMoreStockAdjustment.getTotalAmount());
	}
	
	private NoMoreStockAdjustmentItem createItem(String unitPrice, int quantity) {
		NoMoreStockAdjustmentItem item = new NoMoreStockAdjustmentItem();
		item.setQuantity(quantity);
		
		SalesInvoiceItem salesInvoiceItem = mock(SalesInvoiceItem.class);
		when(salesInvoiceItem.getDiscountedUnitPrice()).thenReturn(new BigDecimal(unitPrice));
		item.setSalesInvoiceItem(salesInvoiceItem);
		
		return item;
	}

	@Test
	public void getTotalItems() {
		NoMoreStockAdjustment noMoreStockAdjustment = new NoMoreStockAdjustment();
		noMoreStockAdjustment.setItems(Arrays.asList(
				new NoMoreStockAdjustmentItem(),
				new NoMoreStockAdjustmentItem()));
		
		assertEquals(2, noMoreStockAdjustment.getTotalItems());
	}
	
	@Test
	public void hasItems_true() {
		NoMoreStockAdjustment noMoreStockAdjustment = new NoMoreStockAdjustment();
		noMoreStockAdjustment.setItems(Arrays.asList(
				new NoMoreStockAdjustmentItem(),
				new NoMoreStockAdjustmentItem()));
		
		assertTrue(noMoreStockAdjustment.hasItems());
	}
	
	@Test
	public void hasItems_false() {
		assertFalse(new NoMoreStockAdjustment().hasItems());
	}
	
	@Test
	public void getTotalCost() {
		NoMoreStockAdjustment noMoreStockAdjustment = new NoMoreStockAdjustment();
		noMoreStockAdjustment.setItems(Arrays.asList(
				createItemWithTotalCost("10"),
				createItemWithTotalCost("20")));
		
		assertEquals(new BigDecimal("30.00"), noMoreStockAdjustment.getTotalCost());
	}

	private NoMoreStockAdjustmentItem createItemWithTotalCost(String totalCost) {
		NoMoreStockAdjustmentItem item = mock(NoMoreStockAdjustmentItem.class);
		when(item.getTotalCost()).thenReturn(new BigDecimal(totalCost));
		return item;
	}
	
}