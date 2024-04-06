package com.pj.magic.excel;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.pj.magic.model.report.TopSalesByItemReport;
import com.pj.magic.model.report.TopSalesByItemReportItem;
import com.pj.magic.model.util.CellStyleBuilder;
import com.pj.magic.util.FormatterUtil;

public class TopSalesByItemReportExcelGenerator {

    public Workbook generate(TopSalesByItemReport report) {
        XSSFWorkbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet();

        CellStyle amountFormat = CellStyleBuilder.createStyle(workbook)
                .setAmountFormat(true).build();
        CellStyle headerStyle = CellStyleBuilder.createStyle(workbook)
        		.setBold(true).setAlignment(CellStyle.ALIGN_CENTER).build();
        
        int currentRow = 0;
        
        Row row = sheet.createRow(currentRow);
        Cell cell = row.createCell(0);
        cell.setCellValue("TOP SALES BY ITEM REPORT");
        
        currentRow++;
        currentRow++;
        
        row = sheet.createRow(currentRow);
        cell = row.createCell(0);
        cell.setCellValue("From Date: " + FormatterUtil.formatDate(report.getFromDate()));
        
        currentRow++;
        
        row = sheet.createRow(currentRow);
        cell = row.createCell(0);
        cell.setCellValue("To Date: " + FormatterUtil.formatDate(report.getToDate()));
        
        currentRow++;
        currentRow++;
        
        row = sheet.createRow(currentRow);
        cell = row.createCell(0);
        cell.setCellValue("Product Code");
        cell.setCellStyle(headerStyle);
        
        cell = row.createCell(1);
        cell.setCellValue("Description");
        cell.setCellStyle(headerStyle);
        
        cell = row.createCell(2);
        cell.setCellValue("Unit");
        cell.setCellStyle(headerStyle);
        
        cell = row.createCell(3);
        cell.setCellValue("Amount");
        cell.setCellStyle(headerStyle);
        
        for (TopSalesByItemReportItem item : report.getItems()) {
            currentRow++;
            
            row = sheet.createRow(currentRow);
            
            cell = row.createCell(0);
            cell.setCellValue(item.getProduct().getCode());
            
            cell = row.createCell(1);
            cell.setCellValue(item.getProduct().getDescription());
            
            cell = row.createCell(2);
            cell.setCellValue(item.getUnit());
            
            cell = row.createCell(3);
            cell.setCellValue(item.getAmount().doubleValue());
            cell.setCellStyle(amountFormat);
        }
        
        sheet.autoSizeColumn(0);
        sheet.autoSizeColumn(1);
        sheet.autoSizeColumn(2);
        sheet.autoSizeColumn(3);
        
        return workbook;
    }
	
}
