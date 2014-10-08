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
	public List<InventoryCheck> getAllInventoryChecks() {
		return inventoryCheckDao.getAll();
	}

	@Override
	public void delete(InventoryCheck inventoryCheck) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void save(InventoryCheck inventoryCheck) {
		inventoryCheckDao.save(inventoryCheck);
	}

	@Override
	public InventoryCheck getNonPostedInventoryCheck() {
		List<InventoryCheck> inventoryChecks = inventoryCheckDao.search(new InventoryCheck());
		switch (inventoryChecks.size()) {
		case 0:
			return null;
		case 1:
			return inventoryChecks.get(0);
		default:
			throw new RuntimeException("There should only be one non-posted inventory check at any one time");
		}
	}

	@Override
	public InventoryCheck getInventoryCheck(long id) {
		InventoryCheck inventoryCheck = inventoryCheckDao.get(id);
		inventoryCheck.setSummaryItems(inventoryCheckDao.getSummaryItems(inventoryCheck));
		return inventoryCheck;
	}

}
