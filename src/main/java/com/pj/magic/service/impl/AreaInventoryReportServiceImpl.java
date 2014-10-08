package com.pj.magic.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pj.magic.dao.AreaInventoryReportDao;
import com.pj.magic.model.AreaInventoryReport;
import com.pj.magic.service.AreaInventoryReportService;

@Service
public class AreaInventoryReportServiceImpl implements AreaInventoryReportService {

	@Autowired private AreaInventoryReportDao areaInventoryReportDao;
	
	@Override
	public List<AreaInventoryReport> getAllAreaInventoryReports() {
		return areaInventoryReportDao.getAll();
	}

	@Override
	public void save(AreaInventoryReport areaInventoryReport) {
		areaInventoryReportDao.save(areaInventoryReport);
	}

	@Override
	public void delete(AreaInventoryReport inventoryCheck) {
		// TODO Auto-generated method stub
		
	}

}
