package com.pj.magic.dao;

import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;

@ContextConfiguration(locations={
		"classpath:applicationContext.xml",
//		"classpath:datasource-test.xml"
		"classpath:datasource.xml"
		})
public abstract class IntegrationTest extends AbstractTransactionalJUnit4SpringContextTests {

}
