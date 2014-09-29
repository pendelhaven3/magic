package com.pj.magic.service;

import static org.junit.Assert.*;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.ColumnMapRowMapper;

import com.pj.magic.Constants;
import com.pj.magic.dao.IntegrationTest;
import com.pj.magic.model.Product;
import com.pj.magic.model.Unit;

public class ProductServiceIntegrationTest extends IntegrationTest {

	private static final int NON_CANVASSER_1_PRICING_SCHEME_ID = 2;
	private static final int NON_CANVASSER_2_PRICING_SCHEME_ID = 3;
	
	@Autowired private ProductService productService;
	
	private Product product;
	
	@Before
	public void setUp() {
		insertPricingSchemes();
		product = new Product();
	}
	
	@Test
	public void saveNewProduct() {
		product.setCode("TEST");
		product.setDescription("TEST PRODUCT DESCRIPTION");
		product.setUnits(Arrays.asList(Unit.CASE, Unit.PIECES));
		product.setUnitConversion(Unit.CASE, 10);
		product.setUnitConversion(Unit.PIECES, 1);
//		product.setFinalCost(Unit.CASE, new BigDecimal("90.00"));
//		product.setUnitPrice(Unit.CASE, new BigDecimal("100.00"));

		productService.save(product);
		
//		verifyCostsOfSmallerUnitsAreCalculated();
//		verifyPricesOfSmallerUnitsAreCalculated();
//		verifyPricesForNonCanvasserPricingSchemesAreNotSet();
	}

	private void verifyPricesForNonCanvasserPricingSchemesAreNotSet() {
		List<Map<String, Object>> result = jdbcTemplate.query("select * from PRODUCT_PRICE where PRODUCT_ID = ? and PRICING_SCHEME_ID = ?", 
				new ColumnMapRowMapper(), product.getId(), NON_CANVASSER_1_PRICING_SCHEME_ID);
		assertEquals(1, result.size());

		Map<String, Object> unitPrice = result.get(0);
		assertEquals(new BigDecimal("0.00"), unitPrice.get("UNIT_PRICE_CSE"));
		assertEquals(new BigDecimal("0.00"), unitPrice.get("UNIT_PRICE_PCS"));
		
		result = jdbcTemplate.query("select * from PRODUCT_PRICE where PRODUCT_ID = ? and PRICING_SCHEME_ID = ?", 
				new ColumnMapRowMapper(), product.getId(), NON_CANVASSER_2_PRICING_SCHEME_ID);
		assertEquals(1, result.size());

		unitPrice = result.get(0);
		assertEquals(new BigDecimal("0.00"), unitPrice.get("UNIT_PRICE_CSE"));
		assertEquals(new BigDecimal("0.00"), unitPrice.get("UNIT_PRICE_PCS"));
	}

	private void verifyPricesOfSmallerUnitsAreCalculated() {
		List<Map<String, Object>> result = jdbcTemplate.query("select * from PRODUCT_PRICE where PRODUCT_ID = ? and PRICING_SCHEME_ID = ?", 
				new ColumnMapRowMapper(), product.getId(), Constants.CANVASSER_PRICING_SCHEME_ID);
		assertEquals(1, result.size());
		
		Map<String, Object> unitPrice = result.get(0);
		assertEquals(new BigDecimal("100.00"), unitPrice.get("UNIT_PRICE_CSE"));
		assertEquals(new BigDecimal("25.00"), unitPrice.get("UNIT_PRICE_CTN"));
		assertEquals(new BigDecimal("5.00"), unitPrice.get("UNIT_PRICE_PCS"));
		
		result = jdbcTemplate.query("select * from PRODUCT_PRICE where PRODUCT_ID = ? and PRICING_SCHEME_ID = ?", 
				new ColumnMapRowMapper(), product.getId(), NON_CANVASSER_1_PRICING_SCHEME_ID);
		assertEquals(1, result.size());
		
		unitPrice = result.get(0);
		assertEquals(new BigDecimal("120.00"), unitPrice.get("UNIT_PRICE_CSE"));
		assertEquals(new BigDecimal("30.00"), unitPrice.get("UNIT_PRICE_CTN"));
		assertEquals(new BigDecimal("6.00"), unitPrice.get("UNIT_PRICE_PCS"));
		
		result = jdbcTemplate.query("select * from PRODUCT_PRICE where PRODUCT_ID = ? and PRICING_SCHEME_ID = ?", 
				new ColumnMapRowMapper(), product.getId(), NON_CANVASSER_2_PRICING_SCHEME_ID);
		assertEquals(1, result.size());
		
		unitPrice = result.get(0);
		assertEquals(new BigDecimal("0.00"), unitPrice.get("UNIT_PRICE_CSE"));
		assertEquals(new BigDecimal("0.00"), unitPrice.get("UNIT_PRICE_CTN"));
		assertEquals(new BigDecimal("0.00"), unitPrice.get("UNIT_PRICE_PCS"));
	}

	private void verifyCostsOfSmallerUnitsAreUpdated() {
		List<Map<String, Object>> result = jdbcTemplate.query("select * from PRODUCT where ID = ?", 
				new ColumnMapRowMapper(), product.getId());
		assertEquals(1, result.size());

		// verify costs of smaller units are automatically calculated
		Map<String, Object> productFromDb = result.get(0);
		assertEquals(new BigDecimal("100.00"), productFromDb.get("GROSS_COST_CSE"));
		assertEquals(new BigDecimal("25.00"), productFromDb.get("GROSS_COST_CTN"));
		assertEquals(new BigDecimal("5.00"), productFromDb.get("GROSS_COST_PCS"));
		assertEquals(new BigDecimal("90.00"), productFromDb.get("FINAL_COST_CSE"));
		assertEquals(new BigDecimal("22.50"), productFromDb.get("FINAL_COST_CTN"));
		assertEquals(new BigDecimal("4.50"), productFromDb.get("FINAL_COST_PCS"));
	}

	private void insertPricingSchemes() {
		jdbcTemplate.update("insert into PRICING_SCHEME (ID, NAME) values (1, 'CANVASSER')");
		jdbcTemplate.update("insert into PRICING_SCHEME (ID, NAME) values (2, 'NOT CANVASSER 1')");
		jdbcTemplate.update("insert into PRICING_SCHEME (ID, NAME) values (3, 'NOT CANVASSER 2')");
	}
	
	@Test
	public void updateProduct() {
		product = insertProduct();
		insertProductPrices();
		
		product.addUnit(Unit.CARTON);
		product.setUnitConversion(Unit.CASE, 20);
		product.setUnitConversion(Unit.CARTON, 5);
		
		productService.save(product);
		
		verifyCostsOfSmallerUnitsAreUpdated();
		verifyPricesOfSmallerUnitsAreCalculated();
	}

	private static final String INSERT_PRODUCT_PRICES_SQL =
			"insert into PRODUCT_PRICE (PRICING_SCHEME_ID, PRODUCT_ID, "
			+ " UNIT_PRICE_CSE, UNIT_PRICE_TIE, UNIT_PRICE_CTN, UNIT_PRICE_DOZ, UNIT_PRICE_PCS)"
			+ " values (?, ?, ?, ?, ?, ?, ?)";
	
	private void insertProductPrices() {
		jdbcTemplate.update(INSERT_PRODUCT_PRICES_SQL,
				Constants.CANVASSER_PRICING_SCHEME_ID,
				1L,
				new BigDecimal("100.00"),
				BigDecimal.ZERO,
				BigDecimal.ZERO,
				BigDecimal.ZERO,
				new BigDecimal("10.00")
		);
		
		jdbcTemplate.update(INSERT_PRODUCT_PRICES_SQL,
				NON_CANVASSER_1_PRICING_SCHEME_ID,
				1L,
				new BigDecimal("120.00"),
				BigDecimal.ZERO,
				BigDecimal.ZERO,
				BigDecimal.ZERO,
				new BigDecimal("12.00")
		);
		
		jdbcTemplate.update(INSERT_PRODUCT_PRICES_SQL,
				NON_CANVASSER_2_PRICING_SCHEME_ID,
				1L,
				BigDecimal.ZERO,
				BigDecimal.ZERO,
				BigDecimal.ZERO,
				BigDecimal.ZERO,
				BigDecimal.ZERO
		);
	}

	private static final String INSERT_PRODUCT_SQL = 
			"insert into PRODUCT (ID, CODE, DESCRIPTION, UNIT_IND_CSE, UNIT_IND_PCS,"
			+ " GROSS_COST_CSE, GROSS_COST_PCS, FINAL_COST_CSE, FINAL_COST_PCS,"
			+ " UNIT_CONV_CSE, UNIT_CONV_PCS)"
			+ " values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
	
	private Product insertProduct() {
		jdbcTemplate.update(INSERT_PRODUCT_SQL,
				1, "TEST", "TEST PRODUCT DESCRIPTION", "Y", "Y",
				new BigDecimal("100.00"), new BigDecimal("10.00"),
				new BigDecimal("90.00"), new BigDecimal("9.00"),
				10, 1);
		
		product.setId(1L);
		product.setCode("TEST");
		product.setDescription("TEST PRODUCT DESCRIPTION");
		product.addUnit(Unit.CASE);
		product.addUnit(Unit.PIECES);
		product.setUnitConversion(Unit.CASE, 10);
		product.setUnitConversion(Unit.PIECES, 1);
		product.setGrossCost(Unit.CASE, new BigDecimal("100.00"));
		product.setGrossCost(Unit.PIECES, new BigDecimal("10.00"));
		product.setFinalCost(Unit.CASE, new BigDecimal("90.00"));
		product.setFinalCost(Unit.PIECES, new BigDecimal("9.00"));
		return product;
	}
	
}
