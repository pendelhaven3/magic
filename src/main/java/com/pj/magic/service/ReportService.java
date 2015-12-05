package com.pj.magic.service;

import java.util.Date;
import java.util.List;

import com.pj.magic.model.StockCardInventoryReportItem;
import com.pj.magic.model.report.CustomerSalesSummaryReport;
import com.pj.magic.model.report.InventoryReport;
import com.pj.magic.model.report.SalesByManufacturerReport;
import com.pj.magic.model.report.StockTakeoffReport;
import com.pj.magic.model.search.SalesByManufacturerReportSearchCriteria;
import com.pj.magic.model.search.StockCardInventoryReportSearchCriteria;
import com.pj.magic.model.search.StockTakeoffReportCriteria;

public interface ReportService {

	List<StockCardInventoryReportItem> getStockCardInventoryReport(
			StockCardInventoryReportSearchCriteria criteria);

	InventoryReport getInventoryReport();

	CustomerSalesSummaryReport getCustomerSalesSummaryReport(Date fromDate, Date toDate);

	SalesByManufacturerReport getManufacturerSalesReport(SalesByManufacturerReportSearchCriteria criteria);
	
	StockTakeoffReport getStockTakeoffReport(StockTakeoffReportCriteria criteria);
	
}