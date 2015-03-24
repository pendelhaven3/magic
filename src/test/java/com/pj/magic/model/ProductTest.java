package com.pj.magic.model;

import static org.junit.Assert.*;

import java.math.BigDecimal;
import java.util.Arrays;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.junit.Test;

import com.pj.magic.Constants;

public class ProductTest {

	@Test
	public void getUnitQuantity() {
		Product product = new Product();
		product.getUnitQuantities().add(new UnitQuantity(Unit.CASE, 1));
		product.getUnitQuantities().add(new UnitQuantity(Unit.CARTON, 2));
		
		assertEquals(2, product.getUnitQuantity(Unit.CARTON));
	}
	
	@Test
	public void getUnitQuantity_productHasNoSuchUnit() {
		Product product = new Product();
		product.getUnitQuantities().add(new UnitQuantity(Unit.CASE, 1));
		product.getUnitQuantities().add(new UnitQuantity(Unit.CARTON, 2));
		
		assertEquals(0, product.getUnitQuantity(Unit.DOZEN));
	}
	
	@Test
	public void getUnitPrice() {
		Product product = new Product();
		product.getUnitPrices().add(new UnitPrice(Unit.CASE, new BigDecimal("10")));
		product.getUnitPrices().add(new UnitPrice(Unit.CARTON, new BigDecimal("2")));
		
		assertEquals(new BigDecimal("2"), product.getUnitPrice(Unit.CARTON));
	}
	
	@Test
	public void getUnitPrice_productHasUnitButNoUnitPrice() {
		Product product = new Product();
		product.getUnits().add(Unit.CASE);
		
		assertEquals(Constants.ZERO, product.getUnitPrice(Unit.CASE));
		assertNotNull(CollectionUtils.find(product.getUnitPrices(), new Predicate() {
			
			@Override
			public boolean evaluate(Object object) {
				UnitPrice unitPrice = (UnitPrice)object;
				return Unit.CASE.equals(unitPrice.getUnit()) && Constants.ZERO.equals(unitPrice.getPrice());
			}
		}));
	}
	
	@Test
	public void getUnitPrice_productHasNoUnit() {
		Product product = new Product();
		
		assertEquals(Constants.ZERO, product.getUnitPrice(Unit.CASE));
		assertTrue(product.getUnitPrices().isEmpty());
	}
	
	@Test
	public void hasAvailableUnitQuantity() {
		Product product = new Product();
		product.getUnitQuantities().add(new UnitQuantity(Unit.CASE, 1));
		
		assertTrue(product.hasAvailableUnitQuantity(Unit.CASE, 1));
		assertFalse(product.hasAvailableUnitQuantity(Unit.CASE, 2));
	}
	
	@Test
	public void subtractUnitQuantity() {
		Product product = new Product();
		product.getUnitQuantities().add(new UnitQuantity(Unit.CASE, 10));
		product.getUnitQuantities().add(new UnitQuantity(Unit.CARTON, 5));
		
		product.subtractUnitQuantity(Unit.CASE, 1);
		
		assertEquals(9, product.getUnitQuantity(Unit.CASE));
	}
	
	@Test
	public void addUnitQuantity() {
		Product product = new Product();
		product.getUnitQuantities().add(new UnitQuantity(Unit.CASE, 10));
		product.getUnitQuantities().add(new UnitQuantity(Unit.CARTON, 5));
		
		product.addUnitQuantity(Unit.CARTON, 10);
		
		assertEquals(15, product.getUnitQuantity(Unit.CARTON));
	}
	
	@Test
	public void addUnitQuantity_productHasNoUnitQuantity() {
		Product product = new Product();
		
		product.addUnitQuantity(Unit.CARTON, 10);
		
		assertEquals(10, product.getUnitQuantity(Unit.CARTON));
		assertNotNull(CollectionUtils.find(product.getUnitQuantities(), new Predicate() {
			
			@Override
			public boolean evaluate(Object object) {
				UnitQuantity unitQuantity = (UnitQuantity)object;
				return Unit.CARTON.equals(unitQuantity.getUnit()) && unitQuantity.getQuantity() == 10;
			}
		}));
	}
	
	@Test
	public void setUnitPrice() {
		Product product = new Product();
		product.getUnitPrices().add(new UnitPrice(Unit.CASE, new BigDecimal("10")));
		product.getUnitPrices().add(new UnitPrice(Unit.CARTON, new BigDecimal("2")));
		
		product.setUnitPrice(Unit.CARTON, new BigDecimal("3"));
		
		assertEquals(new BigDecimal("3"), product.getUnitPrice(Unit.CARTON));
	}

	@Test
	public void setUnitPrice_productHasNoUnitPrice() {
		Product product = new Product();
		
		product.setUnitPrice(Unit.CARTON, new BigDecimal("3"));
		
		assertEquals(new BigDecimal("3"), product.getUnitPrice(Unit.CARTON));
		assertNotNull(CollectionUtils.find(product.getUnitPrices(), new Predicate() {
			
			@Override
			public boolean evaluate(Object object) {
				UnitPrice unitPrice = (UnitPrice)object;
				return Unit.CARTON.equals(unitPrice.getUnit())
						&& new BigDecimal("3").equals(unitPrice.getPrice());
			}
		}));
	}
	
	@Test
	public void getUnitConversion() {
		Product product = new Product();
		product.getUnitConversions().add(new UnitConversion(Unit.CASE, 10));
		product.getUnitConversions().add(new UnitConversion(Unit.CARTON, 2));
		
		assertEquals(2, product.getUnitConversion(Unit.CARTON));
	}

	@Test
	public void getUnitConversion_productHasNoUnitConversion() {
		assertEquals(0, new Product().getUnitConversion(Unit.CARTON));
	}
	
	@Test
	public void setUnitConversion() {
		Product product = new Product();
		product.getUnitConversions().add(new UnitConversion(Unit.CARTON, 5));
		product.getUnitConversions().add(new UnitConversion(Unit.CASE, 10));
		
		product.setUnitConversion(Unit.CASE, 15);
		
		assertEquals(15, product.getUnitConversion(Unit.CASE));
	}

	@Test
	public void setUnitConversion_productHasNoUnitConversion() {
		Product product = new Product();
		
		product.setUnitConversion(Unit.CASE, 15);
		
		assertEquals(15, product.getUnitConversion(Unit.CASE));
		assertNotNull(CollectionUtils.find(product.getUnitConversions(), new Predicate() {
			
			@Override
			public boolean evaluate(Object object) {
				UnitConversion unitConversion = (UnitConversion)object;
				return Unit.CASE.equals(unitConversion.getUnit())
						&& unitConversion.getQuantity() == 15;
			}
		}));
	}
	
	@Test
	public void setGrossCost() {
		Product product = new Product();
		product.getUnitCosts().add(new UnitCost(Unit.CARTON, new BigDecimal("1"), Constants.ZERO));
		product.getUnitCosts().add(new UnitCost(Unit.CASE, new BigDecimal("5"), Constants.ZERO));
		
		product.setGrossCost(Unit.CASE, new BigDecimal("10"));
		
		assertEquals(new BigDecimal("10"), product.getGrossCost(Unit.CASE));
	}

	@Test
	public void setGrossCost_productHasNoGrossCost() {
		Product product = new Product();
		
		product.setGrossCost(Unit.CASE, new BigDecimal("10"));
		
		assertEquals(new BigDecimal("10"), product.getGrossCost(Unit.CASE));
		assertNotNull(CollectionUtils.find(product.getUnitCosts(), new Predicate() {
			
			@Override
			public boolean evaluate(Object object) {
				UnitCost unitCost = (UnitCost)object;
				return Unit.CASE.equals(unitCost.getUnit())
						&& new BigDecimal("10").equals(unitCost.getGrossCost());
			}
		}));
	}
	
	@Test
	public void getGrossCost() {
		Product product = new Product();
		product.getUnitCosts().add(new UnitCost(Unit.CARTON, new BigDecimal("1"), Constants.ZERO));
		product.getUnitCosts().add(new UnitCost(Unit.CASE, new BigDecimal("5"), Constants.ZERO));
		
		assertEquals(new BigDecimal("5"), product.getGrossCost(Unit.CASE));
	}

	@Test
	public void getGrossCost_productHasUnitButNoGrossCost() {
		Product product = new Product();
		product.getUnits().add(Unit.CASE);
		
		assertEquals(Constants.ZERO, product.getGrossCost(Unit.CASE));
		assertNotNull(CollectionUtils.find(product.getUnitCosts(), new Predicate() {
			
			@Override
			public boolean evaluate(Object object) {
				UnitCost unitCost = (UnitCost)object;
				return Unit.CASE.equals(unitCost.getUnit()) && Constants.ZERO.equals(unitCost.getGrossCost());
			}
		}));
	}
	
	@Test
	public void getGrossCost_productHasNoUnit() {
		Product product = new Product();
		
		assertEquals(Constants.ZERO, product.getGrossCost(Unit.CASE));
		assertTrue(product.getUnitCosts().isEmpty());
	}
	
	@Test
	public void setFinalCost() {
		Product product = new Product();
		product.getUnitCosts().add(new UnitCost(Unit.CARTON, Constants.ZERO, new BigDecimal("1")));
		product.getUnitCosts().add(new UnitCost(Unit.CASE, Constants.ZERO, new BigDecimal("5")));
		
		product.setFinalCost(Unit.CASE, new BigDecimal("10"));
		
		assertEquals(new BigDecimal("10"), product.getFinalCost(Unit.CASE));
	}
	
	@Test
	public void setFinalCost_productHasNoFinalCost() {
		Product product = new Product();
		
		product.setFinalCost(Unit.CASE, new BigDecimal("10"));
		
		assertEquals(new BigDecimal("10"), product.getFinalCost(Unit.CASE));
		assertNotNull(CollectionUtils.find(product.getUnitCosts(), new Predicate() {
			
			@Override
			public boolean evaluate(Object object) {
				UnitCost unitCost = (UnitCost)object;
				return Unit.CASE.equals(unitCost.getUnit())
						&& new BigDecimal("10").equals(unitCost.getFinalCost());
			}
		}));
	}
	
	@Test
	public void getFinalCost() {
		Product product = new Product();
		product.getUnitCosts().add(new UnitCost(Unit.CARTON, Constants.ZERO, new BigDecimal("1")));
		product.getUnitCosts().add(new UnitCost(Unit.CASE, Constants.ZERO, new BigDecimal("5")));
		
		assertEquals(new BigDecimal("5"), product.getFinalCost(Unit.CASE));
	}

	@Test
	public void getFinalCost_productHasUnitButNoFinalCost() {
		Product product = new Product();
		product.getUnits().add(Unit.CASE);
		
		assertEquals(Constants.ZERO, product.getFinalCost(Unit.CASE));
		assertNotNull(CollectionUtils.find(product.getUnitCosts(), new Predicate() {
			
			@Override
			public boolean evaluate(Object object) {
				UnitCost unitCost = (UnitCost)object;
				return Unit.CASE.equals(unitCost.getUnit()) && Constants.ZERO.equals(unitCost.getFinalCost());
			}
		}));
	}

	@Test
	public void getFinalCost_productHasNoUnit() {
		Product product = new Product();
		
		assertEquals(Constants.ZERO, product.getFinalCost(Unit.CASE));
		assertTrue(product.getUnitCosts().isEmpty());
	}
	
	@Test
	public void isMaxUnit() {
		Product product = new Product();
		product.setUnits(Arrays.asList(Unit.CASE, Unit.CARTON, Unit.TIE));
		
		assertTrue(product.isMaxUnit(Unit.CASE));
		assertFalse(product.isMaxUnit(Unit.CARTON));
	}
	
	@Test
	public void getSuggestedOrder() {
		Product product = new Product();
		product.getUnitQuantities().add(new UnitQuantity(Unit.CASE, 5));
		product.setMaximumStockLevel(10);
		
		assertEquals(5, product.getSuggestedOrder(Unit.CASE));
	}
	
	@Test
	public void getSuggestedOrder_currentQuantityMoreThanMaximumStockLevel() {
		Product product = new Product();
		product.getUnitQuantities().add(new UnitQuantity(Unit.CASE, 15));
		product.setMaximumStockLevel(10);
		
		assertEquals(0, product.getSuggestedOrder(Unit.CASE));
	}
	
	@Test
	public void getPercentProfit() {
		Product product = new Product();
		product.getUnitPrices().add(new UnitPrice(Unit.CASE, new BigDecimal("2100")));
		product.getUnitCosts().add(new UnitCost(Unit.CASE, new BigDecimal("2000"), new BigDecimal("2000")));
		
		assertEquals(new BigDecimal("4.76"), product.getPercentProfit(Unit.CASE));
	}

	@Test
	public void getPercentProfit_noSellingPrice() {
		Product product = new Product();
		product.getUnitPrices().add(new UnitPrice(Unit.CASE, Constants.ZERO));
		
		assertEquals(Constants.ZERO, product.getPercentProfit(Unit.CASE));
	}

	@Test
	public void getPercentProfit_noFinalCost() {
		Product product = new Product();
		product.getUnitPrices().add(new UnitPrice(Unit.CASE, new BigDecimal("2100")));
		product.getUnitCosts().add(new UnitCost(Unit.CASE, Constants.ZERO, Constants.ZERO));
		
		assertEquals(Constants.ONE_HUNDRED, product.getPercentProfit(Unit.CASE));
	}
	
	@Test
	public void getFlatProfit() {
		Product product = new Product();
		product.getUnitPrices().add(new UnitPrice(Unit.CASE, new BigDecimal("120")));
		product.getUnitCosts().add(new UnitCost(Unit.CASE, new BigDecimal("100"), new BigDecimal("100")));
		
		assertEquals(new BigDecimal("20"), product.getFlatProfit(Unit.CASE));
	}
	
	@Test
	public void setPercentProfit() {
		Product product = new Product();
		product.setFinalCost(Unit.CASE, new BigDecimal("2000"));
		
		product.setPercentProfit(Unit.CASE, new BigDecimal("3"));
		
		assertEquals(new BigDecimal("2061.90"), product.getUnitPrice(Unit.CASE));
	}
	
	@Test
	public void setFlatProfit() {
		Product product = new Product();
		product.setFinalCost(Unit.CASE, new BigDecimal("2000"));
		
		product.setFlatProfit(Unit.CASE, new BigDecimal("100"));
		
		assertEquals(new BigDecimal("2100"), product.getUnitPrice(Unit.CASE));
	}
	
	@Test
	public void autoCalculatePricesOfSmallerUnits() {
		Product product = new Product();
		product.getUnits().addAll(Arrays.asList(Unit.CASE, Unit.CARTON));
		product.setUnitPrice(Unit.CASE, new BigDecimal("1000"));
		product.setUnitConversion(Unit.CASE, 10);
		product.setUnitConversion(Unit.CARTON, 1);
		
		product.autoCalculatePricesOfSmallerUnits();

		assertEquals(new BigDecimal("100.00"), product.getUnitPrice(Unit.CARTON));
	}
	
	@Test
	public void getMaxUnit() {
		Product product = new Product();
		product.getUnits().addAll(Arrays.asList(Unit.TIE, Unit.CARTON));
		
		assertEquals(Unit.TIE, product.getMaxUnit());
	}
	
	@Test
	public void autoCalculateCostsOfSmallerUnits() {
		Product product = new Product();
		product.getUnits().addAll(Arrays.asList(Unit.TIE, Unit.CARTON, Unit.PIECES, Unit.CASE));
		product.getUnitCosts().add(new UnitCost(Unit.CASE, new BigDecimal("1000"), new BigDecimal("900")));
		product.setUnitConversion(Unit.CASE, 20);
		product.setUnitConversion(Unit.TIE, 10);
		product.setUnitConversion(Unit.CARTON, 5);
		product.setUnitConversion(Unit.PIECES, 1);
		
		product.autoCalculateCostsOfSmallerUnits();

		assertEquals(new BigDecimal("500.00"), product.getGrossCost(Unit.TIE));
		assertEquals(new BigDecimal("450.00"), product.getFinalCost(Unit.TIE));
		assertEquals(new BigDecimal("250.00"), product.getGrossCost(Unit.CARTON));
		assertEquals(new BigDecimal("225.00"), product.getFinalCost(Unit.CARTON));
		assertEquals(new BigDecimal("50.00"), product.getGrossCost(Unit.PIECES));
		assertEquals(new BigDecimal("45.00"), product.getFinalCost(Unit.PIECES));
	}

	@Test
	public void autoCalculateCostsOfSmallerUnits_referenceUnitIsNotMaxUnit() {
		Product product = new Product();
		product.getUnits().addAll(Arrays.asList(Unit.TIE, Unit.CARTON, Unit.PIECES, Unit.CASE));
		product.getUnitCosts().add(new UnitCost(Unit.CARTON, new BigDecimal("250"), new BigDecimal("225")));
		product.setUnitConversion(Unit.CARTON, 5);
		product.setUnitConversion(Unit.PIECES, 1);
		
		BigDecimal originalTieGrossCost = new BigDecimal("450");
		BigDecimal originalTieFinalCost = new BigDecimal("400");
		product.getUnitCosts().add(new UnitCost(Unit.TIE, originalTieGrossCost, originalTieFinalCost));
		
		product.autoCalculateCostsOfSmallerUnits(Unit.CARTON);

		assertEquals(new BigDecimal("50.00"), product.getGrossCost(Unit.PIECES));
		assertEquals(new BigDecimal("45.00"), product.getFinalCost(Unit.PIECES));
		assertEquals(originalTieGrossCost, product.getGrossCost(Unit.TIE));
		assertEquals(originalTieFinalCost, product.getFinalCost(Unit.TIE));
	}
	
	@Test
	public void hasNoSellingPrice() {
		Product product = new Product();
		product.getUnits().addAll(Arrays.asList(Unit.CASE, Unit.CARTON));
		product.setUnitPrice(Unit.CASE, new BigDecimal("1000"));
		
		assertFalse(product.hasNoSellingPrice(Unit.CASE));
		assertTrue(product.hasNoSellingPrice(Unit.CARTON));
	}

	@Test
	public void hasSellingPriceLessThanCost() {
		Product product = new Product();
		product.getUnits().addAll(Arrays.asList(Unit.CASE, Unit.CARTON));
		product.setUnitPrice(Unit.CASE, new BigDecimal("900"));
		product.setFinalCost(Unit.CASE, new BigDecimal("1000"));
		product.setUnitPrice(Unit.CARTON, new BigDecimal("100"));
		product.setFinalCost(Unit.CARTON, new BigDecimal("90"));
		
		assertTrue(product.hasSellingPriceLessThanCost(Unit.CASE));
		assertFalse(product.hasSellingPriceLessThanCost(Unit.CARTON));
	}

	@Test
	public void hasActiveUnit() {
		Product product = new Product();
		product.getActiveUnits().add(Unit.CASE);
		
		assertTrue(product.hasActiveUnit(Unit.CASE));
		assertFalse(product.hasActiveUnit(Unit.CARTON));
	}
	
	@Test
	public void getActiveUnitPrice() {
		Product product = new Product();
		product.getActiveUnits().add(Unit.CASE);
		product.setUnitPrice(Unit.CASE, new BigDecimal("1000"));
		
		assertEquals(new BigDecimal("1000"), product.getActiveUnitPrice(Unit.CASE));
	}

	@Test
	public void getActiveUnitPrice_unitIsNotActive() {
		Product product = new Product();
		product.setUnitPrice(Unit.CASE, new BigDecimal("1000"));
		
		assertEquals(Constants.ZERO, product.getActiveUnitPrice(Unit.CASE));
	}
	
	@Test
	public void getTotalValue() {
		Product product = new Product();
		product.getUnitCosts().add(new UnitCost(Unit.CASE, new BigDecimal("900"), new BigDecimal("900")));
		product.getUnitQuantities().add(new UnitQuantity(Unit.CASE, 2));
		
		assertEquals(new BigDecimal("1800"), product.getTotalValue(Unit.CASE));
	}
	
}