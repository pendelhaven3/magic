package com.pj.magic.dao;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;

@ContextConfiguration(locations={"classpath:applicationContext.xml"})
public class SalesInvoiceItemDaoTest extends AbstractJUnit4SpringContextTests {

	@Autowired private SalesInvoiceItemDao salesInvoiceItemDao;
	
	@Test
	public void save() {
	}
	
}
