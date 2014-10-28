package com.pj.magic.dao;

import java.util.List;

import com.pj.magic.model.Product;
import com.pj.magic.model.StockCardInventoryReportItem;

public interface ReportDao {

	List<StockCardInventoryReportItem> getStockCardInventoryReport(Product product);
	
}
