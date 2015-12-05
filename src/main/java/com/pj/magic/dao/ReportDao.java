package com.pj.magic.dao;

import java.util.Date;
import java.util.List;

import com.pj.magic.model.StockCardInventoryReportItem;
import com.pj.magic.model.report.CustomerSalesSummaryReportItem;
import com.pj.magic.model.report.InventoryReportItem;
import com.pj.magic.model.report.SalesByManufacturerReportItem;
import com.pj.magic.model.report.StockTakeoffReportItem;
import com.pj.magic.model.search.SalesByManufacturerReportSearchCriteria;
import com.pj.magic.model.search.StockCardInventoryReportSearchCriteria;
import com.pj.magic.model.search.StockTakeoffReportCriteria;

public interface ReportDao {

	List<StockCardInventoryReportItem> getStockCardInventoryReport(
			StockCardInventoryReportSearchCriteria criteria);

	List<InventoryReportItem> getAllInventoryReportItems();

	List<CustomerSalesSummaryReportItem> searchCustomerSalesSummaryReportItems(Date fromDate, Date toDate);

	List<SalesByManufacturerReportItem> searchSalesByManufacturerReportItems(
			SalesByManufacturerReportSearchCriteria criteria);

	List<StockTakeoffReportItem> searchStockTakeoffReportItems(StockTakeoffReportCriteria criteria);
	
}