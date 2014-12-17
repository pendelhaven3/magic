package com.pj.magic.service.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pj.magic.Constants;
import com.pj.magic.dao.ProductDao;
import com.pj.magic.dao.ReceivingReceiptDao;
import com.pj.magic.dao.ReceivingReceiptItemDao;
import com.pj.magic.model.Product;
import com.pj.magic.model.ReceivingReceipt;
import com.pj.magic.model.ReceivingReceiptItem;
import com.pj.magic.model.search.ReceivingReceiptSearchCriteria;
import com.pj.magic.service.LoginService;
import com.pj.magic.service.ReceivingReceiptService;

// TODO: No need to fetch product details for some cases

@Service
public class ReceivingReceiptServiceImpl implements ReceivingReceiptService {

	@Autowired private ReceivingReceiptDao receivingReceiptDao;
	@Autowired private ReceivingReceiptItemDao receivingReceiptItemDao;
	@Autowired private ProductDao productDao;
	@Autowired private LoginService loginService;
	
	@Transactional
	@Override
	public void save(ReceivingReceipt receivingReceipt) {
		boolean inserting = (receivingReceipt.getId() == null);
		receivingReceiptDao.save(receivingReceipt);
		if (inserting) {
			for (ReceivingReceiptItem item : receivingReceipt.getItems()) {
				receivingReceiptItemDao.save(item);
			}
		}
	}

	@Override
	public ReceivingReceipt getReceivingReceipt(long id) {
		ReceivingReceipt receivingReceipt = receivingReceiptDao.get(id);
		loadReceivingReceiptDetails(receivingReceipt);
		return receivingReceipt;
	}
	
	private void loadReceivingReceiptDetails(ReceivingReceipt receivingReceipt) {
		receivingReceipt.setItems(receivingReceiptItemDao.findAllByReceivingReceipt(receivingReceipt));
		for (ReceivingReceiptItem item : receivingReceipt.getItems()) {
			item.setProduct(productDao.get(item.getProduct().getId()));
		}
	}

	@Transactional
	@Override
	public void save(ReceivingReceiptItem item) {
		receivingReceiptItemDao.save(item);
	}

	@Override
	public List<ReceivingReceipt> getNewReceivingReceipts() {
		ReceivingReceiptSearchCriteria criteria = new ReceivingReceiptSearchCriteria();
		criteria.setPosted(false);
		criteria.setCancelled(false);
		
		return search(criteria);
	}

	@Transactional
	@Override
	public void post(ReceivingReceipt receivingReceipt) {
		ReceivingReceipt updated = getReceivingReceipt(receivingReceipt.getId());
		BigDecimal costMultipler = Constants.ONE;
		if (!updated.isVatInclusive()) {
			costMultipler = Constants.ONE.add(updated.getVatRate());
		}
		
		for (ReceivingReceiptItem item : updated.getItems()) {
			Product product = productDao.get(item.getProduct().getId());
			BigDecimal currentCost = product.getFinalCost(item.getUnit());
			
			product.setGrossCost(item.getUnit(), 
					item.getCost().multiply(costMultipler).setScale(2, RoundingMode.HALF_UP));
			product.setFinalCost(item.getUnit(), 
					item.getFinalCost().multiply(costMultipler).setScale(2, RoundingMode.HALF_UP));
			if (item.getProduct().getUnits().size() > 1) {
				product.autoCalculateCostsOfSmallerUnits(item.getUnit());
			}
			productDao.updateCosts(product);
			
			product.addUnitQuantity(item.getUnit(), item.getQuantity());
			productDao.updateAvailableQuantities(product);
			
			item.setCurrentCost(currentCost);
			receivingReceiptItemDao.save(item);
		}
		
		updated.setPosted(true);
		updated.setPostDate(new Date());
		updated.setPostedBy(loginService.getLoggedInUser());
		receivingReceiptDao.save(updated);
	}

	public void setProductDao(ProductDao productDao) {
		this.productDao = productDao;
	}
	
	public void setReceivingReceiptItemDao(ReceivingReceiptItemDao receivingReceiptItemDao) {
		this.receivingReceiptItemDao = receivingReceiptItemDao;
	}

	public void setReceivingReceiptDao(ReceivingReceiptDao receivingReceiptDao) {
		this.receivingReceiptDao = receivingReceiptDao;
	}

	@Override
	public List<ReceivingReceipt> search(ReceivingReceiptSearchCriteria criteria) {
		List<ReceivingReceipt> receivingReceipts = receivingReceiptDao.search(criteria);
		for (ReceivingReceipt purchaseOrder : receivingReceipts) {
			loadReceivingReceiptDetails(purchaseOrder);
		}
		return receivingReceipts;
	}

	@Transactional
	@Override
	public void cancel(ReceivingReceipt receivingReceipt) {
		ReceivingReceipt updated = receivingReceiptDao.get(receivingReceipt.getId());
		updated.setCancelled(true);
		updated.setCancelDate(new Date());
		updated.setCancelledBy(loginService.getLoggedInUser());
		receivingReceiptDao.save(updated);
	}
	
}