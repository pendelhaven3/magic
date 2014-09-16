package com.pj.magic.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pj.magic.dao.ProductDao;
import com.pj.magic.dao.ReceivingReceiptDao;
import com.pj.magic.dao.ReceivingReceiptItemDao;
import com.pj.magic.model.ReceivingReceipt;
import com.pj.magic.model.ReceivingReceiptItem;
import com.pj.magic.service.ReceivingReceiptService;
import com.pj.magic.service.SalesInvoiceService;

@Service
public class ReceivingReceiptServiceImpl implements ReceivingReceiptService {

	@Autowired private ReceivingReceiptDao receivingReceiptDao;
	@Autowired private ReceivingReceiptItemDao receivingReceiptItemDao;
	@Autowired private SalesInvoiceService salesInvoiceService;
	@Autowired private ProductDao productDao;
	
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
	public List<ReceivingReceipt> getAllReceivingReceipts() {
		return receivingReceiptDao.getAll();
	}

	@Override
	public List<ReceivingReceipt> getAllNonPostedReceivingReceipts() {
		ReceivingReceipt criteria = new ReceivingReceipt();
		criteria.setPosted(false);
		
		List<ReceivingReceipt> receivingReceipts = receivingReceiptDao.search(criteria);
		for (ReceivingReceipt purchaseOrder : receivingReceipts) {
			loadReceivingReceiptDetails(purchaseOrder);
		}
		return receivingReceipts;
	}

}
