package com.pj.magic.service.impl;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pj.magic.dao.InventoryCheckDao;
import com.pj.magic.dao.ReportDao;
import com.pj.magic.model.InventoryCheck;
import com.pj.magic.model.InventoryCorrection;
import com.pj.magic.model.StockCardInventoryReportItem;
import com.pj.magic.model.report.CustomerSalesSummaryReport;
import com.pj.magic.model.report.InventoryReport;
import com.pj.magic.model.report.SalesByManufacturerReport;
import com.pj.magic.model.report.StockOfftakeReport;
import com.pj.magic.model.search.SalesByManufacturerReportCriteria;
import com.pj.magic.model.search.StockCardInventoryReportCriteria;
import com.pj.magic.model.search.StockOfftakeReportCriteria;
import com.pj.magic.repository.InventoryCorrectionRepository;
import com.pj.magic.service.ReportService;

@Service
public class ReportServiceImpl implements ReportService {

	@Autowired private ReportDao reportDao;
	@Autowired private InventoryCheckDao inventoryCheckDao;
	@Autowired private InventoryCorrectionRepository inventoryCorrectionRepository;
	
	@Override
	public List<StockCardInventoryReportItem> getStockCardInventoryReport(StockCardInventoryReportCriteria criteria) {
		InventoryCheck lastInventoryCheck = null;
		InventoryCorrection lastInventoryCorrection = null;
		
		if (criteria.isFromLastInventoryCheck()) {
			lastInventoryCorrection = inventoryCorrectionRepository.findMostRecentByProduct(criteria.getProduct());
			lastInventoryCheck = inventoryCheckDao.getMostRecent();
			
			if (lastInventoryCorrection != null && lastInventoryCheck != null) {
				if (lastInventoryCorrection.getPostDate().compareTo(lastInventoryCheck.getInventoryDate()) > 0) {
					criteria.setFromDateTime(lastInventoryCorrection.getPostDate());
					lastInventoryCheck = null;
				} else {
					criteria.setFromDate(lastInventoryCheck.getInventoryDate());
					lastInventoryCorrection = null;
				}
			}
			
			criteria.setToDate(null);
			
			if (criteria.getTransactionTypes().isEmpty()) {
				criteria.addAllTransactionTypesExceptInventoryCheck();
			} else {
				criteria.getTransactionTypes().remove("INVENTORY CHECK");
			}
		}		
		
		List<StockCardInventoryReportItem> items = reportDao.getStockCardInventoryReport(criteria);
		
		if (lastInventoryCheck != null) {
			StockCardInventoryReportCriteria inventoryCheckCriteria = new StockCardInventoryReportCriteria();
			inventoryCheckCriteria.setProduct(criteria.getProduct());
			inventoryCheckCriteria.setInventoryCheck(lastInventoryCheck);
			inventoryCheckCriteria.setUnit(criteria.getUnit());
			
			items.addAll(reportDao.getStockCardInventoryReportItem(inventoryCheckCriteria));
		} else if (lastInventoryCorrection != null) {
			StockCardInventoryReportItem item = new StockCardInventoryReportItem();
			item.setPostDate(lastInventoryCorrection.getPostDate());
			item.setTransactionType("INVENTORY CORRECTION");
			item.setCurrentCostOrSellingPrice(lastInventoryCorrection.getCost());
			item.setUnit(lastInventoryCorrection.getUnit());
			item.setAddQuantity(lastInventoryCorrection.getNewQuantity());
			
			items.add(item);
		}
		
		return items;
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
	public SalesByManufacturerReport getManufacturerSalesReport(SalesByManufacturerReportCriteria criteria) {
		SalesByManufacturerReport report = new SalesByManufacturerReport();
		report.setItems(reportDao.searchSalesByManufacturerReportItems(criteria));
		return report; 
	}

	@Override
	public StockOfftakeReport getStockOfftakeReport(StockOfftakeReportCriteria criteria) {
		StockOfftakeReport report = new StockOfftakeReport();
		report.setItems(reportDao.searchStockOfftakeReportItems(criteria));
		return report;
	}

}