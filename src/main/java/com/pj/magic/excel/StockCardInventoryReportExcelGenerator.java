package com.pj.magic.excel;

import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.pj.magic.model.StockCardInventoryReportItem;
import com.pj.magic.model.util.CellStyleBuilder;
import com.pj.magic.util.FormatterUtil;

public class StockCardInventoryReportExcelGenerator {

    public Workbook generate(List<StockCardInventoryReportItem> items) {
        XSSFWorkbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet();

        CellStyle header = CellStyleBuilder.createStyle(workbook)
                .setAlignment(CellStyle.ALIGN_CENTER)
                .setBold(true)
                .build();
        CellStyle amountFormat = CellStyleBuilder.createStyle(workbook)
                .setAmountFormat(true).build();
        
        int currentRow = 0;
        
        Row row = sheet.createRow(currentRow);
        
        Cell cell = row.createCell(0);
        cell.setCellValue("Post Date");
        cell.setCellStyle(header);
        
        cell = row.createCell(1);
        cell.setCellValue("Trans. No.");
        cell.setCellStyle(header);
        
        cell = row.createCell(2);
        cell.setCellValue("Supplier/Customer");
        cell.setCellStyle(header);
        
        cell = row.createCell(3);
        cell.setCellValue("Trans. Type");
        cell.setCellStyle(header);
        
        cell = row.createCell(4);
        cell.setCellValue("Unit");
        cell.setCellStyle(header);
        
        cell = row.createCell(5);
        cell.setCellValue("Add Qty");
        cell.setCellStyle(header);
        
        cell = row.createCell(6);
        cell.setCellValue("Less Qty");
        cell.setCellStyle(header);
        
        cell = row.createCell(7);
        cell.setCellValue("Cost/Price");
        cell.setCellStyle(header);
        
        cell = row.createCell(8);
        cell.setCellValue("Amount");
        cell.setCellStyle(header);
        
        cell = row.createCell(9);
        cell.setCellValue("Ref. No.");
        cell.setCellStyle(header);
        
        for (StockCardInventoryReportItem item : items) {
            currentRow++;
            row = sheet.createRow(currentRow);
            row.createCell(0).setCellValue(FormatterUtil.formatDateTime(item.getPostDate()));
            row.createCell(1).setCellValue(String.valueOf(item.getTransactionNumber()));
            
            if (item.getSupplierOrCustomerName() != null) {
                row.createCell(2).setCellValue(item.getSupplierOrCustomerName());
            }
            
            row.createCell(3).setCellValue(item.getTransactionType());
            row.createCell(4).setCellValue(item.getUnit());
            
            if (item.getAddQuantity() != null) {
                row.createCell(5).setCellValue(item.getAddQuantity());
            }
            
            if (item.getLessQuantity() != null) {
                row.createCell(6).setCellValue(item.getLessQuantity());
            }
            
            if (item.getCurrentCostOrSellingPrice() != null) {
                cell = row.createCell(7);
                cell.setCellValue(item.getCurrentCostOrSellingPrice().doubleValue());
                cell.setCellStyle(amountFormat);
            }
            
            if (item.getAmount() != null) {
                cell = row.createCell(8);
                cell.setCellValue(item.getAmount().doubleValue());
                cell.setCellStyle(amountFormat);
            }
            
            if (item.getReferenceNumber() != null) {
                row.createCell(9).setCellValue(item.getReferenceNumber());
            }
        }
        
        sheet.autoSizeColumn(0);
        sheet.autoSizeColumn(1);
        sheet.autoSizeColumn(2);
        sheet.autoSizeColumn(3);
        sheet.autoSizeColumn(4);
        sheet.autoSizeColumn(5);
        sheet.autoSizeColumn(6);
        sheet.autoSizeColumn(7);
        sheet.autoSizeColumn(8);
        sheet.autoSizeColumn(9);
        
        return workbook;
    }

}
