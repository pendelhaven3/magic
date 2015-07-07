package com.pj.magic.model;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;
import org.mockito.ArgumentMatcher;

import com.pj.magic.Constants;

public class PromoRedemptionTest {

	@Test
	public void getSalesInvoices() {
		PromoRedemption redemption = new PromoRedemption();
		SalesInvoice salesInvoice = new SalesInvoice();
		
		PromoRedemptionSalesInvoice redemptionSalesInvoice = new PromoRedemptionSalesInvoice();
		redemptionSalesInvoice.setSalesInvoice(salesInvoice);
		redemption.setRedemptionSalesInvoices(Arrays.asList(redemptionSalesInvoice));
		
		List<SalesInvoice> salesInvoices = redemption.getSalesInvoices();
		assertEquals(1, salesInvoices.size());
		assertSame(salesInvoice, salesInvoices.get(0));
	}
	
	@Test
	public void getStatus_new() {
		assertEquals("New", new PromoRedemption().getStatus());
	}
	
	@Test
	public void getStatus_posted() {
		PromoRedemption redemption = new PromoRedemption();
		redemption.setPosted(true);
		
		assertEquals("Posted", redemption.getStatus());
	}
	
	@Test
	public void getTotalAmount_promoType1() {
		PromoRedemption redemption = new PromoRedemption();
		redemption.setPromo(new Promo());
		redemption.getPromo().setPromoType(PromoType.PROMO_TYPE_1);
		redemption.getPromo().setPromoType1Rule(new PromoType1Rule());
		
		Manufacturer manufacturer = new Manufacturer();
		redemption.getPromo().getPromoType1Rule().setManufacturer(manufacturer);
		
		SalesInvoice salesInvoice = mock(SalesInvoice.class);
		BigDecimal manufacturerSales = new BigDecimal("1234.50");
		when(salesInvoice.getSalesByManufacturer(manufacturer)).thenReturn(manufacturerSales);
		
		PromoRedemptionSalesInvoice redemptionSalesInvoice = new PromoRedemptionSalesInvoice();
		redemptionSalesInvoice.setSalesInvoice(salesInvoice);
		redemption.setRedemptionSalesInvoices(Arrays.asList(redemptionSalesInvoice));
		
		assertEquals(manufacturerSales, redemption.getTotalAmount());
	}

	@Test
	public void getTotalAmount_promoType2() {
		PromoRedemption redemption = new PromoRedemption();
		redemption.setPromo(new Promo());
		redemption.getPromo().setPromoType(PromoType.PROMO_TYPE_2);
		
		assertEquals(Constants.ZERO, redemption.getTotalAmount());
	}
	
	@Test
	public void getTotalAmount_promoType3() {
		PromoType3Rule rule = mock(PromoType3Rule.class);
		
		PromoRedemption redemption = new PromoRedemption();
		redemption.setPromo(new Promo());
		redemption.getPromo().setPromoType(PromoType.PROMO_TYPE_3);
		redemption.getPromo().setPromoType3Rule(rule);
		
		SalesInvoice salesInvoice = new SalesInvoice();
		
		PromoRedemptionSalesInvoice redemptionSalesInvoice = new PromoRedemptionSalesInvoice();
		redemption.setRedemptionSalesInvoices(Arrays.asList(redemptionSalesInvoice));
		redemptionSalesInvoice.setSalesInvoice(salesInvoice);
		
		BigDecimal qualifyingAmount = new BigDecimal("1234.50");
		when(rule.getQualifyingAmount(salesInvoice)).thenReturn(qualifyingAmount);
		
		assertEquals(qualifyingAmount, redemption.getTotalAmount());
	}
	
	@Test
	public void getFreeQuantity_promoType2() {
		Product product = new Product();
		String unit = Unit.CASE;
		int quantity = 10;
		
		SalesInvoice salesInvoice = mock(SalesInvoice.class);
		SalesInvoiceItem item = new SalesInvoiceItem();
		item.setQuantity(quantity);
		
		when(salesInvoice.findItemByProductAndUnit(product, unit)).thenReturn(item);
		
		PromoRedemptionSalesInvoice redemptionSalesInvoice = new PromoRedemptionSalesInvoice();
		redemptionSalesInvoice.setSalesInvoice(salesInvoice);
		
		PromoRedemption redemption = new PromoRedemption();
		redemption.setPromo(new Promo());
		redemption.getPromo().setPromoType(PromoType.PROMO_TYPE_2);
		redemption.setRedemptionSalesInvoices(Arrays.asList(redemptionSalesInvoice));
		
		PromoType2Rule rule = new PromoType2Rule();
		rule.setPromoProduct(product);
		rule.setPromoUnit(unit);
		rule.setPromoQuantity(5);
		rule.setFreeQuantity(2);
		
		assertEquals(4, redemption.getFreeQuantity(rule));
	}
	
	@Test
	public void getTotalQuantityByProductAndUnit() {
		Product product = new Product();
		String unit = Unit.CASE;
		int quantity = 5;
		
		SalesInvoice salesInvoice = mock(SalesInvoice.class);
		SalesInvoiceItem item = new SalesInvoiceItem();
		item.setQuantity(quantity);
		
		when(salesInvoice.findItemByProductAndUnit(product, unit)).thenReturn(item);
		
		PromoRedemptionSalesInvoice redemptionSalesInvoice = new PromoRedemptionSalesInvoice();
		redemptionSalesInvoice.setSalesInvoice(salesInvoice);
		
		PromoRedemption redemption = new PromoRedemption();
		redemption.setPromo(new Promo());
		redemption.getPromo().setPromoType(PromoType.PROMO_TYPE_2);
		redemption.setRedemptionSalesInvoices(new ArrayList<>(Arrays.asList(redemptionSalesInvoice)));
		
		SalesInvoice salesInvoice2 = mock(SalesInvoice.class);
		when(salesInvoice2.findItemByProductAndUnit(product, unit)).thenReturn(null);
		
		PromoRedemptionSalesInvoice redemptionSalesInvoice2 = new PromoRedemptionSalesInvoice();
		redemptionSalesInvoice2.setSalesInvoice(salesInvoice2);
		
		redemption.getRedemptionSalesInvoices().add(redemptionSalesInvoice2);
		
		assertEquals(quantity, redemption.getTotalQuantityByProductAndUnit(product, unit));
	}
	
	@Test
	public void getRewardByRule_hasMatchingReward() {
		Product product = new Product();
		String unit = Unit.CASE;
		
		PromoRedemptionReward reward = new PromoRedemptionReward();
		reward.setProduct(product);
		reward.setUnit(unit);
		
		PromoType2Rule rule = new PromoType2Rule();
		rule.setFreeProduct(product);
		rule.setFreeUnit(unit);
		
		PromoRedemption redemption = new PromoRedemption();
		redemption.setRewards(Arrays.asList(reward));
		
		assertSame(reward, redemption.getRewardByRule(rule));
	}
	
	@Test
	public void getRewardByRule_noMatchingReward() {
		Product product = new Product();
		product.setCode("PRODUCT1");
		
		Product product2 = new Product();
		product2.setCode("PRODUCT2");
		
		PromoRedemptionReward reward = new PromoRedemptionReward();
		reward.setProduct(product);
		reward.setUnit(Unit.CASE);
		
		PromoRedemptionReward reward2 = new PromoRedemptionReward();
		reward2.setProduct(product2);
		reward2.setUnit(Unit.CARTON);
		
		PromoType2Rule rule = new PromoType2Rule();
		rule.setFreeProduct(product2);
		rule.setFreeUnit(Unit.CASE);
		
		PromoRedemption redemption = new PromoRedemption();
		redemption.setRewards(Arrays.asList(reward, reward2));
		
		assertNull(redemption.getRewardByRule(rule));
	}
	
	@Test
	public void getFreeQuantity_promoType3() {
		final SalesInvoice salesInvoice = new SalesInvoice();
		
		PromoRedemptionSalesInvoice redemptionSalesInvoice = new PromoRedemptionSalesInvoice();
		redemptionSalesInvoice.setSalesInvoice(salesInvoice);
		
		PromoRedemption redemption = new PromoRedemption();
		redemption.setRedemptionSalesInvoices(Arrays.asList(redemptionSalesInvoice));
		
		PromoType3Rule rule = mock(PromoType3Rule.class);
		PromoRedemptionReward reward = new PromoRedemptionReward();
		when(rule.evaluate(argThat(new ArgumentMatcher<List<SalesInvoice>>() {

			@Override
			public boolean matches(Object argument) {
				if (argument instanceof List) {
					@SuppressWarnings("unchecked")
					List<Object> list = (List<Object>)argument;
					if (list.get(0) instanceof SalesInvoice) {
						return salesInvoice == (SalesInvoice)list.get(0);
					}
				}
				return false;
			}
			
		}))).thenReturn(reward);
	
		assertSame(reward, redemption.getFreeQuantity(rule));
	}
	
	@Test
	public void getTotalRewards() {
		PromoRedemptionReward reward = new PromoRedemptionReward();
		PromoRedemptionReward reward2 = new PromoRedemptionReward();
		PromoRedemptionReward reward3 = new PromoRedemptionReward();
		
		PromoRedemption redemption = new PromoRedemption();
		redemption.setRewards(Arrays.asList(reward, reward2, reward3));
		
		assertEquals(3, redemption.getTotalRewards());
	}

	@Test
	public void getTotalRewardQuantity() {
		PromoRedemptionReward reward = new PromoRedemptionReward();
		reward.setQuantity(1);
		
		PromoRedemptionReward reward2 = new PromoRedemptionReward();
		reward2.setQuantity(2);
		
		PromoRedemptionReward reward3 = new PromoRedemptionReward();
		reward3.setQuantity(3);
		
		PromoRedemption redemption = new PromoRedemption();
		redemption.setRewards(Arrays.asList(reward, reward2, reward3));
		
		assertEquals(6, redemption.getTotalRewardQuantity());
	}
	
}