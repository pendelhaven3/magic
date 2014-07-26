package com.pj.magic.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pj.magic.dao.SalesRequisitionDao;
import com.pj.magic.model.SalesRequisition;
import com.pj.magic.service.SalesRequisitionService;

@Service
public class SalesRequisitionServiceImpl implements SalesRequisitionService {

	@Autowired private SalesRequisitionDao salesRequisitionDao;
	
	@Override
	public List<SalesRequisition> getAllSalesRequisitions() {
		return salesRequisitionDao.getAll();
	}

	@Override
	public void save(SalesRequisition salesRequisition) {
		salesRequisitionDao.save(salesRequisition);
	}

}
