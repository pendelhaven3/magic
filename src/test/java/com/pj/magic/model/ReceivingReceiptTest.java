package com.pj.magic.model;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;

import com.pj.magic.Constants;

public class ReceivingReceiptTest {

	private ReceivingReceipt receivingReceipt;
	
	@Before
	public void setUp() {
		receivingReceipt = new ReceivingReceipt();
	}
	
	@Test
	public void getStatus_posted() {
		receivingReceipt.setPosted(true);
		
		assertEquals("Posted", receivingReceipt.getStatus());
	}
	
	@Test
	public void getStatus_cancelled() {
		receivingReceipt.setCancelled(true);
		
		assertEquals("Cancelled", receivingReceipt.getStatus());
	}

	@Test
	public void getStatus_new() {
		assertEquals("New", receivingReceipt.getStatus());
	}

	@Test
	public void getTotalNumberOfItems() {
		receivingReceipt.setItems(Arrays.asList(
				new ReceivingReceiptItem(), new ReceivingReceiptItem()));
		
		assertEquals(2, receivingReceipt.getTotalNumberOfItems());
	}
	
	@Test
	public void getSubTotalAmount() {
		ReceivingReceiptItem item = mock(ReceivingReceiptItem.class);
		when(item.getAmount()).thenReturn(new BigDecimal("10"));

		ReceivingReceiptItem item2 = mock(ReceivingReceiptItem.class);
		when(item2.getAmount()).thenReturn(new BigDecimal("20"));
		
		receivingReceipt.setItems(Arrays.asList(item, item2));
		
		assertEquals(new BigDecimal("30.00"), receivingReceipt.getSubTotalAmount());
	}

	@Test
	public void getTotalDiscountedAmount() {
		ReceivingReceiptItem item = mock(ReceivingReceiptItem.class);
		when(item.getDiscountedAmount()).thenReturn(new BigDecimal("10"));

		ReceivingReceiptItem item2 = mock(ReceivingReceiptItem.class);
		when(item2.getDiscountedAmount()).thenReturn(new BigDecimal("20"));
		
		receivingReceipt.setItems(Arrays.asList(item, item2));
		
		assertEquals(new BigDecimal("30.00"), receivingReceipt.getTotalDiscountedAmount());
	}

	@Test
	public void getTotalNetAmount() {
		ReceivingReceiptItem item = mock(ReceivingReceiptItem.class);
		when(item.getNetAmount()).thenReturn(new BigDecimal("10"));

		ReceivingReceiptItem item2 = mock(ReceivingReceiptItem.class);
		when(item2.getNetAmount()).thenReturn(new BigDecimal("20"));
		
		receivingReceipt.setItems(Arrays.asList(item, item2));
		
		assertEquals(new BigDecimal("30.00"), receivingReceipt.getTotalNetAmount());
	}

	@Test
	public void getTotalQuantity() {
		ReceivingReceiptItem item = new ReceivingReceiptItem();
		item.setQuantity(1);

		ReceivingReceiptItem item2 = new ReceivingReceiptItem();
		item2.setQuantity(2);
		
		receivingReceipt.setItems(Arrays.asList(item, item2));
		
		assertEquals(3, receivingReceipt.getTotalQuantity());
	}
	
	@Test
	public void getVatAmount_vatInclusive() {
		receivingReceipt.setVatInclusive(true);
		
		assertEquals(Constants.ZERO, receivingReceipt.getVatAmount());
	}
	
	@Test
	public void getVatAmount_notVatInclusive() {
		ReceivingReceiptItem item = mock(ReceivingReceiptItem.class);
		when(item.getNetAmount()).thenReturn(new BigDecimal("10"));

		ReceivingReceiptItem item2 = mock(ReceivingReceiptItem.class);
		when(item2.getNetAmount()).thenReturn(new BigDecimal("20"));
		
		receivingReceipt.setItems(Arrays.asList(item, item2));
		receivingReceipt.setVatRate(new BigDecimal("0.12"));
		
		assertEquals(new BigDecimal("3.60"), receivingReceipt.getVatAmount());
	}

	@Test
	public void getTotalNetAmountWithVat() {
		ReceivingReceiptItem item = mock(ReceivingReceiptItem.class);
		when(item.getNetAmount()).thenReturn(new BigDecimal("10"));

		ReceivingReceiptItem item2 = mock(ReceivingReceiptItem.class);
		when(item2.getNetAmount()).thenReturn(new BigDecimal("20"));
		
		receivingReceipt.setItems(Arrays.asList(item, item2));
		receivingReceipt.setVatRate(new BigDecimal("0.12"));
		
		assertEquals(new BigDecimal("33.60"), receivingReceipt.getTotalNetAmountWithVat());
	}
	
	@Test
	public void getVatMultiplier_vatInclusive() {
		receivingReceipt.setVatInclusive(true);
		
		assertEquals(Constants.ONE, receivingReceipt.getVatMultiplier());
	}

	@Test
	public void getVatMultiplier_notVatInclusive() {
		receivingReceipt.setVatInclusive(false);
		receivingReceipt.setVatRate(new BigDecimal("0.12"));
		
		assertEquals(new BigDecimal("1.12"), receivingReceipt.getVatMultiplier());
	}
	
	@Test
	public void isNew() {
		assertTrue(receivingReceipt.isNew());
	}
	
	@Test
	public void isNew_cancelled() {
		receivingReceipt.setCancelled(true);
		
		assertFalse(receivingReceipt.isNew());
	}

	@Test
	public void isNew_posted() {
		receivingReceipt.setPosted(true);
		
		assertFalse(receivingReceipt.isNew());
	}
	
	@Test
	public void findItemByProductAndUnit() {
		Product product = new Product();
		product.setCode("PRODUCT");
		
		String unit = Unit.CASE;
		
		ReceivingReceiptItem item = new ReceivingReceiptItem();
		item.setProduct(product);
		item.setUnit(unit);
		
		receivingReceipt.getItems().add(item);
		
		assertSame(item, receivingReceipt.findItemByProductAndUnit(product, unit));
	}

	@Test
	public void findItemByProductAndUnit_productNotMatch() {
		Product product = new Product();
		product.setCode("PRODUCT");
		
		String unit = Unit.CASE;
		
		ReceivingReceiptItem item = new ReceivingReceiptItem();
		item.setProduct(product);
		item.setUnit(unit);
		
		receivingReceipt.getItems().add(item);
		
		Product searchProduct = new Product();
		searchProduct.setCode("PRODUCT2");
		
		assertNull(receivingReceipt.findItemByProductAndUnit(searchProduct, unit));
	}

	@Test
	public void findItemByProductAndUnit_unitNotMatch() {
		Product product = new Product();
		product.setCode("PRODUCT");
		
		String unit = Unit.CASE;
		
		ReceivingReceiptItem item = new ReceivingReceiptItem();
		item.setProduct(product);
		item.setUnit(unit);
		
		receivingReceipt.getItems().add(item);
		
		assertNull(receivingReceipt.findItemByProductAndUnit(product, Unit.CARTON));
	}
	
	@Test
	public void hasProduct() {
		Product product = new Product();
		product.setCode("PRODUCT");
		
		ReceivingReceiptItem item = new ReceivingReceiptItem();
		item.setProduct(product);
		
		receivingReceipt.getItems().add(item);
		
		assertTrue(receivingReceipt.hasProduct(product));
	}

	@Test
	public void hasProduct_false() {
		Product product = new Product();
		product.setCode("PRODUCT");
		
		ReceivingReceiptItem item = new ReceivingReceiptItem();
		item.setProduct(product);
		
		receivingReceipt.getItems().add(item);
		
		Product searchProduct = new Product();
		searchProduct.setCode("PRODUCT2");
		
		assertFalse(receivingReceipt.hasProduct(searchProduct));
	}
	
	@Test
	public void hasProductAndUnit() {
		Product product = new Product();
		product.setCode("PRODUCT");
		
		String unit = Unit.CASE;
		
		ReceivingReceiptItem item = new ReceivingReceiptItem();
		item.setProduct(product);
		item.setUnit(unit);
		
		receivingReceipt.getItems().add(item);
		
		assertTrue(receivingReceipt.hasProductAndUnit(product, unit));
	}

	@Test
	public void hasProductAndUnit_productNotMatch() {
		Product product = new Product();
		product.setCode("PRODUCT");
		
		String unit = Unit.CASE;
		
		ReceivingReceiptItem item = new ReceivingReceiptItem();
		item.setProduct(product);
		item.setUnit(unit);
		
		receivingReceipt.getItems().add(item);
		
		Product searchProduct = new Product();
		searchProduct.setCode("PRODUCT2");
		
		assertFalse(receivingReceipt.hasProductAndUnit(searchProduct, unit));
	}

	@Test
	public void hasProductAndUnit_unitNotMatch() {
		Product product = new Product();
		product.setCode("PRODUCT");
		
		String unit = Unit.CASE;
		
		ReceivingReceiptItem item = new ReceivingReceiptItem();
		item.setProduct(product);
		item.setUnit(unit);
		
		receivingReceipt.getItems().add(item);
		
		assertFalse(receivingReceipt.hasProductAndUnit(product, Unit.CARTON));
	}
	
}