package com.pj.magic.dao;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;

import com.pj.magic.model.ProductCategory;

@ContextConfiguration(locations={"classpath:applicationContext.xml"})
public class ProductCategoryDaoTest extends AbstractTransactionalJUnit4SpringContextTests {

	@Autowired private ProductCategoryDao dao;
	
	@Test
	public void test() {
		ProductCategory category = new ProductCategory();
		category.setName("FOOD");
		dao.save(category);
		System.out.println("x: " + category.getId());
	}
	
}
