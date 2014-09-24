package com.pj.magic.dao;

import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;

@ContextConfiguration(locations={
		"classpath:applicationContext.xml",
		"classpath:datasource-test.xml"
		})
public abstract class DaoTest extends AbstractTransactionalJUnit4SpringContextTests {

}
