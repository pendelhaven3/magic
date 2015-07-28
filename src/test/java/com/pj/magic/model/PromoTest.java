package com.pj.magic.model;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;

public class PromoTest {

	@Test
	public void getMechanicsDescription_promoType1() {
		Promo promo = new Promo();
		promo.setPromoType(PromoType.PROMO_TYPE_1);
		
		PromoType1Rule rule = new PromoType1Rule();
		rule.setTargetAmount(new BigDecimal("5000"));
		rule.setManufacturer(new Manufacturer());
		rule.getManufacturer().setName("TEST MANUFACTURER");
		rule.setQuantity(5);
		rule.setUnit(Unit.PIECES);
		rule.setProduct(new Product());
		rule.getProduct().setDescription("TEST PRODUCT DESCRIPTION");
		promo.setPromoType1Rule(rule);
		
		assertEquals("For every P5,000.00 worth of TEST MANUFACTURER products, get 5 PCS TEST PRODUCT DESCRIPTION", 
				promo.getMechanicsDescription());
	}

	@Test
	public void getMechanicsDescription_promoType2() {
		Promo promo = new Promo();
		promo.setPromoType(PromoType.PROMO_TYPE_2);
		
		PromoType2Rule rule = new PromoType2Rule();
		rule.setPromoProduct(new Product());
		rule.getPromoProduct().setDescription("PROMO PRODUCT DESCRIPTION");
		rule.setPromoUnit(Unit.CASE);
		rule.setPromoQuantity(5);
		rule.setFreeProduct(new Product());
		rule.getFreeProduct().setDescription("FREE PRODUCT DESCRIPTION");
		rule.setFreeUnit(Unit.PIECES);
		rule.setFreeQuantity(1);
		promo.setPromoType2Rules(new ArrayList<>(Arrays.asList(rule)));
		
		rule = new PromoType2Rule();
		rule.setPromoProduct(new Product());
		rule.getPromoProduct().setDescription("PROMO PRODUCT 2 DESCRIPTION");
		rule.setPromoUnit(Unit.CASE);
		rule.setPromoQuantity(5);
		rule.setFreeProduct(new Product());
		rule.getFreeProduct().setDescription("FREE PRODUCT 2 DESCRIPTION");
		rule.setFreeUnit(Unit.PIECES);
		rule.setFreeQuantity(1);
		promo.getPromoType2Rules().add(rule);
		
		assertEquals(
				"Buy 5 CSE PROMO PRODUCT DESCRIPTION, get 1 PCS FREE PRODUCT DESCRIPTION free\n"
				+ "Buy 5 CSE PROMO PRODUCT 2 DESCRIPTION, get 1 PCS FREE PRODUCT 2 DESCRIPTION free", 
				promo.getMechanicsDescription());
	}

	@Test
	public void getMechanicsDescription_promoType3() {
		Promo promo = new Promo();
		promo.setPromoType(PromoType.PROMO_TYPE_3);
		
		PromoType3Rule rule = new PromoType3Rule();
		rule.setTargetAmount(new BigDecimal("5000"));
		rule.setFreeProduct(new Product());
		rule.getFreeProduct().setDescription("FREE PRODUCT DESCRIPTION");
		rule.setFreeUnit(Unit.PIECES);
		rule.setFreeQuantity(1);
		promo.setPromoType3Rule(rule);
		
		assertEquals("Buy 5,000.00 worth of participating products, get 1 PCS FREE PRODUCT DESCRIPTION free", 
				promo.getMechanicsDescription());
	}

	@Test
	public void evaluate_salesRequisition_withReward() {
		Promo promo = new Promo();
		
		PromoType2Rule rule = mock(PromoType2Rule.class);
		when(rule.getPromoProduct()).thenReturn(new Product());
		when(rule.getPromoUnit()).thenReturn(Unit.CASE);
		promo.setPromoType2Rules(Arrays.asList(rule));
		
		SalesRequisition salesRequisition = mock(SalesRequisition.class);
		SalesRequisitionItem item = new SalesRequisitionItem();
		PromoRedemptionReward reward = new PromoRedemptionReward();
		
		when(salesRequisition.findItemByProductAndUnit(rule.getPromoProduct(), rule.getPromoUnit())).thenReturn(item);
		when(rule.evaluate(item)).thenReturn(reward);
		
		List<PromoRedemptionReward> rewards = promo.evaluateForRewards(salesRequisition);
		assertEquals(1, rewards.size());
		assertSame(reward, rewards.get(0));
	}

	@Test
	public void evaluate_salesRequisition_noPromoProduct() {
		Promo promo = new Promo();
		
		PromoType2Rule rule = mock(PromoType2Rule.class);
		when(rule.getPromoProduct()).thenReturn(new Product());
		when(rule.getPromoUnit()).thenReturn(Unit.CASE);
		promo.setPromoType2Rules(Arrays.asList(rule));
		
		SalesRequisition salesRequisition = mock(SalesRequisition.class);
		when(salesRequisition.findItemByProductAndUnit(rule.getPromoProduct(), rule.getPromoUnit())).thenReturn(null);
		
		assertTrue(promo.evaluateForRewards(salesRequisition).isEmpty());
	}

	@Test
	public void evaluate_salesRequisition_withPromoProduct_noReward() {
		Promo promo = new Promo();
		
		PromoType2Rule rule = mock(PromoType2Rule.class);
		when(rule.getPromoProduct()).thenReturn(new Product());
		when(rule.getPromoUnit()).thenReturn(Unit.CASE);
		promo.setPromoType2Rules(Arrays.asList(rule));
		
		SalesRequisition salesRequisition = mock(SalesRequisition.class);
		SalesRequisitionItem item = new SalesRequisitionItem();
		
		when(salesRequisition.findItemByProductAndUnit(rule.getPromoProduct(), rule.getPromoUnit())).thenReturn(item);
		when(rule.evaluate(item)).thenReturn(null);
		
		assertTrue(promo.evaluateForRewards(salesRequisition).isEmpty());
	}
	
	@Test
	public void evaluate_salesInvoice_withReward() {
		Promo promo = new Promo();
		
		PromoType2Rule rule = mock(PromoType2Rule.class);
		when(rule.getPromoProduct()).thenReturn(new Product());
		when(rule.getPromoUnit()).thenReturn(Unit.CASE);
		promo.setPromoType2Rules(Arrays.asList(rule));
		
		SalesInvoice salesInvoice = mock(SalesInvoice.class);
		SalesInvoiceItem item = new SalesInvoiceItem();
		PromoRedemptionReward reward = new PromoRedemptionReward();
		
		when(salesInvoice.findItemByProductAndUnit(rule.getPromoProduct(), rule.getPromoUnit())).thenReturn(item);
		when(rule.evaluate(item)).thenReturn(reward);
		
		List<PromoRedemptionReward> rewards = promo.evaluateForRewards(salesInvoice);
		assertEquals(1, rewards.size());
		assertSame(reward, rewards.get(0));
	}

	@Test
	public void evaluate_salesInvoice_noPromoProduct() {
		Promo promo = new Promo();
		
		PromoType2Rule rule = mock(PromoType2Rule.class);
		when(rule.getPromoProduct()).thenReturn(new Product());
		when(rule.getPromoUnit()).thenReturn(Unit.CASE);
		promo.setPromoType2Rules(Arrays.asList(rule));
		
		SalesInvoice salesInvoice = mock(SalesInvoice.class);
		when(salesInvoice.findItemByProductAndUnit(rule.getPromoProduct(), rule.getPromoUnit())).thenReturn(null);
		
		assertTrue(promo.evaluateForRewards(salesInvoice).isEmpty());
	}

	@Test
	public void evaluate_salesInvoice_withPromoProduct_noReward() {
		Promo promo = new Promo();
		
		PromoType2Rule rule = mock(PromoType2Rule.class);
		when(rule.getPromoProduct()).thenReturn(new Product());
		when(rule.getPromoUnit()).thenReturn(Unit.CASE);
		promo.setPromoType2Rules(Arrays.asList(rule));
		
		SalesInvoice salesInvoice = mock(SalesInvoice.class);
		SalesInvoiceItem item = new SalesInvoiceItem();
		
		when(salesInvoice.findItemByProductAndUnit(rule.getPromoProduct(), rule.getPromoUnit())).thenReturn(item);
		when(rule.evaluate(item)).thenReturn(null);
		
		assertTrue(promo.evaluateForRewards(salesInvoice).isEmpty());
	}
	
}