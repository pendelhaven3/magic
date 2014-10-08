package com.pj.magic.dao;

import java.util.List;

import com.pj.magic.model.AreaInventoryReport;
import com.pj.magic.model.AreaInventoryReportItem;

public interface AreaInventoryReportItemDao {

	void save(AreaInventoryReportItem item);
	
	List<AreaInventoryReportItem> findAllByAreaInventoryReport(AreaInventoryReport areaInventoryReport);

	void delete(AreaInventoryReportItem item);

	void deleteAllByAreaInventoryReport(AreaInventoryReport areaInventoryReport);
	
}
