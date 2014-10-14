package com.pj.magic.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.pj.magic.model.ProductCategory;
import com.pj.magic.model.ProductSubcategory;

public class ProductSubcategoryDaoTest extends IntegrationTest {

	@Autowired private ProductSubcategoryDao productSubcategoryDao;
	
	@PersistenceContext
	private EntityManager entityManager;
	
	@Before
	public void setUp() {
		jdbcTemplate.update("insert into PRODUCT_CATEGORY (ID, NAME) values (1, 'HOUSEHOLD ITEMS')");
		jdbcTemplate.update("insert into PRODUCT_CATEGORY (ID, NAME) values (2, 'FOOD AND BEVERAGES')");
		jdbcTemplate.update("insert into PRODUCT_SUBCATEGORY (ID, PRODUCT_CATEGORY_ID, NAME) "
				+ "values (1, 2, 'WINES AND LIQUORS')");
		jdbcTemplate.update("insert into PRODUCT_SUBCATEGORY (ID, PRODUCT_CATEGORY_ID, NAME) "
				+ "values (2, 1, 'DETERGENTS AND FABRIC CONDITIONERS')");
	}
	
	@Test
	public void save() {
		ProductSubcategory subcategory = new ProductSubcategory();
		subcategory.setParent(entityManager.find(ProductCategory.class, 2L));
		subcategory.setName("BOTTLED JUICES");
		productSubcategoryDao.save(subcategory);
		
		assertNotNull(subcategory.getId());
		assertEquals(1, countRowsInTableWhere("PRODUCT_SUBCATEGORY", 
				"NAME = 'BOTTLED JUICES' and PRODUCT_CATEGORY_ID = 2"));
	}
	
	@Test
	public void get() {
		ProductSubcategory subcategory = productSubcategoryDao.get(1L);
		assertEquals("WINES AND LIQUORS", subcategory.getName());
		assertEquals("FOOD AND BEVERAGES", subcategory.getParent().getName());
	}
	
}
