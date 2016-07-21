package com.pj.magic.service.impl;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pj.magic.dao.ProductDao;
import com.pj.magic.dao.SystemDao;
import com.pj.magic.model.InventoryCorrection;
import com.pj.magic.model.Product;
import com.pj.magic.repository.InventoryCorrectionRepository;
import com.pj.magic.service.InventoryCorrectionService;
import com.pj.magic.service.LoginService;

@Service
public class InventoryCorrectionServiceImpl implements InventoryCorrectionService {

	@Autowired private InventoryCorrectionRepository inventoryCorrectionRepository;
	@Autowired private SystemDao systemDao;
	@Autowired private ProductDao productDao;
	@Autowired private LoginService loginService;
	
	@Override
	public List<InventoryCorrection> getAllInventoryCorrections() {
		return inventoryCorrectionRepository.getAll();
	}

	@Override
	public InventoryCorrection getInventoryCorrection(long id) {
		return inventoryCorrectionRepository.get(id);
	}

	@Transactional
	@Override
	public void save(InventoryCorrection inventoryCorrection) {
		inventoryCorrection.setPostDate(systemDao.getCurrentDateTime());
		inventoryCorrection.setPostedBy(loginService.getLoggedInUser());
		inventoryCorrection.setCost(inventoryCorrection.getProduct().getFinalCost(inventoryCorrection.getUnit()));
		inventoryCorrectionRepository.save(inventoryCorrection);
		
		Product product = productDao.get(inventoryCorrection.getProduct().getId());
		product.setUnitQuanty(inventoryCorrection.getUnit(), inventoryCorrection.getNewQuantity());
		productDao.updateAvailableQuantities(product);
	}

}
