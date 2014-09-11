package com.pj.magic.dao;

import java.util.List;

import com.pj.magic.model.ReceivingReceipt;

public interface ReceivingReceiptDao {

	ReceivingReceipt get(long id);

	void save(ReceivingReceipt receivingReceipt);

	List<ReceivingReceipt> search(ReceivingReceipt criteria);

	List<ReceivingReceipt> getAll();
	
}
