package com.pj.magic.service;

import java.util.List;

import com.pj.magic.model.StockCardInventoryReportItem;
import com.pj.magic.model.report.InventoryReport;
import com.pj.magic.model.search.StockCardInventoryReportSearchCriteria;

public interface ReportService {

	List<StockCardInventoryReportItem> getStockCardInventoryReport(
			StockCardInventoryReportSearchCriteria criteria);

	InventoryReport getInventoryReport();
	
}