package com.pj.magic.dao;

import java.util.List;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;

import com.pj.magic.model.Product;

@ContextConfiguration(locations={"classpath:applicationContext.xml"})
public class ProductDaoTest extends AbstractJUnit4SpringContextTests {
	
	@Autowired private ProductDao productDao;
	
	@Test
	public void getAllProducts() {
		List<Product> allProducts = productDao.getAllProducts();
		for (Product product : allProducts) {
			System.out.println(ToStringBuilder.reflectionToString(product));
		}
	}

}
