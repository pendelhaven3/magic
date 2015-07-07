package com.pj.magic.model;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.util.Arrays;

import org.junit.Test;

public class PromoType1RuleTest {

	@Test
	public void isNew_true() {
		assertTrue(new PromoType1Rule().isNew());
	}

	@Test
	public void isNew_false() {
		PromoType1Rule rule = new PromoType1Rule();
		rule.setId(1L);
		
		assertFalse(rule.isNew());
	}
	
	@Test
	public void evaluate_hasReward() {
		PromoType1Rule rule = new PromoType1Rule();
		rule.setManufacturer(new Manufacturer());
		rule.setTargetAmount(new BigDecimal("1000"));
		rule.setProduct(new Product());
		rule.setUnit(Unit.CASE);
		rule.setQuantity(2);
		
		SalesInvoice salesInvoice = mock(SalesInvoice.class);
		when(salesInvoice.getSalesByManufacturer(rule.getManufacturer())).thenReturn(new BigDecimal("5000"));
		
		PromoRedemptionReward reward = rule.evaluate(Arrays.asList(salesInvoice));
		assertTrue(reward.getProduct() == rule.getProduct()
				&& reward.getUnit() == rule.getUnit()
				&& reward.getQuantity() == 10);
	}

	@Test
	public void evaluate_noReward() {
		PromoType1Rule rule = new PromoType1Rule();
		rule.setManufacturer(new Manufacturer());
		rule.setTargetAmount(new BigDecimal("1000"));
		rule.setProduct(new Product());
		rule.setUnit(Unit.CASE);
		rule.setQuantity(2);
		
		SalesInvoice salesInvoice = mock(SalesInvoice.class);
		when(salesInvoice.getSalesByManufacturer(rule.getManufacturer())).thenReturn(new BigDecimal("500"));
		
		assertNull(rule.evaluate(Arrays.asList(salesInvoice)));
	}
	
}