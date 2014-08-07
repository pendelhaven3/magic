package com.pj.magic.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.Date;

import javax.sql.DataSource;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;

import com.pj.magic.dao.CustomerDao;
import com.pj.magic.dao.ProductDao;
import com.pj.magic.dao.SalesRequisitionDao;
import com.pj.magic.dao.SalesRequisitionItemDao;
import com.pj.magic.exception.NotEnoughStocksException;
import com.pj.magic.model.Customer;
import com.pj.magic.model.Product;
import com.pj.magic.model.SalesRequisition;
import com.pj.magic.model.SalesRequisitionItem;
import com.pj.magic.model.Unit;

@ContextConfiguration(locations={"classpath:applicationContext.xml"})
public class SalesRequisitionServiceIntegrationTest extends AbstractTransactionalJUnit4SpringContextTests {

	@Autowired private SalesRequisitionService salesRequisitionService;
	
	@Autowired private SalesRequisitionDao salesRequisitionDao;
	@Autowired private SalesRequisitionItemDao salesRequisitionItemDao;
	@Autowired private CustomerDao customerDao;
	@Autowired private ProductDao productDao;
	
	@Autowired private DataSource dataSource;
	
	private SalesRequisition salesRequisition = new SalesRequisition();
	
	@Before
	public void setUp() {
		Customer customer = new Customer();
		customer.setCode("TEST");
		customer.setName("TEST CUSTOMER");
		customer.setAddress("TEST CUSTOMER ADDRESS");
		customerDao.save(customer);
		
		salesRequisition.setCustomer(customer);
		salesRequisition.setCreateDate(new Date());
		salesRequisition.setEncoder("TEST ENCODER");
		salesRequisitionDao.save(salesRequisition);
	}
	
	@Test
	public void postWithNotEnoughStocksException() throws Exception {
		SalesRequisitionItem item = new SalesRequisitionItem();
		item.setParent(salesRequisition);
		item.setProduct(productDao.findByCode("555CAL155"));
		item.setUnit(Unit.CASE);
		item.setQuantity(4);
		salesRequisitionItemDao.save(item);
		salesRequisition.getItems().add(item);
		
		item = new SalesRequisitionItem();
		item.setParent(salesRequisition);
		item.setProduct(productDao.findByCode("555HOT155"));
		item.setUnit(Unit.CASE);
		item.setQuantity(3);
		salesRequisitionItemDao.save(item);
		salesRequisition.getItems().add(item);
		
		Product product = productDao.findByCode("555HOT155");
		product.getUnitQuantities().get(0).setQuantity(0);
		productDao.updateAvailableQuantities(product);
		
		DataSourceUtils.getConnection(dataSource).commit(); // do this because posting has its own transaction
		
		try {
			salesRequisitionService.post(salesRequisition);
			fail("Post should have failed");
		} catch (NotEnoughStocksException e) {
			assertEquals("Should have rollbacked", 
					4, productDao.findByCode("555CAL155").getUnitQuantity(Unit.CASE));
		}
	}
	
}
