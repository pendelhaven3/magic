package com.pj.magic.dao;

import java.util.List;

import com.pj.magic.model.ReceivingReceipt;
import com.pj.magic.model.ReceivingReceiptItem;

public interface ReceivingReceiptItemDao {

	void save(ReceivingReceiptItem item);
	
	List<ReceivingReceiptItem> findAllByReceivingReceipt(ReceivingReceipt receivingReceipt);

}
