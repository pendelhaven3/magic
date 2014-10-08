package com.pj.magic.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pj.magic.dao.InventoryCheckDao;
import com.pj.magic.model.InventoryCheck;
import com.pj.magic.service.InventoryCheckService;

@Service
public class InventoryCheckServiceImpl implements InventoryCheckService {

	@Autowired private InventoryCheckDao inventoryCheckDao;
	
	@Override
	public List<InventoryCheck> getAllInventoryCheck() {
		return inventoryCheckDao.getAll();
	}

	@Override
	public void delete(InventoryCheck salesRequisition) {
		// TODO Auto-generated method stub
		
	}

}
