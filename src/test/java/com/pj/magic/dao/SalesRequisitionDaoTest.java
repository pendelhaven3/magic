package com.pj.magic.dao;

import static org.junit.Assert.*;

import java.util.Date;
import java.util.List;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;

import com.pj.magic.model.SalesRequisition;

@ContextConfiguration(locations={"classpath:applicationContext.xml"})
public class SalesRequisitionDaoTest extends AbstractJUnit4SpringContextTests {
	
	@Autowired private SalesRequisitionDao salesRequisitionDao;
	
	@Test
	public void getAllSalesRequisitions() {
		List<SalesRequisition> allProducts = salesRequisitionDao.getAll();
		assertTrue(allProducts.isEmpty());
	}
	
	@Test
	public void save() {
		SalesRequisition salesRequisition = new SalesRequisition();
		salesRequisition.setCustomerName("PJ CUSTOMER");
		salesRequisition.setCreateDate(new Date());
		salesRequisition.setEncoder("PJ");
		salesRequisitionDao.save(salesRequisition);
		
		assertNotNull(salesRequisition.getId());
		assertNotNull(salesRequisition.getSalesRequisitionNumber());
	}
	
}
