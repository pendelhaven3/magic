package com.pj.magic.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pj.magic.dao.CustomerDao;
import com.pj.magic.dao.ProductDao;
import com.pj.magic.dao.PurchaseOrderDao;
import com.pj.magic.dao.PurchaseOrderItemDao;
import com.pj.magic.dao.UserDao;
import com.pj.magic.model.PurchaseOrder;
import com.pj.magic.model.PurchaseOrderItem;
import com.pj.magic.service.PurchaseOrderService;
import com.pj.magic.service.SalesInvoiceService;

@Service
public class PurchaseOrderServiceImpl implements PurchaseOrderService {

	@Autowired private PurchaseOrderDao purchaseOrderDao;
	@Autowired private PurchaseOrderItemDao purchaseOrderItemDao;
	@Autowired private ProductDao productDao;
	@Autowired private SalesInvoiceService salesInvoiceService;
	@Autowired private CustomerDao customerDao;
	@Autowired private UserDao userDao;
	
	@Transactional
	@Override
	public void save(PurchaseOrder purchaseOrder) {
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
	public void post(PurchaseOrder purchaseOrder) {
//		PurchaseOrder updated = getPurchaseOrder(purchaseOrder.getId());
//		for (PurchaseOrderItem item : updated.getItems()) {
//			// [PJ 08/06/2014] Do not update product quantity inside sales requisition object
//			// because it has to be "rolled back" manually when an exception happens during posting
//			Product product = productDao.get(item.getProduct().getId());
//			if (!product.hasAvailableUnitQuantity(item.getUnit(), item.getQuantity())) {
//				throw new NotEnoughStocksException(item);
//			} else {
//				product.subtractUnitQuantity(item.getUnit(), item.getQuantity());
//				productDao.updateAvailableQuantities(product);
//			}
//		}
//		updated.setPosted(true);
//		purchaseOrderDao.save(updated);
//
//		SalesInvoice salesInvoice = updated.createSalesInvoice();
//		salesInvoiceService.save(salesInvoice);
//		return salesInvoice;
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
	
}
