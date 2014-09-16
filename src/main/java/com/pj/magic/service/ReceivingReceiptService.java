package com.pj.magic.service;

import java.util.List;

import com.pj.magic.model.ReceivingReceipt;
import com.pj.magic.model.ReceivingReceiptItem;

public interface ReceivingReceiptService {

	List<ReceivingReceipt> getAllReceivingReceipts();
	
	void save(ReceivingReceipt receivingReceipt);
	
	void save(ReceivingReceiptItem receivingReceiptItem);
	
	ReceivingReceipt getReceivingReceipt(long id);

	List<ReceivingReceipt> getAllNonPostedReceivingReceipts();
	
}
