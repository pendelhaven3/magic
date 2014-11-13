package com.pj.magic.dao;

import static org.junit.Assert.assertNotNull;

import java.util.Date;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;

import com.pj.magic.model.Customer;
import com.pj.magic.model.SalesRequisition;
import com.pj.magic.model.User;

@ContextConfiguration(locations={"classpath:applicationContext.xml"})
public class SalesRequisitionDaoTest extends AbstractTransactionalJUnit4SpringContextTests {
	
	@Autowired private SalesRequisitionDao salesRequisitionDao;
	@Autowired private CustomerDao customerDao;
	@Autowired private UserDao userDao;
	
	private Customer customer = new Customer();
	private User user = new User();
	
	@Before
	public void setUp() {
		customer.setCode("TEST");
		customer.setName("TEST CUSTOMER");
		customer.setBusinessAddress("TEST ADDRESS");
		customerDao.save(customer);
		
		user.setUsername("TESTENCODER");
		userDao.save(user);
	}
	
	@Test
	public void save() {
		SalesRequisition salesRequisition = new SalesRequisition();
		salesRequisition.setCustomer(customer);
		salesRequisition.setCreateDate(new Date());
		salesRequisition.setEncoder(user);
		salesRequisitionDao.save(salesRequisition);
		
		assertNotNull(salesRequisition.getId());
		assertNotNull(salesRequisition.getSalesRequisitionNumber());
	}
	
}
