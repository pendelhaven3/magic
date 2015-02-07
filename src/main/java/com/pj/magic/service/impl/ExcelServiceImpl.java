package com.pj.magic.service.impl;

import java.util.Date;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pj.magic.dao.SupplierDao;
import com.pj.magic.model.PurchaseOrder;
import com.pj.magic.model.PurchaseOrderItem;
import com.pj.magic.model.SalesInvoice;
import com.pj.magic.model.SalesInvoiceItem;
import com.pj.magic.model.util.CellStyleBuilder;
import com.pj.magic.service.ExcelService;
import com.pj.magic.util.FormatterUtil;

@Service
public class ExcelServiceImpl implements ExcelService {

	@Autowired private SupplierDao supplierDao;
	
	@Override
	public XSSFWorkbook generateSpreadsheet(SalesInvoice salesInvoice) {
		XSSFWorkbook workbook = new XSSFWorkbook();
		Sheet sheet = workbook.createSheet();
		int currentRow = 0;
		CellStyle centered = CellStyleBuilder.createStyle(workbook)
				.setAlignment(CellStyle.ALIGN_CENTER).build();
		CellStyle amountFormat = CellStyleBuilder.createStyle(workbook)
				.setAmountFormat(true).build();
		
		Row row = sheet.createRow(currentRow);
		Cell cell = row.createCell(0);
		cell.setCellValue("JC HARMONY SELLING INC");
		sheet.addMergedRegion(new CellRangeAddress(currentRow, currentRow, 0, 8));
		cell.setCellStyle(centered);
		
		currentRow++;
		
		row = sheet.createRow(currentRow);
		cell = row.createCell(0);
		cell.setCellValue("SALES INVOICE");
		sheet.addMergedRegion(new CellRangeAddress(currentRow, currentRow, 0, 8));
		cell.setCellStyle(centered);
		
		currentRow++;
		currentRow++;
		
		row = sheet.createRow(currentRow);
		row.createCell(0).setCellValue("CUSTOMER:  " + salesInvoice.getCustomer().getName());
		row.createCell(7).setCellValue("SI#");
		row.createCell(8).setCellValue(salesInvoice.getSalesInvoiceNumber());

		currentRow++;
		
		row = sheet.createRow(currentRow);
		row.createCell(0).setCellValue("ADDRESS:  " + salesInvoice.getCustomer().getBusinessAddress());
		row.createCell(7).setCellValue("DATE:");
		
		cell = row.createCell(8);
		cell.setCellValue(FormatterUtil.formatDate(salesInvoice.getTransactionDate()));
		cell.setCellStyle(CellStyleBuilder.createStyle(workbook)
				.setAlignment(CellStyle.ALIGN_RIGHT).build());
		
		currentRow++;
		
		row = sheet.createRow(currentRow);
		row.createCell(0).setCellValue("MODE:  " + salesInvoice.getMode());
		row.createCell(7).setCellValue("PS:");
		row.createCell(8).setCellValue(salesInvoice.getPricingScheme().getId());
		
		currentRow++;
		currentRow++;
		currentRow++;
		currentRow++;
		
		row = sheet.createRow(currentRow);
		cell = row.createCell(0);
		cell.setCellValue("PRODUCT DETAILS");
		sheet.addMergedRegion(new CellRangeAddress(currentRow, currentRow, 0, 4));
		cell.setCellStyle(centered);
		
		cell = row.createCell(5);
		cell.setCellValue("UNIT");
		cell.setCellStyle(centered);
		
		cell = row.createCell(6);
		cell.setCellValue("QTY");
		cell.setCellStyle(centered);
		
		cell = row.createCell(7);
		cell.setCellValue("PRICE");
		cell.setCellStyle(centered);
		
		cell = row.createCell(8);
		cell.setCellValue("AMT");
		cell.setCellStyle(centered);
		
		currentRow++;
		
		for (SalesInvoiceItem item : salesInvoice.getItems()) {
			currentRow++;
			row = sheet.createRow(currentRow);
			row.createCell(0).setCellValue(item.getProduct().getDescription());
			row.createCell(5).setCellValue(item.getUnit());
			row.createCell(6).setCellValue(item.getQuantity());
			cell = row.createCell(7);
			cell.setCellValue(item.getUnitPrice().doubleValue());
			cell.setCellStyle(amountFormat);
			cell = row.createCell(8);
			cell.setCellValue(item.getAmount().doubleValue());
			cell.setCellStyle(amountFormat);
		}
		cell.setCellStyle(CellStyleBuilder.createStyle(workbook)
				.setBorderBottom(CellStyle.BORDER_THIN).setAmountFormat(true).build());
		
		currentRow++;
		currentRow++;
		
		row = sheet.createRow(currentRow);	
		row.createCell(0).setCellValue("TOTAL ITEMS:  " + salesInvoice.getItems().size());
		row.createCell(2).setCellValue("TOTAL QTY:  " + salesInvoice.getTotalQuantity());
		cell = row.createCell(8);
		cell.setCellValue(salesInvoice.getTotalAmount().doubleValue());
		cell.setCellStyle(amountFormat);
		
		currentRow++;
		
		row = sheet.createRow(currentRow);
		row.createCell(7).setCellValue("DISC");
		cell = row.createCell(8);
		cell.setCellValue(salesInvoice.getTotalDiscounts().doubleValue());
		cell.setCellStyle(CellStyleBuilder.createStyle(workbook)
				.setBorderBottom(CellStyle.BORDER_THIN).setAmountFormat(true).build());
		
		currentRow++;
		
		row = sheet.createRow(currentRow);
		row.createCell(7).setCellValue("NET AMT");
		cell = row.createCell(8);
		cell.setCellValue(salesInvoice.getTotalNetAmount().doubleValue());
		cell.setCellStyle(CellStyleBuilder.createStyle(workbook)
				.setBorderBottom(CellStyle.BORDER_DOUBLE).setAmountFormat(true).build());
		
		currentRow++;
		currentRow++;
		
		row = sheet.createRow(currentRow);
		row.createCell(0).setCellValue("ENCODER:  " + salesInvoice.getEncoder());

		currentRow++;
		currentRow++;
		
		row = sheet.createRow(currentRow);
		row.createCell(0).setCellValue("REMARKS:  " + salesInvoice.getRemarks());
		
		sheet.autoSizeColumn(7);
		sheet.autoSizeColumn(8);
		
		return workbook;
	}

	@Override
	public XSSFWorkbook generateSpreadsheet(PurchaseOrder purchaseOrder) {
		purchaseOrder.setSupplier(supplierDao.get(purchaseOrder.getSupplier().getId()));
		
		XSSFWorkbook workbook = new XSSFWorkbook();
		Sheet sheet = workbook.createSheet();
		int currentRow = 0;
		CellStyle centered = CellStyleBuilder.createStyle(workbook)
				.setAlignment(CellStyle.ALIGN_CENTER).build();
		CellStyle amountFormat = CellStyleBuilder.createStyle(workbook)
				.setAmountFormat(true).build();
		
		Row row = sheet.createRow(currentRow);
		Cell cell = row.createCell(0);
		cell.setCellValue("JC HARMONY SELLING INC");
		sheet.addMergedRegion(new CellRangeAddress(currentRow, currentRow, 0, 8));
		cell.setCellStyle(centered);
		
		currentRow++;
		
		row = sheet.createRow(currentRow);
		cell = row.createCell(0);
		cell.setCellValue("PURCHASE ORDER");
		sheet.addMergedRegion(new CellRangeAddress(currentRow, currentRow, 0, 8));
		cell.setCellStyle(centered);
		
		currentRow++;
		currentRow++;
		
		row = sheet.createRow(currentRow);
		row.createCell(0).setCellValue("SUPPLIER: " + purchaseOrder.getSupplier().getName());
		row.createCell(7).setCellValue("PO#");
		row.createCell(8).setCellValue(purchaseOrder.getPurchaseOrderNumber());
		
		currentRow++;
		
		row = sheet.createRow(currentRow);
		row.createCell(0).setCellValue("ADDRESS: " + purchaseOrder.getSupplier().getAddress());
		row.createCell(7).setCellValue("DATE:");
		cell = row.createCell(8);
		cell.setCellValue(FormatterUtil.formatDate(new Date()));
		cell.setCellStyle(CellStyleBuilder.createStyle(workbook)
				.setAlignment(CellStyle.ALIGN_RIGHT).build());

		currentRow++;
		currentRow++;
		
		row = sheet.createRow(currentRow);
		row.createCell(0).setCellValue("FAX: " + purchaseOrder.getSupplier().getFaxNumber());
		
		currentRow++;
		
		row = sheet.createRow(currentRow);
		row.createCell(0).setCellValue("CONTACT: " + purchaseOrder.getSupplier().getContactNumber());

		currentRow++;
		currentRow++;
		
		row = sheet.createRow(currentRow);
		cell = row.createCell(0);
		cell.setCellValue("PRODUCT DETAILS");
		sheet.addMergedRegion(new CellRangeAddress(currentRow, currentRow, 0, 4));
		cell.setCellStyle(centered);
		
		cell = row.createCell(5);
		cell.setCellValue("UNIT");
		cell.setCellStyle(centered);
		
		cell = row.createCell(6);
		cell.setCellValue("QTY");
		cell.setCellStyle(centered);
		
		cell = row.createCell(7);
		cell.setCellValue("PRICE/U");
		cell.setCellStyle(centered);
		
		cell = row.createCell(8);
		cell.setCellValue("AMT");
		cell.setCellStyle(centered);
		
		currentRow++;
		
		for (PurchaseOrderItem item : purchaseOrder.getItems()) {
			currentRow++;
			row = sheet.createRow(currentRow);
			row.createCell(0).setCellValue(item.getProduct().getDescription());
			row.createCell(5).setCellValue(item.getUnit());
			row.createCell(6).setCellValue(item.getQuantity());
			cell = row.createCell(7);
			cell.setCellValue(item.getCost().doubleValue());
			cell.setCellStyle(amountFormat);
			cell = row.createCell(8);
			cell.setCellValue(item.getAmount().doubleValue());
			cell.setCellStyle(amountFormat);
		}
		
		currentRow++;
		currentRow++;
		
		row = sheet.createRow(currentRow);
		cell = row.createCell(8);
		cell.setCellValue(purchaseOrder.getTotalAmount().doubleValue());
		cell.setCellStyle(amountFormat);

		currentRow++;
		currentRow++;
		
		row = sheet.createRow(currentRow);
		row.createCell(0).setCellValue("TOTAL ITEMS: " + purchaseOrder.getTotalNumberOfItems());
		row.createCell(2).setCellValue("TOTAL QTY: " + purchaseOrder.getTotalQuantity());

		currentRow++;
		currentRow++;
		
		row = sheet.createRow(currentRow);
		row.createCell(0).setCellValue("PREPARED BY: " + purchaseOrder.getCreatedBy().getUsername());
		
		currentRow++;
		currentRow++;

		row = sheet.createRow(currentRow);
		row.createCell(0).setCellValue("REMARKS: " + purchaseOrder.getRemarks());
		
		sheet.autoSizeColumn(5);
		sheet.autoSizeColumn(7);
		sheet.autoSizeColumn(8);
		
		return workbook;
	}

}