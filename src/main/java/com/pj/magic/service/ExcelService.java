package com.pj.magic.service;

import java.io.IOException;

import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.pj.magic.model.PricingScheme;
import com.pj.magic.model.PurchaseOrder;
import com.pj.magic.model.SalesInvoice;

public interface ExcelService {

	XSSFWorkbook generateSpreadsheet(SalesInvoice salesInvoice);

	XSSFWorkbook generateSpreadsheet(PurchaseOrder purchaseOrder);

	XSSFWorkbook generateSpreadsheet(PricingScheme pricingScheme) throws IOException;
	
}