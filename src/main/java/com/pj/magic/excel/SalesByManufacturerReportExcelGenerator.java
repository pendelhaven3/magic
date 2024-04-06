package com.pj.magic.excel;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.pj.magic.model.report.SalesByManufacturerReport;
import com.pj.magic.model.report.SalesByManufacturerReportItem;
import com.pj.magic.model.util.CellStyleBuilder;
import com.pj.magic.util.FormatterUtil;

public class SalesByManufacturerReportExcelGenerator {

    public Workbook generate(SalesByManufacturerReport report) {
        XSSFWorkbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet();

        CellStyle amountFormat = CellStyleBuilder.createStyle(workbook)
                .setAmountFormat(true).build();
        CellStyle headerStyle = CellStyleBuilder.createStyle(workbook)
        		.setBold(true).setAlignment(CellStyle.ALIGN_CENTER).build();
        
        int currentRow = 0;
        
        Row row = sheet.createRow(currentRow);
        Cell cell = row.createCell(0);
        cell.setCellValue("SALES BY MANUFACTURER REPORT");
        
        currentRow++;
        
        if (report.getCriteria().getCustomer() != null) {
            currentRow++;
            
            row = sheet.createRow(currentRow);
            cell = row.createCell(0);
            cell.setCellValue("Customer: " + report.getCriteria().getCustomer().getCode() + " - " + report.getCriteria().getCustomer().getName());
        }
        
        currentRow++;
        
        row = sheet.createRow(currentRow);
        cell = row.createCell(0);
        cell.setCellValue("From Date: " + FormatterUtil.formatDate(report.getCriteria().getFromDate()));
        
        currentRow++;
        
        row = sheet.createRow(currentRow);
        cell = row.createCell(0);
        cell.setCellValue("To Date: " + FormatterUtil.formatDate(report.getCriteria().getToDate()));
        
        currentRow++;
        currentRow++;
        
        row = sheet.createRow(currentRow);
        cell = row.createCell(0);
        cell.setCellValue("Manufacturer");
        cell.setCellStyle(headerStyle);
        
        cell = row.createCell(1);
        cell.setCellValue("Amount");
        cell.setCellStyle(headerStyle);
        
        for (SalesByManufacturerReportItem item : report.getItems()) {
            currentRow++;
            row = sheet.createRow(currentRow);
            row.createCell(0).setCellValue(item.getManufacturer().getName());
            
            cell = row.createCell(1);
            cell.setCellValue(item.getAmount().doubleValue());
            cell.setCellStyle(amountFormat);
        }
        
        sheet.autoSizeColumn(0);
        sheet.autoSizeColumn(1);
        
        return workbook;
    }
	
}
