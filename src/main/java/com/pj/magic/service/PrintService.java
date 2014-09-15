package com.pj.magic.service;

import com.pj.magic.model.PurchaseOrder;
import com.pj.magic.model.SalesInvoice;

public interface PrintService {

	void print(SalesInvoice salesInvoice);

	void print(PurchaseOrder purchaseOrder);
	
}
