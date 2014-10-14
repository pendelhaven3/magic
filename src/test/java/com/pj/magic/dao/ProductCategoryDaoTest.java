package com.pj.magic.dao;

import static org.junit.Assert.*;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.pj.magic.model.ProductCategory;

public class ProductCategoryDaoTest extends IntegrationTest {

	@Autowired private ProductCategoryDao productCategoryDao;
	
	@Test
	public void save() {
		ProductCategory category = new ProductCategory();
		category.setName("FOOD");
		productCategoryDao.save(category);
		
		assertNotNull(category.getId());
	}
	
	@Test
	public void get() {
		jdbcTemplate.update("insert into PRODUCT_CATEGORY (ID, NAME) values (1, 'FOOD AND BEVERAGES')");
		
		ProductCategory productCategory = productCategoryDao.get(1L);
		assertEquals("FOOD AND BEVERAGES", productCategory.getName());
	}
	
}
