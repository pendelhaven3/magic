package com.pj.magic.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import java.util.List;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;

import com.pj.magic.model.Product;

@ContextConfiguration(locations={"classpath:applicationContext.xml"})
public class ProductDaoTest extends AbstractTransactionalJUnit4SpringContextTests {
	
	@Autowired private ProductDao productDao;
	
	@Test
	public void getAllProducts() {
		List<Product> allProducts = productDao.getAll();
		assertFalse(allProducts.isEmpty());
	}
	
	@Test
	public void findProductByCode() {
		Product product = productDao.findByCode("555CAL155");
		assertNotNull(product);
	}

	@Test
	public void findFirstWithCodeLike() {
		Product product = productDao.findFirstWithCodeLike("A");
		assertEquals("ACELEB030", product.getCode());
	}
	
	@Test
	public void update() {
		Product product = productDao.findByCode("555CAL155");
		product.setDescription("TUNA");
		productDao.save(product);

		product = productDao.findByCode("555CAL155");
		assertEquals("TUNA", product.getDescription());
	}
	
}
