package com.pj.magic.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.pj.magic.dao.AdjustmentInDao;
import com.pj.magic.dao.AdjustmentInItemDao;
import com.pj.magic.dao.ProductDao;
import com.pj.magic.model.AdjustmentIn;
import com.pj.magic.model.AdjustmentInItem;
import com.pj.magic.model.Product;
import com.pj.magic.service.AdjustmentInService;

@Service
public class AdjustmentInServiceImpl implements AdjustmentInService {

	@Autowired private AdjustmentInDao adjustmentInDao;
	@Autowired private AdjustmentInItemDao adjustmentInItemDao;
	@Autowired private ProductDao productDao;
	
	@Transactional
	@Override
	public void save(AdjustmentIn adjustmentIn) {
		adjustmentInDao.save(adjustmentIn);
	}

	@Override
	public AdjustmentIn getAdjustmentIn(long id) {
		AdjustmentIn adjustmentIn = adjustmentInDao.get(id);
		loadAdjustmentInDetails(adjustmentIn);
		return adjustmentIn;
	}
	
	private void loadAdjustmentInDetails(AdjustmentIn adjustmentIn) {
		adjustmentIn.setItems(adjustmentInItemDao.findAllByAdjustmentIn(adjustmentIn));
		for (AdjustmentInItem item : adjustmentIn.getItems()) {
			item.setProduct(productDao.get(item.getProduct().getId()));
		}
	}

	@Transactional
	@Override
	public void save(AdjustmentInItem item) {
		adjustmentInItemDao.save(item);
	}

	@Transactional
	@Override
	public void delete(AdjustmentInItem item) {
		adjustmentInItemDao.delete(item);
	}

	@Transactional
	@Override
	public void delete(AdjustmentIn adjustmentIn) {
		adjustmentInItemDao.deleteAllByAdjustmentIn(adjustmentIn);
		adjustmentInDao.delete(adjustmentIn);
	}

	@Transactional(propagation=Propagation.REQUIRES_NEW)
	@Override
	public void post(AdjustmentIn adjustmentIn) {
		AdjustmentIn updated = getAdjustmentIn(adjustmentIn.getId());
		for (AdjustmentInItem item : updated.getItems()) {
			Product product = productDao.get(item.getProduct().getId());
			product.addUnitQuantity(item.getUnit(), item.getQuantity());
			productDao.updateAvailableQuantities(product);
		}
		updated.setPosted(true);
		adjustmentInDao.save(updated);
	}

	@Override
	public List<AdjustmentIn> getAllNonPostedAdjustmentIns() {
		AdjustmentIn criteria = new AdjustmentIn();
		criteria.setPosted(false);
		
		List<AdjustmentIn> adjustmentIns = adjustmentInDao.search(criteria);
		for (AdjustmentIn adjustmentIn : adjustmentIns) {
			loadAdjustmentInDetails(adjustmentIn);
		}
		return adjustmentIns;
	}
	
}
