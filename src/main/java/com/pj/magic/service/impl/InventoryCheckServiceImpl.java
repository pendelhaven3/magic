package com.pj.magic.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pj.magic.dao.InventoryCheckDao;
import com.pj.magic.dao.ProductDao;
import com.pj.magic.model.InventoryCheck;
import com.pj.magic.model.InventoryCheckSummaryItem;
import com.pj.magic.model.Product;
import com.pj.magic.service.InventoryCheckService;

@Service
public class InventoryCheckServiceImpl implements InventoryCheckService {

	@Autowired private InventoryCheckDao inventoryCheckDao;
	@Autowired private ProductDao productDao;
	
	@Override
	public List<InventoryCheck> getAllInventoryChecks() {
		return inventoryCheckDao.getAll();
	}

	@Override
	public void delete(InventoryCheck inventoryCheck) {
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

	@Transactional
	@Override
	public void post(InventoryCheck inventoryCheck) {
		InventoryCheck updated = getInventoryCheck(inventoryCheck.getId());
		for (InventoryCheckSummaryItem item : updated.getSummaryItems()) {
			Product product = productDao.get(item.getProduct().getId());
			product.addUnitQuantity(item.getUnit(), item.getQuantityDifference());
			productDao.updateAvailableQuantities(product);
		}
		updated.setPosted(true);
		inventoryCheckDao.save(updated);
	}

}
