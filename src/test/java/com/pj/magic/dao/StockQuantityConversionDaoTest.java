package com.pj.magic.dao;

import static org.junit.Assert.*;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;

import com.pj.magic.model.StockQuantityConversion;

@ContextConfiguration(locations={"classpath:applicationContext.xml"})
public class StockQuantityConversionDaoTest extends AbstractTransactionalJUnit4SpringContextTests {
	
	@Autowired private StockQuantityConversionDao stockQuantityConversionDao;
	
	@Test
	public void getAll() {
		StockQuantityConversion stockQuantityConversion = new StockQuantityConversion();
		stockQuantityConversionDao.save(stockQuantityConversion);
		
		stockQuantityConversion = new StockQuantityConversion();
		stockQuantityConversionDao.save(stockQuantityConversion);
		
		assertEquals(2, stockQuantityConversionDao.getAll().size());
	}

}
