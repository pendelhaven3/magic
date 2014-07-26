package com.pj.magic.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pj.magic.dao.ProductDao;
import com.pj.magic.dao.SalesRequisitionDao;
import com.pj.magic.dao.SalesRequisitionItemDao;
import com.pj.magic.model.SalesRequisition;
import com.pj.magic.model.SalesRequisitionItem;
import com.pj.magic.service.SalesRequisitionService;

@Service
public class SalesRequisitionServiceImpl implements SalesRequisitionService {

	@Autowired private SalesRequisitionDao salesRequisitionDao;
	@Autowired private SalesRequisitionItemDao salesRequisitionItemDao;
	@Autowired private ProductDao productDao;
	
	@Override
	public List<SalesRequisition> getAllSalesRequisitions() {
		List<SalesRequisition> salesRequisitions = salesRequisitionDao.getAll();
		for (SalesRequisition salesRequisition : salesRequisitions) {
			loadSalesRequisitionItems(salesRequisition);
		}
		return salesRequisitions;
	}

	@Override
	public void save(SalesRequisition salesRequisition) {
		salesRequisitionDao.save(salesRequisition);
	}

	@Override
	public SalesRequisition getSalesRequisition(long id) {
		SalesRequisition salesRequisition = salesRequisitionDao.get(id);
		loadSalesRequisitionItems(salesRequisition);
		return salesRequisition;
	}
	
	private void loadSalesRequisitionItems(SalesRequisition salesRequisition) {
		salesRequisition.setItems(salesRequisitionItemDao.findAllBySalesRequisition(salesRequisition));
		for (SalesRequisitionItem item : salesRequisition.getItems()) {
			item.setProduct(productDao.getProduct(item.getProduct().getId()));
		}
	}

	@Override
	public void save(SalesRequisitionItem item) {
		salesRequisitionItemDao.save(item);
	}

	@Override
	public void delete(SalesRequisitionItem item) {
		salesRequisitionItemDao.delete(item);
	}
	
}
