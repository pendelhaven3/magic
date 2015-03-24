package com.pj.magic.model;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;

import org.junit.Test;

public class NoMoreStockAdjustmentItemTest {

	@Test
	public void getAmount_salesInvoiceItemIsNull() {
		assertNull(new NoMoreStockAdjustmentItem().getAmount());
	}
	
	@Test
	public void getAmount_quantityIsNull() {
		NoMoreStockAdjustmentItem item = new NoMoreStockAdjustmentItem();
		item.setSalesInvoiceItem(new SalesInvoiceItem());
		
		assertNull(item.getAmount());
	}
	
	@Test
	public void getAmount() {
		NoMoreStockAdjustmentItem item = new NoMoreStockAdjustmentItem();
		item.setQuantity(2);
		
		SalesInvoiceItem salesInvoiceItem = mock(SalesInvoiceItem.class);
		when(salesInvoiceItem.getDiscountedUnitPrice()).thenReturn(new BigDecimal("10"));
		item.setSalesInvoiceItem(salesInvoiceItem);
		
		assertEquals(new BigDecimal("20"), item.getAmount());
	}
	
	@Test
	public void getUnitPrice_salesInvoiceItemIsNull() {
		assertNull(new NoMoreStockAdjustmentItem().getUnitPrice());
	}
	
	@Test
	public void getUnitPrice_success() {
		NoMoreStockAdjustmentItem item = new NoMoreStockAdjustmentItem();
		
		BigDecimal unitPrice = new BigDecimal("10");
		SalesInvoiceItem salesInvoiceItem = mock(SalesInvoiceItem.class);
		when(salesInvoiceItem.getDiscountedUnitPrice()).thenReturn(unitPrice);
		item.setSalesInvoiceItem(salesInvoiceItem);
		
		assertEquals(unitPrice, item.getUnitPrice());
	}
	
	@Test
	public void getCost_salesInvoiceItemIsNull() {
		assertNull(new NoMoreStockAdjustmentItem().getCost());
	}
	
	@Test
	public void getCost() {
		NoMoreStockAdjustmentItem item = new NoMoreStockAdjustmentItem();
		
		BigDecimal cost = new BigDecimal("10");
		SalesInvoiceItem salesInvoiceItem = mock(SalesInvoiceItem.class);
		when(salesInvoiceItem.getCost()).thenReturn(cost);
		item.setSalesInvoiceItem(salesInvoiceItem);
		
		assertEquals(cost, item.getCost());
	}
	
	@Test
	public void getTotalCost() {
		NoMoreStockAdjustmentItem item = new NoMoreStockAdjustmentItem();
		item.setQuantity(2);
		
		SalesInvoiceItem salesInvoiceItem = mock(SalesInvoiceItem.class);
		when(salesInvoiceItem.getCost()).thenReturn(new BigDecimal("10"));
		item.setSalesInvoiceItem(salesInvoiceItem);
		
		assertEquals(new BigDecimal("20"), item.getTotalCost());
	}
	
}