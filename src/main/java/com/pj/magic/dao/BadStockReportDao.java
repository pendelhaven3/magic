package com.pj.magic.dao;

import java.util.List;

import com.pj.magic.model.BadStockReport;
import com.pj.magic.model.search.BadStockReportSearchCriteria;

public interface BadStockReportDao {

	List<BadStockReport> search(BadStockReportSearchCriteria criteria);

	void save(BadStockReport badStockReport);

	BadStockReport get(Long id);

}