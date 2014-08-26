package com.pj.magic.service;

import java.util.Date;

import javax.sql.DataSource;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;

import com.pj.magic.dao.CustomerDao;
import com.pj.magic.dao.ProductDao;
import com.pj.magic.dao.SalesRequisitionDao;
import com.pj.magic.dao.SalesRequisitionItemDao;
import com.pj.magic.dao.UserDao;
import com.pj.magic.exception.NotEnoughStocksException;
import com.pj.magic.model.Customer;
import com.pj.magic.model.Product;
import com.pj.magic.model.SalesRequisition;
import com.pj.magic.model.SalesRequisitionItem;
import com.pj.magic.model.Unit;
import com.pj.magic.model.User;

@ContextConfiguration(locations={"classpath:applicationContext.xml"})
public class SalesRequisitionServiceTest extends AbstractJUnit4SpringContextTests {

	@Autowired private SalesRequisitionService salesRequisitionService;
	
	@Autowired private CustomerDao customerDao;
	@Autowired private SalesRequisitionDao salesRequisitionDao;
	@Autowired private ProductDao productDao;
	@Autowired private ProductService productService;
	@Autowired private SalesRequisitionItemDao salesRequisitionItemDao;
	@Autowired private UserDao userDao;
	
	@Autowired private DataSource dataSource;
	
	@Test
	public void postWithNotEnoughStocksException() {
		Customer customer = new Customer();
		customer.setCode("TEST");
		customer.setName("TEST CUSTOMER");
		customer.setAddress("TEST CUSTOMER ADDRESS");
		customerDao.save(customer);
		
		User encoder = new User();
		encoder.setUsername("TEST ENCODER");
		userDao.save(encoder);
		
		SalesRequisition salesRequisition = new SalesRequisition();
		salesRequisition.setCustomer(customer);
		salesRequisition.setCreateDate(new Date());
		salesRequisition.setEncoder(encoder);
		salesRequisitionDao.save(salesRequisition);
		
		SalesRequisitionItem item = new SalesRequisitionItem();
		item.setParent(salesRequisition);
		item.setProduct(productDao.findByCode("555CAL155"));
		item.setUnit(Unit.CASE);
		item.setQuantity(4);
		salesRequisitionItemDao.save(item);
		
		item = new SalesRequisitionItem();
		item.setParent(salesRequisition);
		item.setProduct(productDao.findByCode("555HOT155"));
		item.setUnit(Unit.CASE);
		item.setQuantity(3);
		salesRequisitionItemDao.save(item);
		
		salesRequisition = salesRequisitionService.getSalesRequisition(salesRequisition.getId());
		
		Product product = productDao.findByCode("555HOT155");
		product.getUnitQuantities().get(0).setQuantity(0);
		productDao.updateAvailableQuantities(product);
		
		try {
			salesRequisitionService.post(salesRequisition);
		} catch (NotEnoughStocksException e) {
			System.out.println(e.getSalesRequisitionItem().getProduct().getCode());
			System.out.println(productService.findProductByCode("555CAL155").getUnitQuantity(Unit.CASE));
		}
	}
	
}
