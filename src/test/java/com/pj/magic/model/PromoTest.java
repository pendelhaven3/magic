package com.pj.magic.model;

import static org.junit.Assert.*;

import java.math.BigDecimal;

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
		
		assertEquals("For every P5,000.00 worth of TEST MANUFACTURER products,"
				+ " get 5 " + Unit.PIECES + " TEST PRODUCT DESCRIPTION", 
				promo.getMechanicsDescription());
	}
	
}