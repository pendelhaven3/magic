package com.pj.magic.dao;

import static org.junit.Assert.assertNotNull;

import java.util.Date;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;

import com.pj.magic.model.Customer;
import com.pj.magic.model.SalesRequisition;

@ContextConfiguration(locations={"classpath:applicationContext.xml"})
public class SalesRequisitionDaoTest extends AbstractJUnit4SpringContextTests {
	
	@Autowired private SalesRequisitionDao salesRequisitionDao;
	
	@Test
	public void save() {
		SalesRequisition salesRequisition = new SalesRequisition();
		
		// TODO: Replce with proper Customer table
		Customer customer = new Customer();
		customer.setName("PJ CUSTOMER");
		
		salesRequisition.setCustomer(customer);
		salesRequisition.setCreateDate(new Date());
		salesRequisition.setEncoder("PJ");
		salesRequisitionDao.save(salesRequisition);
		
		assertNotNull(salesRequisition.getId());
		assertNotNull(salesRequisition.getSalesRequisitionNumber());
	}
	
}
