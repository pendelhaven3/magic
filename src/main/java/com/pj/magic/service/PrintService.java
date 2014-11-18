package com.pj.magic.service;

import java.util.List;

import com.pj.magic.model.InventoryCheck;
import com.pj.magic.model.PricingScheme;
import com.pj.magic.model.Product;
import com.pj.magic.model.PurchaseOrder;
import com.pj.magic.model.ReceivingReceipt;
import com.pj.magic.model.SalesInvoice;
import com.pj.magic.model.StockQuantityConversion;

public interface PrintService {

	void print(SalesInvoice salesInvoice);

	void print(PurchaseOrder purchaseOrder);
	
	void print(ReceivingReceipt receivingReceipt, boolean includeDiscountDetails);
	
	void print(StockQuantityConversion stockQuantityConversion);

	List<String> generateReportAsString(PurchaseOrder purchaseOrder);
	
	void print(PricingScheme pricingScheme, List<Product> products);
	
	List<String> generateReportAsString(PricingScheme pricingScheme, List<Product> products);
	
	void print(List<String> printPages);

	void printForCondensedFont(List<String> printPages);

	List<String> generateReportAsString(SalesInvoice salesInvoice);

	List<String> generateReportAsString(ReceivingReceipt receivingReceipt, boolean includeDiscountDetails);

	List<String> generateReportAsString(StockQuantityConversion stockQuantityConversion);

	List<String> generateReportAsString(InventoryCheck inventoryCheck, boolean beginningInventory);

	void print(InventoryCheck inventoryCheck, boolean beginningInventory);
	
	void printBirForm(SalesInvoice salesInvoice);
	
}
