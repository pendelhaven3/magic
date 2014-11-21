package com.pj.magic.service;

import java.util.List;

import com.pj.magic.model.AdjustmentIn;
import com.pj.magic.model.AdjustmentOut;
import com.pj.magic.model.AreaInventoryReport;
import com.pj.magic.model.InventoryCheck;
import com.pj.magic.model.PricingScheme;
import com.pj.magic.model.Product;
import com.pj.magic.model.PurchaseOrder;
import com.pj.magic.model.ReceivingReceipt;
import com.pj.magic.model.SalesInvoice;
import com.pj.magic.model.SalesInvoiceReport;
import com.pj.magic.model.StockQuantityConversion;
import com.pj.magic.model.util.InventoryCheckReportType;

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

	List<String> generateReportAsString(InventoryCheck inventoryCheck, InventoryCheckReportType reportType);

	void print(InventoryCheck inventoryCheck, InventoryCheckReportType reportType);
	
	void printBirForm(SalesInvoice salesInvoice);

	List<String> generateReportAsString(AreaInventoryReport areaInventoryReport);

	void print(AreaInventoryReport areaInventoryReport);

	List<String> generateReportAsString(SalesInvoiceReport salesInvoiceReport);

	void print(SalesInvoiceReport salesInvoiceReport);

	List<String> generateReportAsString(AdjustmentOut adjustmentOut);

	void print(AdjustmentOut adjustmentOut);

	List<String> generateReportAsString(AdjustmentIn adjustmentIn);

	void print(AdjustmentIn adjustmentIn);
	
}
