package com.pj.magic.model;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;

import org.junit.Before;
import org.junit.Test;

public class PurchaseReturnItemTest {

	private PurchaseReturnItem item;
	
	@Before
	public void setUp() {
		item = new PurchaseReturnItem();
	}
	
	@Test
	public void getAmount() {
		ReceivingReceiptItem receivingReceiptItem = mock(ReceivingReceiptItem.class);
		when(receivingReceiptItem.getFinalCostWithVat()).thenReturn(new BigDecimal("1.50"));
		
		item.setReceivingReceiptItem(receivingReceiptItem);
		item.setQuantity(5);
		
		assertEquals(new BigDecimal("7.50"), item.getAmount());
	}

	@Test
	public void getAmount_noReceivingReceiptItem() {
		item.setQuantity(5);
		
		assertNull(item.getAmount());
	}

	@Test
	public void getAmount_noQuantity() {
		ReceivingReceiptItem receivingReceiptItem = mock(ReceivingReceiptItem.class);
		when(receivingReceiptItem.getFinalCostWithVat()).thenReturn(new BigDecimal("1.50"));
		
		item.setReceivingReceiptItem(receivingReceiptItem);
		
		assertNull(item.getAmount());
	}
	
	@Test
	public void getUnitCost() {
		BigDecimal unitCost = new BigDecimal("1.50");
		
		ReceivingReceiptItem receivingReceiptItem = mock(ReceivingReceiptItem.class);
		when(receivingReceiptItem.getFinalCostWithVat()).thenReturn(unitCost);
		
		item.setReceivingReceiptItem(receivingReceiptItem);
		
		assertEquals(unitCost, item.getUnitCost());
	}

	@Test
	public void getUnitCost_noReceivingReceiptItem() {
		assertNull(item.getUnitCost());
	}
	
}