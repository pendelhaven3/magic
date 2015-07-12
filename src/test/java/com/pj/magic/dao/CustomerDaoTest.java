package com.pj.magic.dao;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.pj.magic.model.Customer;
import com.pj.magic.model.PaymentTerm;
import com.pj.magic.model.search.CustomerSearchCriteria;

public class CustomerDaoTest extends IntegrationTest {
	
	@Autowired private CustomerDao customerDao;
	
	private void insertTestPaymentTerm() {
		jdbcTemplate.update("insert into PAYMENT_TERM (ID, NAME, NUMBER_OF_DAYS) values (1, 'TESTPAYTERM', 0)");
	}
	
	private void insertTestCustomer() {
		jdbcTemplate.update("insert into CUSTOMER (ID, CODE, NAME) values (1, 'TESTCUST', 'TEST CUSTOMER')");
	}
	
	private void insertTestCustomer2() {
		jdbcTemplate.update("insert into CUSTOMER (ID, CODE, NAME) values (2, 'TESTCUST2', 'TEST CUSTOMER 2')");
	}
	
	private void insertTestCustomer3() {
		insertTestPaymentTerm();
		jdbcTemplate.update("insert into CUSTOMER (ID, CODE, NAME, PAYMENT_TERM_ID)"
				+ " values (3, 'TESTCUST3', 'TEST CUSTOMER 3', 1)");
	}
	
	@Test
	public void save_insert_minimumFields() {
		insertTestPaymentTerm();
		
		Customer customer = new Customer();
		customer.setCode("TESTCUSTOMER");
		customer.setName("TEST CUSTOMER");
		customerDao.save(customer);
		
		Customer fromDb = customerDao.get(customer.getId());
		assertNotNull(fromDb);
		assertEquals(customer.getCode(), fromDb.getCode());
		assertEquals(customer.getName(), fromDb.getName());
	}
	
	@Test
	public void save_insert_allFields() {
		insertTestPaymentTerm();
		
		Customer customer = new Customer();
		customer.setCode("TESTCUSTOMER");
		customer.setName("TEST CUSTOMER");
		customer.setBusinessAddress("TEST CUSTOMER ADDRESS");
		customer.setContactPerson("TEST_CONTACT_PERSON");
		customer.setContactNumber("TEST_CONTACT_NUMBER");
		customer.setPaymentTerm(new PaymentTerm(1L));
		customerDao.save(customer);
		
		Customer fromDb = customerDao.get(customer.getId());
		assertNotNull(fromDb);
		assertEquals(customer.getCode(), fromDb.getCode());
		assertEquals(customer.getName(), fromDb.getName());
		assertEquals(customer.getBusinessAddress(), fromDb.getBusinessAddress());
		assertEquals(customer.getContactPerson(), fromDb.getContactPerson());
		assertEquals(customer.getContactNumber(), fromDb.getContactNumber());
		assertEquals(customer.getPaymentTerm(), fromDb.getPaymentTerm());
	}

	@Test
	public void save_update_minimumFields() {
		insertTestCustomer();
		
		Customer customer = new Customer();
		customer.setId(1L);
		customer.setCode("TESTCUSTOMER2"); // TODO: Why is this working when the new code is more than 12 characters?
		customer.setName("TEST CUSTOMER 2");
		customerDao.save(customer);
		
		Customer fromDb = customerDao.get(customer.getId());
		assertNotNull(fromDb);
		assertEquals(customer.getCode(), fromDb.getCode());
		assertEquals(customer.getName(), fromDb.getName());
	}

	@Test
	public void save_update_allFields() {
		insertTestCustomer();
		insertTestPaymentTerm();
		
		Customer customer = new Customer();
		customer.setId(1L);
		customer.setCode("TESTCUSTOMER2");
		customer.setName("TEST CUSTOMER 2");
		customer.setBusinessAddress("TEST CUSTOMER ADDRESS");
		customer.setContactPerson("TEST_CONTACT_PERSON");
		customer.setContactNumber("TEST_CONTACT_NUMBER");
		customer.setPaymentTerm(new PaymentTerm(1L));
		customerDao.save(customer);
		
		Customer fromDb = customerDao.get(customer.getId());
		assertNotNull(fromDb);
		assertEquals(customer.getCode(), fromDb.getCode());
		assertEquals(customer.getName(), fromDb.getName());
		assertEquals(customer.getBusinessAddress(), fromDb.getBusinessAddress());
		assertEquals(customer.getContactPerson(), fromDb.getContactPerson());
		assertEquals(customer.getContactNumber(), fromDb.getContactNumber());
		assertEquals(customer.getPaymentTerm(), fromDb.getPaymentTerm());
	}
	
	@Test
	public void getAll() {
		insertTestCustomer2();
		insertTestCustomer();
		
		List<Customer> results = customerDao.getAll();
		assertEquals(2, results.size());
		assertEquals(1L, results.get(0).getId().longValue());
		assertEquals(2L, results.get(1).getId().longValue());
	}
	
	@Test
	public void get_success() {
		insertTestCustomer();
		
		Customer fromDb = customerDao.get(1L);
		assertNotNull(fromDb);
		assertEquals("TESTCUST", fromDb.getCode());
		assertEquals("TEST CUSTOMER", fromDb.getName());
	}
	
	@Test
	public void get_invalidId() {
		Customer fromDb = customerDao.get(1L);
		assertNull(fromDb);
	}
	
	@Test
	public void findFirstWithCodeLike_success() {
		insertTestCustomer2();
		insertTestCustomer();
		
		// TODO: investigate further why ordering seems to be done. is it because there is unique index?
		
		Customer fromDb = customerDao.findFirstWithCodeLike("TEST");
		assertNotNull(fromDb);
		assertEquals(1L, fromDb.getId().longValue());
	}
	
	@Test
	public void findFirstWithCodeLike_noMatch() {
		insertTestCustomer();
		insertTestCustomer2();
		
		Customer fromDb = customerDao.findFirstWithCodeLike("HARK");
		assertNull(fromDb);
	}
	
	@Test
	public void findByCode_success() {
		insertTestCustomer();
		insertTestCustomer3();
		
		// TODO: 2 cases here so that CustomerRowMapper can be covered
		
		Customer fromDb = customerDao.findByCode("TESTCUST3");
		assertNotNull(fromDb);
		assertEquals(3L, fromDb.getId().longValue());
		
		fromDb = customerDao.findByCode("TESTCUST");
		assertNotNull(fromDb);
		assertEquals(1L, fromDb.getId().longValue());
	}
	
	@Test
	public void findByCode_noMatch() {
		insertTestCustomer();
		insertTestCustomer2();
		
		Customer fromDb = customerDao.findByCode("HARK");
		assertNull(fromDb);
	}

	@Test
	public void findAllWithNameLike() {
		jdbcTemplate.update("insert into CUSTOMER (ID, CODE, NAME) values (1, 'TESTCUST', 'SEARCH ME')");
		jdbcTemplate.update("insert into CUSTOMER (ID, CODE, NAME) values (2, 'TESTCUST2', 'DONT SEARCH ME')");
		
		List<Customer> results = customerDao.findAllWithNameLike("SEA");
		assertEquals(1, results.size());
		assertEquals(1, results.get(0).getId().longValue());
	}
	
	@Test
	public void search_active() {
		jdbcTemplate.update("insert into CUSTOMER (ID, CODE, NAME, ACTIVE_IND) values (1, 'TESTCUST', 'CUSTOMER1', 'N')");
		jdbcTemplate.update("insert into CUSTOMER (ID, CODE, NAME, ACTIVE_IND) values (2, 'TESTCUST2', 'NOT CUSTOMER2', 'Y')");
		jdbcTemplate.update("insert into CUSTOMER (ID, CODE, NAME, ACTIVE_IND) values (3, 'TESTCUST3', 'CUSTOMER3', 'Y')");
		
		CustomerSearchCriteria criteria = new CustomerSearchCriteria();
		criteria.setNameLike("CUST");
		criteria.setActive(true);
		
		List<Customer> results = customerDao.search(criteria);
		assertEquals(1, results.size());
		assertEquals(3, results.get(0).getId().longValue());
	}

	@Test
	public void search_inactive() {
		jdbcTemplate.update("insert into CUSTOMER (ID, CODE, NAME, ACTIVE_IND) values (1, 'TESTCUST', 'CUSTOMER1', 'N')");
		jdbcTemplate.update("insert into CUSTOMER (ID, CODE, NAME, ACTIVE_IND) values (2, 'TESTCUST2', 'NOT CUSTOMER2', 'Y')");
		jdbcTemplate.update("insert into CUSTOMER (ID, CODE, NAME, ACTIVE_IND) values (3, 'TESTCUST3', 'CUSTOMER3', 'Y')");
		
		CustomerSearchCriteria criteria = new CustomerSearchCriteria();
		criteria.setNameLike("CUST");
		criteria.setActive(false);
		
		List<Customer> results = customerDao.search(criteria);
		assertEquals(1, results.size());
		assertEquals(1, results.get(0).getId().longValue());
	}
	
	@Test
	public void search_noCriteria() {
		jdbcTemplate.update("insert into CUSTOMER (ID, CODE, NAME, ACTIVE_IND) values (1, 'TESTCUST', 'B-CUSTOMER', 'N')");
		jdbcTemplate.update("insert into CUSTOMER (ID, CODE, NAME, ACTIVE_IND) values (2, 'TESTCUST2', 'A-CUSTOMER', 'Y')");
		
		List<Customer> results = customerDao.search(new CustomerSearchCriteria());
		assertEquals(2, results.size());
		assertEquals(2, results.get(0).getId().longValue());
		assertEquals(1, results.get(1).getId().longValue());
	}
	
	@Test
	public void delete() {
		insertTestCustomer();
		
		customerDao.delete(new Customer(1L));
		
		assertEquals(0, countRowsInTable("CUSTOMER"));
	}
	
}