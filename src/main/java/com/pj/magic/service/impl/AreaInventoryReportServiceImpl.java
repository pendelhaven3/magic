package com.pj.magic.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pj.magic.dao.AreaInventoryReportDao;
import com.pj.magic.dao.AreaInventoryReportItemDao;
import com.pj.magic.dao.ProductDao;
import com.pj.magic.model.AreaInventoryReport;
import com.pj.magic.model.AreaInventoryReportItem;
import com.pj.magic.model.InventoryCheck;
import com.pj.magic.service.AreaInventoryReportService;

@Service
public class AreaInventoryReportServiceImpl implements AreaInventoryReportService {

	@Autowired private AreaInventoryReportDao areaInventoryReportDao;
	@Autowired private AreaInventoryReportItemDao areaInventoryReportItemDao;
	@Autowired private ProductDao productDao;
	
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

	@Override
	public AreaInventoryReport getAreaInventoryReport(long id) {
		AreaInventoryReport areaInventoryReport = areaInventoryReportDao.get(id);
		loadChildrenDetails(areaInventoryReport);
		return areaInventoryReport;
	}

	private void loadChildrenDetails(AreaInventoryReport areaInventoryReport) {
		List<AreaInventoryReportItem> items = areaInventoryReportItemDao.findAllByAreaInventoryReport(areaInventoryReport);
		for (AreaInventoryReportItem item : items) {
			item.setProduct(productDao.get(item.getProduct().getId()));
		}
		areaInventoryReport.setItems(items);
	}

	@Override
	public void delete(AreaInventoryReportItem item) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void save(AreaInventoryReportItem item) {
		areaInventoryReportItemDao.save(item);
	}

	@Override
	public AreaInventoryReport findByInventoryCheckAndReportNumber(
			InventoryCheck inventoryCheck, int reportNumber) {
		return areaInventoryReportDao.findByInventoryCheckAndReportNumber(inventoryCheck, reportNumber);
	}

}
