package com.pj.magic.dao;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.pj.magic.model.PurchaseOrder;

public class PurchaseOrderDaoTest extends DaoTest {

	@Autowired private PurchaseOrderDao purchaseOrderDao;
	
	@Before
	public void setUp() {
		
	}
	
	@Test
	public void saveThenGet() {
		PurchaseOrder purchaseOrder = new PurchaseOrder();
		purchaseOrderDao.save(purchaseOrder);
		
		assertNotNull(purchaseOrder.getId());
	}
	
}
