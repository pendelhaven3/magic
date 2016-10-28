package com.pj.magic.model;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Arrays;

import org.junit.Test;

public class PromoType3RuleTest {

	@Test
	public void isNew_true() {
		assertTrue(new PromoType3Rule().isNew());
	}
	
	@Test
	public void isNew_false() {
		PromoType3Rule rule = new PromoType3Rule();
		rule.setId(1L);
		
		assertFalse(rule.isNew());
	}
	
	@Test
	public void hasPromoProduct_true() {
		Product product = new Product();
		product.setCode("CODE");
		
		PromoType3RulePromoProduct rulePromoProduct = new PromoType3RulePromoProduct();
		rulePromoProduct.setProduct(product);
		
		PromoType3Rule rule = new PromoType3Rule();
		rule.setPromoProducts(Arrays.asList(rulePromoProduct));
		
		assertTrue(rule.hasPromoProduct(product));
	}

	@Test
	public void hasPromoProduct_false() {
		Product product = new Product();
		product.setCode("CODE");

		Product product2 = new Product();
		product2.setCode("CODE2");
		
		PromoType3RulePromoProduct rulePromoProduct = new PromoType3RulePromoProduct();
		rulePromoProduct.setProduct(product);
		
		PromoType3Rule rule = new PromoType3Rule();
		rule.setPromoProducts(Arrays.asList(rulePromoProduct));
		
		assertFalse(rule.hasPromoProduct(product2));
	}
	
	@Test
	public void evaluate_hasReward() {
		Product product = new Product(1L);
		
		PromoType3RulePromoProduct rulePromoProduct = new PromoType3RulePromoProduct();
		rulePromoProduct.setProduct(product);
		
		PromoType3Rule rule = new PromoType3Rule();
		rule.setPromoProducts(Arrays.asList(rulePromoProduct));
		rule.setTargetAmount(new BigDecimal("1000"));
		rule.setFreeProduct(new Product());
		rule.setFreeUnit(Unit.PIECES);
		rule.setFreeQuantity(2);
		
		SalesInvoiceItem item = mock(SalesInvoiceItem.class);
		when(item.getProduct()).thenReturn(product);
		when(item.getNetAmount()).thenReturn(new BigDecimal("5000"));

		SalesInvoice salesInvoice = new SalesInvoice();
		salesInvoice.setItems(Arrays.asList(item));
		
		PromoRedemptionReward reward = rule.evaluate(Arrays.asList(salesInvoice));
		assertTrue(reward.getProduct() == rule.getFreeProduct()
				&& reward.getUnit() == rule.getFreeUnit()
				&& reward.getQuantity() == 10);
	}
	
	@Test
	public void evaluate_noReward() {
		Product product = new Product(1L);
		
		PromoType3RulePromoProduct rulePromoProduct = new PromoType3RulePromoProduct();
		rulePromoProduct.setProduct(product);
		
		PromoType3Rule rule = new PromoType3Rule();
		rule.setPromoProducts(Arrays.asList(rulePromoProduct));
		rule.setTargetAmount(new BigDecimal("5000"));
		rule.setFreeProduct(new Product());
		rule.setFreeUnit(Unit.PIECES);
		rule.setFreeQuantity(2);
		
		SalesInvoiceItem item = mock(SalesInvoiceItem.class);
		when(item.getProduct()).thenReturn(product);
		when(item.getNetAmount()).thenReturn(new BigDecimal("1000"));

		SalesInvoice salesInvoice = new SalesInvoice();
		salesInvoice.setItems(Arrays.asList(item));
		
		assertNull(rule.evaluate(Arrays.asList(salesInvoice)));
	}
	
	@Test
	public void getQualifyingAmount() {
		Product product = new Product(1L);
		
		PromoType3RulePromoProduct rulePromoProduct = new PromoType3RulePromoProduct();
		rulePromoProduct.setProduct(product);
		
		PromoType3Rule rule = new PromoType3Rule();
		rule.setPromoProducts(Arrays.asList(rulePromoProduct));
		
		SalesInvoiceItem item = mock(SalesInvoiceItem.class);
		when(item.getProduct()).thenReturn(product);
		when(item.getNetAmount()).thenReturn(new BigDecimal("1000"));

		SalesInvoiceItem item2 = mock(SalesInvoiceItem.class);
		when(item2.getProduct()).thenReturn(product);
		when(item2.getNetAmount()).thenReturn(new BigDecimal("1000"));

		SalesInvoiceItem item3 = mock(SalesInvoiceItem.class);
		when(item3.getProduct()).thenReturn(new Product(2L));
		when(item3.getNetAmount()).thenReturn(new BigDecimal("1000"));

		SalesInvoice salesInvoice = new SalesInvoice();
		salesInvoice.setItems(Arrays.asList(item, item2, item3));
		
		assertEquals(new BigDecimal("2000.00"), rule.getQualifyingAmount(salesInvoice));
	}
	
}