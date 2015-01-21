package com.pj.magic.dao;

import java.util.Date;
import java.util.List;

import com.pj.magic.model.StockCardInventoryReportItem;
import com.pj.magic.model.report.CustomerSalesSummaryReportItem;
import com.pj.magic.model.report.InventoryReportItem;
import com.pj.magic.model.search.StockCardInventoryReportSearchCriteria;

public interface ReportDao {

	List<StockCardInventoryReportItem> getStockCardInventoryReport(
			StockCardInventoryReportSearchCriteria criteria);

	List<InventoryReportItem> getAllInventoryReportItems();

	List<CustomerSalesSummaryReportItem> searchCustomerSalesSummaryReportItems(Date fromDate, Date toDate);
	
}