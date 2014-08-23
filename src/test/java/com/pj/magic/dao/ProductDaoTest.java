package com.pj.magic.dao;

import static org.junit.Assert.*;

import java.math.BigDecimal;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;

import com.pj.magic.model.Product;
import com.pj.magic.model.Unit;
import com.pj.magic.model.UnitConversion;
import com.pj.magic.model.UnitPrice;
import com.pj.magic.model.UnitQuantity;

@ContextConfiguration(locations={"classpath:applicationContext.xml"})
public class ProductDaoTest extends AbstractTransactionalJUnit4SpringContextTests {
	
	@Autowired private ProductDao productDao;
	@Autowired private ProductPriceDao productPriceDao;
	
	// TODO: Add manufacturer, supplier, category to tests
	
	@Test
	public void insertWithMinimumFieldsFilled() {
		String code = "CODE";
		String description = "DESCRIPTION";
		
		Product product = new Product();
		product.setCode(code);
		product.setDescription(description);
		productDao.save(product);
		productPriceDao.save(product);
		
		Product fromDb = productDao.get(product.getId());
		assertEquals(code, fromDb.getCode());
		assertEquals(description, fromDb.getDescription());
	}
	
	@Test
	public void insertWithAllFieldsField() {
		String code = "CODE";
		String description = "DESCRIPTION";
		boolean active = true;
		int caseQuantity = 10;
		int tieQuantity = 15;
		int cartonQuantity = 20;
		int dozenQuantity = 25;
		int piecesQuantity = 30;
		BigDecimal caseUnitPrice = new BigDecimal("10.00");
		BigDecimal tieUnitPrice = new BigDecimal("15.00");
		BigDecimal cartonUnitPrice = new BigDecimal("20.00");
		BigDecimal dozenUnitPrice = new BigDecimal("25.00");
		BigDecimal piecesUnitPrice = new BigDecimal("30.00");
		int caseUnitConversion = 240;
		int tieUnitConversion = 36;
		int cartonUnitConversion = 60;
		int dozenUnitConversion = 12;
		int piecesUnitConversion = 1;
		
		Product product = new Product();
		product.setCode(code);
		product.setDescription(description);
		product.setActive(active);
		
		product.getUnits().add(Unit.CASE);
		product.getUnitQuantities().add(new UnitQuantity(Unit.CASE, caseQuantity));
		product.getUnitPrices().add(new UnitPrice(Unit.CASE, caseUnitPrice));
		product.getUnitConversions().add(new UnitConversion(Unit.CASE, caseUnitConversion));
		
		product.getUnits().add(Unit.TIE);
		product.getUnitQuantities().add(new UnitQuantity(Unit.TIE, tieQuantity));
		product.getUnitPrices().add(new UnitPrice(Unit.TIE, tieUnitPrice));
		product.getUnitConversions().add(new UnitConversion(Unit.TIE, tieUnitConversion));
		
		product.getUnits().add(Unit.CARTON);
		product.getUnitQuantities().add(new UnitQuantity(Unit.CARTON, cartonQuantity));
		product.getUnitPrices().add(new UnitPrice(Unit.CARTON, cartonUnitPrice));
		product.getUnitConversions().add(new UnitConversion(Unit.CARTON, cartonUnitConversion));
		
		product.getUnits().add(Unit.DOZEN);
		product.getUnitQuantities().add(new UnitQuantity(Unit.DOZEN, dozenQuantity));
		product.getUnitPrices().add(new UnitPrice(Unit.DOZEN, dozenUnitPrice));
		product.getUnitConversions().add(new UnitConversion(Unit.DOZEN, dozenUnitConversion));
		
		product.getUnits().add(Unit.PIECES);
		product.getUnitQuantities().add(new UnitQuantity(Unit.PIECES, piecesQuantity));
		product.getUnitPrices().add(new UnitPrice(Unit.PIECES, piecesUnitPrice));
		product.getUnitConversions().add(new UnitConversion(Unit.PIECES, piecesUnitConversion));
		
		productDao.save(product);
		productPriceDao.save(product);
		
		Product fromDb = productDao.get(product.getId());
		assertEquals(code, fromDb.getCode());
		assertEquals(description, fromDb.getDescription());
		assertEquals(active, fromDb.isActive());
		
		assertTrue(fromDb.hasUnit(Unit.CASE));
		assertTrue(fromDb.hasUnit(Unit.TIE));
		assertTrue(fromDb.hasUnit(Unit.CARTON));
		assertTrue(fromDb.hasUnit(Unit.DOZEN));
		assertTrue(fromDb.hasUnit(Unit.PIECES));
		
		assertEquals(caseQuantity, fromDb.getUnitQuantity(Unit.CASE));
		assertEquals(tieQuantity, fromDb.getUnitQuantity(Unit.TIE));
		assertEquals(cartonQuantity, fromDb.getUnitQuantity(Unit.CARTON));
		assertEquals(dozenQuantity, fromDb.getUnitQuantity(Unit.DOZEN));
		assertEquals(piecesQuantity, fromDb.getUnitQuantity(Unit.PIECES));
		
		assertEquals(caseUnitPrice, fromDb.getUnitPrice(Unit.CASE));
		assertEquals(tieUnitPrice, fromDb.getUnitPrice(Unit.TIE));
		assertEquals(cartonUnitPrice, fromDb.getUnitPrice(Unit.CARTON));
		assertEquals(dozenUnitPrice, fromDb.getUnitPrice(Unit.DOZEN));
		assertEquals(piecesUnitPrice, fromDb.getUnitPrice(Unit.PIECES));
		
		assertEquals(caseUnitConversion, fromDb.getUnitConversion(Unit.CASE));
		assertEquals(tieUnitConversion, fromDb.getUnitConversion(Unit.TIE));
		assertEquals(cartonUnitConversion, fromDb.getUnitConversion(Unit.CARTON));
		assertEquals(dozenUnitConversion, fromDb.getUnitConversion(Unit.DOZEN));
		assertEquals(piecesUnitConversion, fromDb.getUnitConversion(Unit.PIECES));
	}
	
	@Test
	public void updateAllFields() {
		Product product = createProduct();
		
		String code = "UPDATEDCODE";
		String description = "UPDATED DESCRIPTION";
		boolean active = true;
		int caseQuantity = 10;
		int tieQuantity = 15;
		int cartonQuantity = 20;
		int dozenQuantity = 25;
		int piecesQuantity = 30;
		int caseUnitConversion = 240;
		int tieUnitConversion = 36;
		int cartonUnitConversion = 60;
		int dozenUnitConversion = 12;
		int piecesUnitConversion = 1;
		
		product.setCode(code);
		product.setDescription(description);
		product.setActive(active);
		
		product.addUnitQuantity(Unit.CASE, caseQuantity);
		product.addUnitQuantity(Unit.TIE, tieQuantity);
		product.addUnitQuantity(Unit.CARTON, cartonQuantity);
		product.addUnitQuantity(Unit.DOZEN, dozenQuantity);
		product.addUnitQuantity(Unit.PIECES, piecesQuantity);
		product.setUnitConversion(Unit.CASE, caseUnitConversion);
		product.setUnitConversion(Unit.TIE, tieUnitConversion);
		product.setUnitConversion(Unit.CARTON, cartonUnitConversion);
		product.setUnitConversion(Unit.DOZEN, dozenUnitConversion);
		product.setUnitConversion(Unit.PIECES, piecesUnitConversion);
		
		productDao.save(product);
		
		Product fromDb = productDao.get(product.getId());
		assertEquals(code, fromDb.getCode());
		assertEquals(description, fromDb.getDescription());
		assertEquals(active, fromDb.isActive());
		
		assertEquals(caseQuantity, fromDb.getUnitQuantity(Unit.CASE));
		assertEquals(tieQuantity, fromDb.getUnitQuantity(Unit.TIE));
		assertEquals(cartonQuantity, fromDb.getUnitQuantity(Unit.CARTON));
		assertEquals(dozenQuantity, fromDb.getUnitQuantity(Unit.DOZEN));
		assertEquals(piecesQuantity, fromDb.getUnitQuantity(Unit.PIECES));
		
		assertEquals(caseUnitConversion, fromDb.getUnitConversion(Unit.CASE));
		assertEquals(tieUnitConversion, fromDb.getUnitConversion(Unit.TIE));
		assertEquals(cartonUnitConversion, fromDb.getUnitConversion(Unit.CARTON));
		assertEquals(dozenUnitConversion, fromDb.getUnitConversion(Unit.DOZEN));
		assertEquals(piecesUnitConversion, fromDb.getUnitConversion(Unit.PIECES));
	}

	private Product createProduct() {
		Product product = new Product();
		product.setCode("DUMMYCODE");
		product.setDescription("SOME DESCRIPTION");
		product.setActive(false);
		
		product.getUnits().add(Unit.CASE);
		product.getUnits().add(Unit.TIE);
		product.getUnits().add(Unit.CARTON);
		product.getUnits().add(Unit.DOZEN);
		product.getUnits().add(Unit.PIECES);
		
		productDao.save(product);
		productPriceDao.save(product);
		
		return product;
	}
	
	@Test
	public void updateToMinimumFields() {
		Product product = createProduct();
		
		Product cleared = new Product();
		cleared.setId(product.getId());
		cleared.setCode(product.getCode());
		cleared.setDescription(product.getDescription());
		productDao.save(cleared);
		
		Product fromDb = productDao.get(product.getId());
		assertTrue(fromDb.getUnits().isEmpty());
	}
	
}
