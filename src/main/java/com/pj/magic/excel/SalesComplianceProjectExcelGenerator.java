package com.pj.magic.excel;

import java.io.IOException;
import java.text.SimpleDateFormat;

import org.apache.poi.hssf.usermodel.HSSFFormulaEvaluator;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFRichTextString;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.pj.magic.model.SalesComplianceProject;
import com.pj.magic.model.SalesComplianceProjectSalesInvoice;
import com.pj.magic.model.SalesInvoice;

public class SalesComplianceProjectExcelGenerator {

	private SimpleDateFormat transactionDateFormatter = new SimpleDateFormat("LLLL dd, yyyy");
	
    public Workbook generate(SalesComplianceProject salesComplianceProject) throws IOException {
		XSSFWorkbook workbook = new XSSFWorkbook(getClass().getResourceAsStream("/excel/salesComplianceProject.xlsx"));
		Sheet sheet = workbook.getSheetAt(0);
		
		Row row = sheet.getRow(1);
		row.createCell(0).setCellValue(generateHeader(workbook, salesComplianceProject));
		
		row = sheet.getRow(5);
		
        CellStyle textCellStyle = row.getCell(0).getCellStyle();
        CellStyle boldAmountCellStyle = row.getCell(6).getCellStyle();
        CellStyle amountCellStyle = row.getCell(7).getCellStyle();
        
		int i = 0;
		int startRow = 5;
		Cell cell = null;
		
		for (SalesComplianceProjectSalesInvoice projectSalesInvoice : salesComplianceProject.getSalesInvoices()) {
			if (i > 0) {
				sheet.shiftRows(startRow + i, startRow + i + 1, 1);
				Row newRow = sheet.createRow(startRow + i);
				newRow.createCell(0, Cell.CELL_TYPE_STRING).setCellStyle(textCellStyle);
				newRow.createCell(1, Cell.CELL_TYPE_STRING).setCellStyle(textCellStyle);
				newRow.createCell(2, Cell.CELL_TYPE_STRING).setCellStyle(textCellStyle);
				newRow.createCell(3, Cell.CELL_TYPE_STRING).setCellStyle(textCellStyle);
				newRow.createCell(4, Cell.CELL_TYPE_STRING).setCellStyle(textCellStyle);
				newRow.createCell(5, Cell.CELL_TYPE_STRING).setCellStyle(textCellStyle);
				newRow.createCell(6, Cell.CELL_TYPE_NUMERIC).setCellStyle(boldAmountCellStyle);
				newRow.createCell(7, Cell.CELL_TYPE_NUMERIC).setCellStyle(amountCellStyle);
				newRow.createCell(8, Cell.CELL_TYPE_NUMERIC).setCellStyle(amountCellStyle);
			}
			
			row = sheet.getRow(startRow + i);
			
			SalesInvoice salesInvoice = projectSalesInvoice.getSalesInvoice();
			
			cell = row.getCell(0);
			cell.setCellValue(transactionDateFormatter.format(salesInvoice.getTransactionDate()));
			
			cell = row.getCell(1);
			cell.setCellValue(salesInvoice.getPrintInvoiceNumber());
			
			cell = row.getCell(2);
			cell.setCellValue(salesInvoice.getCustomer().getTin());
			
			cell = row.getCell(3);
			cell.setCellValue(salesInvoice.getCustomer().getName());
			
			cell = row.getCell(4);
			cell.setCellValue(salesInvoice.getCustomer().getBusinessAddress());
			
			cell = row.getCell(5);
			cell.setCellValue("SALES INVOICE");
			
			cell = row.getCell(6);
			cell.setCellValue(projectSalesInvoice.getTotalAmount().doubleValue());
			
			cell = row.getCell(7);
			cell.setCellValue(projectSalesInvoice.getVatableSales().doubleValue());
			
			cell = row.getCell(8);
			cell.setCellValue(projectSalesInvoice.getVatAmount().doubleValue());
			
			i++;
		}
		
        HSSFFormulaEvaluator.evaluateAllFormulaCells(workbook);
		
		return workbook;
    }

    private XSSFRichTextString generateHeader(XSSFWorkbook workbook, SalesComplianceProject salesComplianceProject) {
		String headerText = "FOR THE MONTH OF " + new SimpleDateFormat("LLLL yyyy").format(salesComplianceProject.getStartDate()).toUpperCase();
		
        XSSFFont redFont = workbook.createFont();
        redFont.setColor(IndexedColors.RED.getIndex());
        redFont.setBold(true);
		
		XSSFRichTextString header = new XSSFRichTextString(headerText);
		header.applyFont(17, header.length(), redFont);
		
		return header;
    }
    
}
