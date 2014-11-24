package com.pj.magic.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pj.magic.dao.ReportDao;
import com.pj.magic.model.StockCardInventoryReportItem;
import com.pj.magic.model.search.StockCardInventoryReportSearchCriteria;
import com.pj.magic.service.ReportService;

@Service
public class ReportServiceImpl implements ReportService {

	@Autowired private ReportDao reportDao;
	
	@Override
	public List<StockCardInventoryReportItem> getStockCardInventoryReport(
			StockCardInventoryReportSearchCriteria criteria) {
		return reportDao.getStockCardInventoryReport(criteria);
	}

}
