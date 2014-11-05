package com.pj.magic.dao;

import java.util.List;

import com.pj.magic.model.Product;
import com.pj.magic.model.ProductCanvassItem;
import com.pj.magic.model.ReceivingReceipt;
import com.pj.magic.model.util.ReceivingReceiptSearchCriteria;

public interface ReceivingReceiptDao {

	ReceivingReceipt get(long id);

	void save(ReceivingReceipt receivingReceipt);

	List<ReceivingReceipt> getAll();

	List<ProductCanvassItem> getProductCanvassItems(Product product);

	List<ReceivingReceipt> search(ReceivingReceiptSearchCriteria criteria);
	
}
