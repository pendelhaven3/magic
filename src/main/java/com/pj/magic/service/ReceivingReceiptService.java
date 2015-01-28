package com.pj.magic.service;

import java.util.List;

import com.pj.magic.model.ReceivingReceipt;
import com.pj.magic.model.ReceivingReceiptItem;
import com.pj.magic.model.Supplier;
import com.pj.magic.model.search.ReceivingReceiptSearchCriteria;

public interface ReceivingReceiptService {

	void save(ReceivingReceipt receivingReceipt);
	
	void save(ReceivingReceiptItem receivingReceiptItem);
	
	ReceivingReceipt getReceivingReceipt(long id);

	List<ReceivingReceipt> getNewReceivingReceipts();
	
	void post(ReceivingReceipt receivingReceipt);

	List<ReceivingReceipt> search(ReceivingReceiptSearchCriteria criteria);
	
	void cancel(ReceivingReceipt receivingReceipt);

	List<ReceivingReceipt> findAllReceivingReceiptsForPaymentBySupplier(Supplier supplier);
	
}