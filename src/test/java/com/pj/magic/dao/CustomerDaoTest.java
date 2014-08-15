package com.pj.magic.dao;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;

@ContextConfiguration(locations={"classpath:applicationContext.xml"})
public class CustomerDaoTest extends AbstractTransactionalJUnit4SpringContextTests {
	
	@Autowired private CustomerDao customerDao;
	
	@Test
	public void insert() {
		// TODO: add implementation
	}

}
