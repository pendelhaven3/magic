package com.pj.magic.service;

import java.util.List;

import com.pj.magic.model.AreaInventoryReport;
import com.pj.magic.model.AreaInventoryReportItem;

public interface AreaInventoryReportService {

	List<AreaInventoryReport> getAllAreaInventoryReports();

	void save(AreaInventoryReport areaInventoryReport);

	void delete(AreaInventoryReport inventoryCheck);

	AreaInventoryReport getAreaInventoryReport(long id);

	void delete(AreaInventoryReportItem item);

	void save(AreaInventoryReportItem item);
	
}
