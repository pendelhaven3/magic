package com.pj.magic.service;

import java.util.List;

import com.pj.magic.model.Product;
import com.pj.magic.model.StockCardInventoryReportItem;

public interface ReportService {

	List<StockCardInventoryReportItem> getStockCardInventoryReport(Product product);
	
}
