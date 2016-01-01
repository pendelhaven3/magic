package com.pj.magic.dao;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.pj.magic.model.Product;
import com.pj.magic.model.search.StockCardInventoryReportCriteria;

@Ignore
public class ReportDaoTest extends IntegrationTest {

	@Autowired private ReportDao reportDao;
	
	@Test
	public void getStockCardInventoryReport() throws Exception {
		StockCardInventoryReportCriteria criteria = new StockCardInventoryReportCriteria();
		criteria.setProduct(new Product(1L));
		criteria.setFromDate(new SimpleDateFormat("MM/dd/yyyy").parse("11/18/2014"));
		criteria.setToDate(new SimpleDateFormat("MM/dd/yyyy").parse("11/18/2014"));
		
		reportDao.getStockCardInventoryReport(criteria);
	}

	@Test
	public void getAllCustomerSalesSummaryReportItems() throws Exception {
		Date fromDate = new SimpleDateFormat("MM/dd/yyyy").parse("11/18/2014");
		Date toDate = new SimpleDateFormat("MM/dd/yyyy").parse("01/18/2015");
		
		reportDao.searchCustomerSalesSummaryReportItems(fromDate, toDate);
	}
	
}