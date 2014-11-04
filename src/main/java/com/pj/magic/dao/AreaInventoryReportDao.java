package com.pj.magic.dao;

import java.util.List;

import com.pj.magic.model.AreaInventoryReport;
import com.pj.magic.model.InventoryCheck;

public interface AreaInventoryReportDao {

	AreaInventoryReport get(long id);

	void save(AreaInventoryReport areaInventoryReport);

	List<AreaInventoryReport> getAll();
	
	AreaInventoryReport findByInventoryCheckAndReportNumber(InventoryCheck inventoryCheck, int reportNumber);
	
}
