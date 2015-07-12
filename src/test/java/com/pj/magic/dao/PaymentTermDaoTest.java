package com.pj.magic.dao;

import static org.junit.Assert.*;

import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;

import com.pj.magic.model.PaymentTerm;

@Ignore
@ContextConfiguration(locations={"classpath:applicationContext.xml"})
public class PaymentTermDaoTest extends AbstractTransactionalJUnit4SpringContextTests {

	@Autowired private PaymentTermDao paymentTermDao;
	
	@Test
	public void save() {
		String name = "7 DAYS";
		int numberOfDays = 7;
		
		PaymentTerm paymentTerm = new PaymentTerm();
		paymentTerm.setName(name);
		paymentTerm.setNumberOfDays(numberOfDays);
		paymentTermDao.save(paymentTerm);
		
		PaymentTerm fromDb = paymentTermDao.get(paymentTerm.getId());
		assertEquals(name, fromDb.getName());
		assertEquals(numberOfDays, fromDb.getNumberOfDays());
	}
	
	@Test
	public void getAll() {
		PaymentTerm paymentTerm = new PaymentTerm();
		paymentTerm.setName("1 DAY");
		paymentTerm.setNumberOfDays(1);
		paymentTermDao.save(paymentTerm);
		
		PaymentTerm paymentTerm2 = new PaymentTerm();
		paymentTerm2.setName("2 DAYS");
		paymentTerm2.setNumberOfDays(2);
		paymentTermDao.save(paymentTerm2);
		
		assertEquals(2, paymentTermDao.getAll().size());
	}
	
}
