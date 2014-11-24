package com.pj.magic.dao;

import java.text.SimpleDateFormat;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.pj.magic.model.Product;
import com.pj.magic.model.search.StockCardInventoryReportSearchCriteria;

public class ReportDaoTest extends IntegrationTest {

	@Autowired private ReportDao reportDao;
	
	@Test
	public void test() throws Exception {
		StockCardInventoryReportSearchCriteria criteria = new StockCardInventoryReportSearchCriteria();
		criteria.setProduct(new Product(1L));
		criteria.setFromDate(new SimpleDateFormat("MM/dd/yyyy").parse("11/18/2014"));
		criteria.setToDate(new SimpleDateFormat("MM/dd/yyyy").parse("11/18/2014"));
		
		reportDao.getStockCardInventoryReport(criteria);
	}
	
}
