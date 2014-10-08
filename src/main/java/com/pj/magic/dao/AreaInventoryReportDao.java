package com.pj.magic.dao;

import java.util.List;

import com.pj.magic.model.AreaInventoryReport;

public interface AreaInventoryReportDao {

	AreaInventoryReport get(long id);

	void save(AreaInventoryReport areaInventoryReport);

	List<AreaInventoryReport> getAll();
	
}
