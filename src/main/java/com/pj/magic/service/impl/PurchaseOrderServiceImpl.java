package com.pj.magic.service.impl;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pj.magic.dao.ProductDao;
import com.pj.magic.dao.PurchaseOrderDao;
import com.pj.magic.dao.PurchaseOrderItemDao;
import com.pj.magic.model.PurchaseOrder;
import com.pj.magic.model.PurchaseOrderItem;
import com.pj.magic.model.ReceivingReceipt;
import com.pj.magic.service.LoginService;
import com.pj.magic.service.PurchaseOrderService;
import com.pj.magic.service.ReceivingReceiptService;

@Service
public class PurchaseOrderServiceImpl implements PurchaseOrderService {

	@Autowired private PurchaseOrderDao purchaseOrderDao;
	@Autowired private PurchaseOrderItemDao purchaseOrderItemDao;
	@Autowired private ProductDao productDao;
	@Autowired private ReceivingReceiptService receivingReceiptService;
	@Autowired private LoginService loginService;
	
	@Transactional
	@Override
	public void save(PurchaseOrder purchaseOrder) {
		boolean inserting = (purchaseOrder.getId() == null);
		if (inserting) {
			purchaseOrder.setCreatedBy(loginService.getLoggedInUser());
		}
		purchaseOrderDao.save(purchaseOrder);
	}

	@Override
	public PurchaseOrder getPurchaseOrder(long id) {
		PurchaseOrder purchaseOrder = purchaseOrderDao.get(id);
		loadPurchaseOrderDetails(purchaseOrder);
		return purchaseOrder;
	}
	
	private void loadPurchaseOrderDetails(PurchaseOrder purchaseOrder) {
		purchaseOrder.setItems(purchaseOrderItemDao.findAllByPurchaseOrder(purchaseOrder));
		for (PurchaseOrderItem item : purchaseOrder.getItems()) {
			item.setProduct(productDao.get(item.getProduct().getId()));
		}
	}

	@Transactional
	@Override
	public void save(PurchaseOrderItem item) {
		purchaseOrderItemDao.save(item);
	}

	@Transactional
	@Override
	public void delete(PurchaseOrderItem item) {
		purchaseOrderItemDao.delete(item);
	}

	@Transactional
	@Override
	public void delete(PurchaseOrder purchaseOrder) {
		purchaseOrderItemDao.deleteAllByPurchaseOrder(purchaseOrder);
		purchaseOrderDao.delete(purchaseOrder);
	}

	@Transactional
	@Override
	public ReceivingReceipt post(PurchaseOrder purchaseOrder) {
		PurchaseOrder updated = getPurchaseOrder(purchaseOrder.getId());
		updated.setPosted(true);
		updated.setPostDate(new Date());
		purchaseOrderDao.save(updated);

		ReceivingReceipt receivingReceipt = updated.createReceivingReceipt();
		receivingReceipt.setReceivedBy(loginService.getLoggedInUser());
		receivingReceiptService.save(receivingReceipt);
		return receivingReceipt;
	}

	@Override
	public List<PurchaseOrder> getAllNonPostedPurchaseOrders() {
		PurchaseOrder criteria = new PurchaseOrder();
		criteria.setPosted(false);
		
		List<PurchaseOrder> purchaseOrders = purchaseOrderDao.search(criteria);
		for (PurchaseOrder purchaseOrder : purchaseOrders) {
			loadPurchaseOrderDetails(purchaseOrder);
		}
		return purchaseOrders;
	}

	@Transactional
	@Override
	public void order(PurchaseOrder purchaseOrder) {
		purchaseOrder.setOrdered(true);
		purchaseOrder.setOrderDate(new Date());
		purchaseOrderDao.save(purchaseOrder);
		purchaseOrderItemDao.updateAllByPurchaseOrderAsOrdered(purchaseOrder);
	}
	
}
