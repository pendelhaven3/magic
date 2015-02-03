package com.pj.magic.service;

import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.pj.magic.model.SalesInvoice;

public interface ExcelService {

	XSSFWorkbook generateSpreadsheet(SalesInvoice salesInvoice);
	
}