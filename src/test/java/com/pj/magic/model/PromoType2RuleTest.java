package com.pj.magic.model;

import static org.junit.Assert.*;

import org.junit.Test;

public class PromoType2RuleTest {

	@Test
	public void evaluate_salesRequisitionItem_hasReward() {
		PromoType2Rule rule = new PromoType2Rule();
		rule.setPromoProduct(new Product(1L));
		rule.setPromoUnit(Unit.CASE);
		rule.setPromoQuantity(5);
		rule.setFreeProduct(new Product());
		rule.setFreeUnit(Unit.CASE);
		rule.setFreeQuantity(2);
		
		SalesRequisitionItem item = new SalesRequisitionItem();
		item.setProduct(rule.getPromoProduct());
		item.setUnit(rule.getPromoUnit());
		item.setQuantity(10);
		
		PromoRedemptionReward reward = rule.evaluate(item);
		assertNotNull(reward);
		assertTrue(reward.getProduct() == rule.getFreeProduct()
				&& reward.getUnit() == rule.getFreeUnit()
				&& reward.getQuantity() == 4);
	}

	@Test
	public void evaluate_salesRequisitionItem_noReward_differentProduct() {
		PromoType2Rule rule = new PromoType2Rule();
		rule.setPromoProduct(new Product(1L));
		rule.setPromoUnit(Unit.CASE);
		rule.setPromoQuantity(5);
		rule.setFreeProduct(new Product());
		rule.setFreeUnit(Unit.CASE);
		rule.setFreeQuantity(2);
		
		SalesRequisitionItem item = new SalesRequisitionItem();
		item.setProduct(new Product(2L));
		item.setUnit(rule.getPromoUnit());
		item.setQuantity(10);
		
		assertNull(rule.evaluate(item));
	}

	@Test
	public void evaluate_salesRequisitionItem_noReward_differentUnit() {
		PromoType2Rule rule = new PromoType2Rule();
		rule.setPromoProduct(new Product(1L));
		rule.setPromoUnit(Unit.CASE);
		rule.setPromoQuantity(5);
		
		SalesRequisitionItem item = new SalesRequisitionItem();
		item.setProduct(rule.getPromoProduct());
		item.setUnit(Unit.CARTON);
		item.setQuantity(10);
		
		assertNull(rule.evaluate(item));
	}

	@Test
	public void evaluate_salesRequisitionItem_noReward_notEnoughQuantity() {
		PromoType2Rule rule = new PromoType2Rule();
		rule.setPromoProduct(new Product(1L));
		rule.setPromoUnit(Unit.CASE);
		rule.setPromoQuantity(5);
		
		SalesRequisitionItem item = new SalesRequisitionItem();
		item.setProduct(rule.getPromoProduct());
		item.setUnit(rule.getPromoUnit());
		item.setQuantity(4);
		
		assertNull(rule.evaluate(item));
	}

	@Test
	public void evaluate_salesInvoiceItem_hasReward() {
		PromoType2Rule rule = new PromoType2Rule();
		rule.setPromoProduct(new Product(1L));
		rule.setPromoUnit(Unit.CASE);
		rule.setPromoQuantity(5);
		rule.setFreeProduct(new Product());
		rule.setFreeUnit(Unit.CASE);
		rule.setFreeQuantity(2);
		
		SalesInvoiceItem item = new SalesInvoiceItem();
		item.setProduct(rule.getPromoProduct());
		item.setUnit(rule.getPromoUnit());
		item.setQuantity(10);
		
		PromoRedemptionReward reward = rule.evaluate(item);
		assertNotNull(reward);
		assertTrue(reward.getProduct() == rule.getFreeProduct()
				&& reward.getUnit() == rule.getFreeUnit()
				&& reward.getQuantity() == 4);
	}

	@Test
	public void evaluate_salesInvoiceItem_noReward_differentProduct() {
		PromoType2Rule rule = new PromoType2Rule();
		rule.setPromoProduct(new Product(1L));
		rule.setPromoUnit(Unit.CASE);
		rule.setPromoQuantity(5);
		rule.setFreeProduct(new Product());
		rule.setFreeUnit(Unit.CASE);
		rule.setFreeQuantity(2);
		
		SalesInvoiceItem item = new SalesInvoiceItem();
		item.setProduct(new Product(2L));
		item.setUnit(rule.getPromoUnit());
		item.setQuantity(10);
		
		assertNull(rule.evaluate(item));
	}

	@Test
	public void evaluate_salesInvoiceItem_noReward_differentUnit() {
		PromoType2Rule rule = new PromoType2Rule();
		rule.setPromoProduct(new Product(1L));
		rule.setPromoUnit(Unit.CASE);
		rule.setPromoQuantity(5);
		
		SalesInvoiceItem item = new SalesInvoiceItem();
		item.setProduct(rule.getPromoProduct());
		item.setUnit(Unit.CARTON);
		item.setQuantity(10);
		
		assertNull(rule.evaluate(item));
	}

	@Test
	public void evaluate_salesInvoiceItem_noReward_notEnoughQuantity() {
		PromoType2Rule rule = new PromoType2Rule();
		rule.setPromoProduct(new Product(1L));
		rule.setPromoUnit(Unit.CASE);
		rule.setPromoQuantity(5);
		
		SalesInvoiceItem item = new SalesInvoiceItem();
		item.setProduct(rule.getPromoProduct());
		item.setUnit(rule.getPromoUnit());
		item.setQuantity(4);
		
		assertNull(rule.evaluate(item));
	}
		
}