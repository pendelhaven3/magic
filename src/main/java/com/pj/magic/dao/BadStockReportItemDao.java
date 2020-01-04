package com.pj.magic.dao;

import java.util.List;

import com.pj.magic.model.BadStockReport;
import com.pj.magic.model.BadStockReportItem;

public interface BadStockReportItemDao {

	void save(BadStockReportItem item);
	
	List<BadStockReportItem> findAllByBadStockReport(BadStockReport badStockReport);

	void delete(BadStockReportItem item);

}