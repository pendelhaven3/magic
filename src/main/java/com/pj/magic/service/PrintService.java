package com.pj.magic.service;

import java.util.List;

import com.pj.magic.model.PurchaseOrder;
import com.pj.magic.model.ReceivingReceipt;
import com.pj.magic.model.SalesInvoice;

public interface PrintService {

	void print(SalesInvoice salesInvoice);

	void print(PurchaseOrder purchaseOrder);
	
	void print(ReceivingReceipt receivingReceipt, boolean includeDiscountDetails);

	List<String> generateReportAsString(PurchaseOrder purchaseOrder);
	
}
