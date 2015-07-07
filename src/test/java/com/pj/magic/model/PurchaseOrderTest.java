package com.pj.magic.model;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Date;

import org.apache.commons.lang.time.DateUtils;
import org.junit.Test;

import com.pj.magic.Constants;

public class PurchaseOrderTest {

	@Test
	public void getStatus_posted() {
		PurchaseOrder purchaseOrder = new PurchaseOrder();
		purchaseOrder.setPosted(true);
		purchaseOrder.setDelivered(true);
		
		assertEquals("Posted", purchaseOrder.getStatus());
	}

	@Test
	public void getStatus_delivered() {
		PurchaseOrder purchaseOrder = new PurchaseOrder();
		purchaseOrder.setPosted(false);
		purchaseOrder.setDelivered(true);
		
		assertEquals("Delivered", purchaseOrder.getStatus());
	}

	@Test
	public void getStatus_new() {
		assertEquals("New", new PurchaseOrder().getStatus());
	}

	@Test
	public void createReceivingReceipt() {
		PurchaseOrder purchaseOrder = new PurchaseOrder();
		purchaseOrder.setPurchaseOrderNumber(1L);
		purchaseOrder.setSupplier(new Supplier());
		purchaseOrder.setPaymentTerm(new PaymentTerm());
		purchaseOrder.setReferenceNumber("REFNO");
		purchaseOrder.setRemarks("REMARKS");
		purchaseOrder.setVatInclusive(true);
		purchaseOrder.setVatRate(new BigDecimal("0.12"));
		
		PurchaseOrderItem purchaseOrderItem = new PurchaseOrderItem();
		purchaseOrderItem.setProduct(new Product());
		purchaseOrderItem.setUnit(Unit.CASE);
		purchaseOrderItem.setActualQuantity(10);
		purchaseOrderItem.setCost(new BigDecimal("100"));
		
		PurchaseOrderItem purchaseOrderItem2 = new PurchaseOrderItem();
		purchaseOrderItem2.setActualQuantity(0);

		purchaseOrder.setItems(Arrays.asList(purchaseOrderItem, purchaseOrderItem2));
		
		ReceivingReceipt receivingReceipt = purchaseOrder.createReceivingReceipt();
		assertSame(purchaseOrder.getSupplier(), receivingReceipt.getSupplier());
		assertTrue(DateUtils.isSameDay(new Date(), receivingReceipt.getReceivedDate()));
		assertSame(purchaseOrder.getPaymentTerm(), receivingReceipt.getPaymentTerm());
		assertEquals(purchaseOrder.getRemarks(), receivingReceipt.getRemarks());
		assertEquals(purchaseOrder.getPurchaseOrderNumber(), receivingReceipt.getRelatedPurchaseOrderNumber());
		assertEquals(purchaseOrder.isVatInclusive(), receivingReceipt.isVatInclusive());
		assertEquals(purchaseOrder.getVatRate(), receivingReceipt.getVatRate());
		assertEquals(1, receivingReceipt.getItems().size());
		
		ReceivingReceiptItem receivingReceiptItem = receivingReceipt.getItems().get(0);
		assertSame(purchaseOrderItem.getProduct(), receivingReceiptItem.getProduct());
		assertEquals(purchaseOrderItem.getUnit(), receivingReceiptItem.getUnit());
		assertEquals(purchaseOrderItem.getActualQuantity(), receivingReceiptItem.getQuantity());
		assertEquals(purchaseOrderItem.getCost(), receivingReceiptItem.getCost());
	}
	
	@Test
	public void getTotalQuantity() {
		PurchaseOrderItem item = new PurchaseOrderItem();
		item.setQuantity(1);
		
		PurchaseOrderItem item2 = new PurchaseOrderItem();
		item2.setQuantity(2);
		
		PurchaseOrder purchaseOrder = new PurchaseOrder();
		purchaseOrder.setItems(Arrays.asList(item, item2));
		
		assertEquals(3, purchaseOrder.getTotalQuantity());
	}

	@Test
	public void getTotalNumberOfItems() {
		PurchaseOrder purchaseOrder = new PurchaseOrder();
		purchaseOrder.setItems(Arrays.asList(new PurchaseOrderItem(), new PurchaseOrderItem()));
		
		assertEquals(2, purchaseOrder.getTotalNumberOfItems());
	}
	
	@Test
	public void getSubTotalAmount() {
		PurchaseOrderItem item = mock(PurchaseOrderItem.class);
		when(item.getAmount()).thenReturn(new BigDecimal("100"));
		
		PurchaseOrderItem item2 = mock(PurchaseOrderItem.class);
		when(item2.getAmount()).thenReturn(new BigDecimal("200"));
		
		PurchaseOrder purchaseOrder = new PurchaseOrder();
		purchaseOrder.setItems(Arrays.asList(item, item2));
		
		assertEquals(new BigDecimal("300.00"), purchaseOrder.getSubTotalAmount());
	}
	
	@Test
	public void getVatAmount_vatInclusive() {
		PurchaseOrder purchaseOrder = new PurchaseOrder();
		purchaseOrder.setVatInclusive(true);
		
		assertEquals(Constants.ZERO, purchaseOrder.getVatAmount());
	}

	@Test
	public void getVatAmount_notVatInclusive() {
		PurchaseOrderItem item = mock(PurchaseOrderItem.class);
		when(item.getAmount()).thenReturn(new BigDecimal("100"));
		
		PurchaseOrder purchaseOrder = new PurchaseOrder();
		purchaseOrder.setItems(Arrays.asList(item));
		purchaseOrder.setVatRate(new BigDecimal("0.12"));
		
		assertEquals(new BigDecimal("12.00"), purchaseOrder.getVatAmount());
	}
	
	@Test
	public void getVatMultiplier_vatInclusive() {
		PurchaseOrder purchaseOrder = new PurchaseOrder();
		purchaseOrder.setVatInclusive(true);
		
		assertEquals(Constants.ONE, purchaseOrder.getVatMultiplier());
	}
	
	@Test
	public void getVatMultiplier_notVatInclusive() {
		PurchaseOrder purchaseOrder = new PurchaseOrder();
		purchaseOrder.setVatInclusive(false);
		purchaseOrder.setVatRate(new BigDecimal("0.12"));
		
		assertEquals(new BigDecimal("1.12"), purchaseOrder.getVatMultiplier());
	}
	
}