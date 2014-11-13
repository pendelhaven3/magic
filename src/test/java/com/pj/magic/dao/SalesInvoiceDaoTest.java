package com.pj.magic.dao;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.pj.magic.model.Customer;

public class SalesInvoiceDaoTest extends IntegrationTest {

	@Autowired private SalesInvoiceDao salesInvoiceDao;
	
	@Test
	public void test() {
		salesInvoiceDao.findAllUnpaidByCustomer(new Customer(1L));
	}
	
}
