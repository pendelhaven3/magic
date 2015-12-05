package com.pj.magic.service.impl;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pj.magic.dao.ReportDao;
import com.pj.magic.model.StockCardInventoryReportItem;
import com.pj.magic.model.report.CustomerSalesSummaryReport;
import com.pj.magic.model.report.InventoryReport;
import com.pj.magic.model.report.SalesByManufacturerReport;
import com.pj.magic.model.report.StockTakeoffReport;
import com.pj.magic.model.search.SalesByManufacturerReportSearchCriteria;
import com.pj.magic.model.search.StockCardInventoryReportSearchCriteria;
import com.pj.magic.model.search.StockTakeoffReportCriteria;
import com.pj.magic.service.ReportService;

@Service
public class ReportServiceImpl implements ReportService {

	@Autowired private ReportDao reportDao;
	
	@Override
	public List<StockCardInventoryReportItem> getStockCardInventoryReport(
			StockCardInventoryReportSearchCriteria criteria) {
		return reportDao.getStockCardInventoryReport(criteria);
	}

	@Override
	public InventoryReport getInventoryReport() {
		InventoryReport report = new InventoryReport();
		report.setItems(reportDao.getAllInventoryReportItems());
		return report;
	}

	@Override
	public CustomerSalesSummaryReport getCustomerSalesSummaryReport(Date fromDate, Date toDate) {
		CustomerSalesSummaryReport report = new CustomerSalesSummaryReport();
		report.setItems(reportDao.searchCustomerSalesSummaryReportItems(fromDate, toDate));
		return report;
	}

	@Override
	public SalesByManufacturerReport getManufacturerSalesReport(SalesByManufacturerReportSearchCriteria criteria) {
		SalesByManufacturerReport report = new SalesByManufacturerReport();
		report.setItems(reportDao.searchSalesByManufacturerReportItems(criteria));
		return report; 
	}

	@Override
	public StockTakeoffReport getStockTakeoffReport(StockTakeoffReportCriteria criteria) {
		StockTakeoffReport report = new StockTakeoffReport();
		report.setItems(reportDao.searchStockTakeoffReportItems(criteria));
		return report;
	}

}