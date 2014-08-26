package com.pj.magic.model;

import static org.junit.Assert.*;
import org.junit.Test;

public class StockQuantityConversionItemTest {

	@Test
	public void getConvertedQuantity() {
		StockQuantityConversionItem item = new StockQuantityConversionItem();
		
		Product product = new Product();
		product.getUnitConversions().add(new UnitConversion(Unit.PIECES, 1));
		product.getUnitConversions().add(new UnitConversion(Unit.CARTON, 6));
		product.getUnitConversions().add(new UnitConversion(Unit.CASE, 36));
		
		item.setProduct(product);
		item.setFromUnit(Unit.CASE);
		item.setQuantity(2);
		item.setToUnit(Unit.CARTON);
		
		assertEquals(12, item.getConvertedQuantity());
	}
	
}
