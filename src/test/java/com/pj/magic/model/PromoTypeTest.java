package com.pj.magic.model;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

public class PromoTypeTest {

	@Test
	public void getPromoTypes() {
		List<PromoType> promoTypes = new ArrayList<>();
		promoTypes.add(PromoType.PROMO_TYPE_1);
		promoTypes.add(PromoType.PROMO_TYPE_2);
		promoTypes.add(PromoType.PROMO_TYPE_3);
		promoTypes.add(PromoType.PROMO_TYPE_4);
		promoTypes.add(PromoType.PROMO_TYPE_5);
		
		assertEquals(promoTypes, PromoType.getPromoTypes());
	}
	
	@Test
	public void getPromoType() {
		assertEquals(PromoType.PROMO_TYPE_2, PromoType.getPromoType(2L));
	}
	
	@Test
	public void isType1() {
		assertTrue(PromoType.PROMO_TYPE_1.isType1());
		assertFalse(PromoType.PROMO_TYPE_2.isType1());
	}
	
	@Test
	public void isType2() {
		assertTrue(PromoType.PROMO_TYPE_2.isType2());
		assertFalse(PromoType.PROMO_TYPE_3.isType2());
	}
	
	@Test
	public void isType3() {
		assertTrue(PromoType.PROMO_TYPE_3.isType3());
		assertFalse(PromoType.PROMO_TYPE_1.isType3());
	}
	
	@Test
	public void isType4() {
		assertTrue(PromoType.PROMO_TYPE_4.isType4());
		assertFalse(PromoType.PROMO_TYPE_1.isType4());
	}
	
	@Test
	public void promoType_toString() {
		assertEquals(PromoType.PROMO_TYPE_1.getDescription(), PromoType.PROMO_TYPE_1.toString());
	}
	
}