package com.pj.magic.service;

import java.util.List;

import com.pj.magic.model.AreaInventoryReport;

public interface AreaInventoryReportService {

	List<AreaInventoryReport> getAllAreaInventoryReports();

	void save(AreaInventoryReport areaInventoryReport);

	void delete(AreaInventoryReport inventoryCheck);
	
}
