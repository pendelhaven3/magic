package com.pj.magic.model;

import static org.junit.Assert.*;

import java.math.BigDecimal;

import org.junit.Test;

public class ProductPriceHistoryTest {

	@Test
	public void getUnitPrice() {
		ProductPriceHistory history = new ProductPriceHistory();
		history.getUnitPrices().add(new UnitPrice(Unit.CASE, new BigDecimal("1000")));
		history.getUnitPrices().add(new UnitPrice(Unit.CARTON, new BigDecimal("500")));
		
		assertEquals(new BigDecimal("500"), history.getUnitPrice(Unit.CARTON));
	}

	@Test
	public void getUnitPrice_productHasNoUnit() {
		assertNull(new ProductPriceHistory().getUnitPrice(Unit.CASE));
	}
	
	@Test
	public void getActiveUnitPrice() {
		ProductPriceHistory history = new ProductPriceHistory();
		history.getUnitPrices().add(new UnitPrice(Unit.CASE, new BigDecimal("1000")));
		history.getActiveUnits().add(Unit.CASE);
		
		assertEquals(new BigDecimal("1000"), history.getActiveUnitPrice(Unit.CASE));
	}

	@Test
	public void getActiveUnitPrice_unitIsNotActive() {
		ProductPriceHistory history = new ProductPriceHistory();
		history.getUnitPrices().add(new UnitPrice(Unit.CASE, new BigDecimal("1000")));
		
		assertNull(history.getActiveUnitPrice(Unit.CASE));
	}
	
}