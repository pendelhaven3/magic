package com.pj.magic.service;

import java.util.Date;
import java.util.List;

import com.pj.magic.model.StockCardInventoryReportItem;
import com.pj.magic.model.report.CustomerSalesSummaryReport;
import com.pj.magic.model.report.InventoryReport;
import com.pj.magic.model.report.SalesByManufacturerReport;
import com.pj.magic.model.report.StockUptakeReport;
import com.pj.magic.model.search.SalesByManufacturerReportCriteria;
import com.pj.magic.model.search.StockCardInventoryReportCriteria;
import com.pj.magic.model.search.StockUptakeReportCriteria;

public interface ReportService {

	List<StockCardInventoryReportItem> getStockCardInventoryReport(
			StockCardInventoryReportCriteria criteria);

	InventoryReport getInventoryReport();

	CustomerSalesSummaryReport getCustomerSalesSummaryReport(Date fromDate, Date toDate);

	SalesByManufacturerReport getManufacturerSalesReport(SalesByManufacturerReportCriteria criteria);
	
	StockUptakeReport getStockUptakeReport(StockUptakeReportCriteria criteria);
	
}