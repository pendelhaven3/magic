package com.pj.magic.excel;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.pj.magic.model.report.InventoryReport;
import com.pj.magic.model.report.InventoryReportItem;
import com.pj.magic.model.util.CellStyleBuilder;

public class InventoryReportExcelGenerator {

    public Workbook generate(InventoryReport report) {
        XSSFWorkbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet();

        CellStyle centered = CellStyleBuilder.createStyle(workbook)
                .setAlignment(CellStyle.ALIGN_CENTER).build();
        CellStyle rightAligned = CellStyleBuilder.createStyle(workbook)
                .setAlignment(CellStyle.ALIGN_RIGHT).build();
        CellStyle amountFormat = CellStyleBuilder.createStyle(workbook)
                .setAmountFormat(true).build();
        
        int currentRow = 0;
        
        Row row = sheet.createRow(currentRow);
        Cell cell = row.createCell(0);
        cell.setCellValue("INVENTORY REPORT");
        sheet.addMergedRegion(new CellRangeAddress(currentRow, currentRow, 0, 5));
        cell.setCellStyle(centered);
        
        if (report.getManufacturer() != null) {
            currentRow++;
            
            row = sheet.createRow(currentRow);
            cell = row.createCell(0);
            cell.setCellValue(report.getManufacturer().getName());
            sheet.addMergedRegion(new CellRangeAddress(currentRow, currentRow, 0, 5));
            cell.setCellStyle(centered);
        }
        
        currentRow++;
        
        row = sheet.createRow(currentRow);
        cell = row.createCell(0);
        cell.setCellValue(new SimpleDateFormat("MMM-dd-yyyy").format(new Date()));
        sheet.addMergedRegion(new CellRangeAddress(currentRow, currentRow, 0, 5));
        cell.setCellStyle(centered);
        
        currentRow++;
        currentRow++;
        
        row = sheet.createRow(currentRow);
        cell = row.createCell(0);
        cell.setCellValue("CODE");
        cell.setCellStyle(centered);
        
        cell = row.createCell(1);
        cell.setCellValue("DESCRIPTION");
        cell.setCellStyle(centered);
        
        cell = row.createCell(2);
        cell.setCellValue("UNIT");
        cell.setCellStyle(centered);
        
        cell = row.createCell(3);
        cell.setCellValue("QUANTITY");
        cell.setCellStyle(centered);
        
        cell = row.createCell(4);
        cell.setCellValue("UNIT COST");
        cell.setCellStyle(centered);
        
        cell = row.createCell(5);
        cell.setCellValue("TOTAL COST");
        cell.setCellStyle(centered);
        
        currentRow++;
        
        for (InventoryReportItem item : report.getItems()) {
            currentRow++;
            row = sheet.createRow(currentRow);
            row.createCell(0).setCellValue(item.getProduct().getCode());
            row.createCell(1).setCellValue(item.getProduct().getDescription());
            row.createCell(2).setCellValue(item.getUnit());
            row.createCell(3).setCellValue(item.getQuantity());
            
            cell = row.createCell(4);
            cell.setCellValue(item.getCost().doubleValue());
            cell.setCellStyle(amountFormat);
            
            cell = row.createCell(5);
            cell.setCellValue(item.getTotalCost().doubleValue());
            cell.setCellStyle(amountFormat);
        }
        
        currentRow++;
        currentRow++;
        
        row = sheet.createRow(currentRow);
        
        cell = row.createCell(4);
        cell.setCellValue("TOTAL COST:");
        cell.setCellStyle(rightAligned);
        
        cell = row.createCell(5);
        cell.setCellValue(report.getTotalCost().doubleValue());
        cell.setCellStyle(amountFormat);

        sheet.autoSizeColumn(0);
        sheet.autoSizeColumn(1);
        sheet.autoSizeColumn(2);
        sheet.autoSizeColumn(3);
        sheet.autoSizeColumn(4);
        sheet.autoSizeColumn(5);
        
        return workbook;
    }

}
