package com.pj.magic.dao;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.pj.magic.model.Product;

public class ReportDaoTest extends IntegrationTest {

	@Autowired private ReportDao reportDao;
	
	@Test
	public void test() {
		reportDao.getStockCardInventoryReport(new Product(1L));
	}
	
}
