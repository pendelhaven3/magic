package com.pj.magic.service;

import java.util.List;

import com.pj.magic.model.BadStockReport;
import com.pj.magic.model.BadStockReportItem;
import com.pj.magic.model.search.BadStockReportSearchCriteria;

public interface BadStockReportService {

    List<BadStockReport> getAllUnpostedBadStockReports();

	void save(BadStockReport badStockReport);

	BadStockReport getBadStockReport(Long id);

	void save(BadStockReportItem item);

	void delete(BadStockReportItem item);

	void post(BadStockReport badStockReport);

	List<BadStockReport> searchBadStockReports(BadStockReportSearchCriteria criteria);

}
