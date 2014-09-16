package com.pj.magic.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.pj.magic.dao.AdjustmentOutDao;
import com.pj.magic.dao.AdjustmentOutItemDao;
import com.pj.magic.dao.ProductDao;
import com.pj.magic.exception.NotEnoughStocksException;
import com.pj.magic.model.AdjustmentOut;
import com.pj.magic.model.AdjustmentOutItem;
import com.pj.magic.model.Product;
import com.pj.magic.service.AdjustmentOutService;

@Service
public class AdjustmentOutServiceImpl implements AdjustmentOutService {

	@Autowired private AdjustmentOutDao adjustmentOutDao;
	@Autowired private AdjustmentOutItemDao adjustmentOutItemDao;
	@Autowired private ProductDao productDao;
	
	@Transactional
	@Override
	public void save(AdjustmentOut adjustmentOut) {
		adjustmentOutDao.save(adjustmentOut);
	}

	@Override
	public AdjustmentOut getAdjustmentOut(long id) {
		AdjustmentOut adjustmentOut = adjustmentOutDao.get(id);
		loadAdjustmentOutDetails(adjustmentOut);
		return adjustmentOut;
	}
	
	private void loadAdjustmentOutDetails(AdjustmentOut adjustmentOut) {
		adjustmentOut.setItems(adjustmentOutItemDao.findAllByAdjustmentOut(adjustmentOut));
		for (AdjustmentOutItem item : adjustmentOut.getItems()) {
			item.setProduct(productDao.get(item.getProduct().getId()));
		}
	}

	@Transactional
	@Override
	public void save(AdjustmentOutItem item) {
		adjustmentOutItemDao.save(item);
	}

	@Transactional
	@Override
	public void delete(AdjustmentOutItem item) {
		adjustmentOutItemDao.delete(item);
	}

	@Transactional
	@Override
	public void delete(AdjustmentOut adjustmentOut) {
		adjustmentOutItemDao.deleteAllByAdjustmentOut(adjustmentOut);
		adjustmentOutDao.delete(adjustmentOut);
	}

	@Transactional(propagation=Propagation.REQUIRES_NEW)
	@Override
	public void post(AdjustmentOut adjustmentOut) throws NotEnoughStocksException {
		AdjustmentOut updated = getAdjustmentOut(adjustmentOut.getId());
		for (AdjustmentOutItem item : updated.getItems()) {
			Product product = productDao.get(item.getProduct().getId());
			if (!product.hasAvailableUnitQuantity(item.getUnit(), item.getQuantity())) {
				throw new NotEnoughStocksException(item);
			} else {
				product.subtractUnitQuantity(item.getUnit(), item.getQuantity());
				productDao.updateAvailableQuantities(product);
			}
		}
		updated.setPosted(true);
		adjustmentOutDao.save(updated);
	}

	@Override
	public List<AdjustmentOut> getAllNonPostedAdjustmentOuts() {
		AdjustmentOut criteria = new AdjustmentOut();
		criteria.setPosted(false);
		
		List<AdjustmentOut> adjustmentOuts = adjustmentOutDao.search(criteria);
		for (AdjustmentOut adjustmentOut : adjustmentOuts) {
			loadAdjustmentOutDetails(adjustmentOut);
		}
		return adjustmentOuts;
	}
	
}
