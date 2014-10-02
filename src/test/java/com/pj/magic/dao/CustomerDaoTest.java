package com.pj.magic.dao;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.pj.magic.model.Customer;
import com.pj.magic.model.PaymentTerm;

public class CustomerDaoTest extends IntegrationTest {
	
	@Autowired private CustomerDao customerDao;
	@Autowired private PaymentTermDao paymentTermDao;
	
	@Test
	public void insert() {
		PaymentTerm paymentTerm = new PaymentTerm();
		paymentTerm.setName("COD");
		paymentTerm.setNumberOfDays(0);
		paymentTermDao.save(paymentTerm);
		
		Customer customer = new Customer();
		customer.setCode("PJ");
		customer.setName("PJ NAME");
		customer.setPaymentTerm(paymentTerm);
		customerDao.save(customer);
		
		Customer c = customerDao.get(customer.getId());
		System.out.println(c.getPaymentTerm());
	}

}
