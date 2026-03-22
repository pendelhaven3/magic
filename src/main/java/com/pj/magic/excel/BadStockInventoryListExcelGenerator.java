package com.pj.magic.excel;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.pj.magic.model.BadStock;
import com.pj.magic.model.Supplier;
import com.pj.magic.model.Unit;
import com.pj.magic.model.util.CellStyleBuilder;

public class BadStockInventoryListExcelGenerator {

    public Workbook generate(List<BadStock> items, Supplier supplier) {
        XSSFWorkbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet();

        CellStyle centered = CellStyleBuilder.createStyle(workbook)
                .setAlignment(CellStyle.ALIGN_CENTER).build();
        CellStyle bold = CellStyleBuilder.createStyle(workbook)
                .setBold(true).build();
        CellStyle header = CellStyleBuilder.createStyle(workbook)
        		.setAlignment(CellStyle.ALIGN_CENTER)
        		.setBold(true).build();
        
        int currentRow = 0;
        
        Row row = sheet.createRow(currentRow);
        Cell cell = row.createCell(0);
        cell.setCellValue("KAPITBAHAY GROCERY");
        sheet.addMergedRegion(new CellRangeAddress(currentRow, currentRow, 0, 7));
        cell.setCellStyle(header);
        
        currentRow++;

        row = sheet.createRow(currentRow);
        cell = row.createCell(0);
        cell.setCellValue("BAD STOCK INVENTORY REPORT");
        sheet.addMergedRegion(new CellRangeAddress(currentRow, currentRow, 0, 7));
        cell.setCellStyle(header);
        
        currentRow++;

        row = sheet.createRow(currentRow);
        cell = row.createCell(0);
        cell.setCellValue(new SimpleDateFormat("MMMM dd, yyyy").format(new Date()).toUpperCase());
        sheet.addMergedRegion(new CellRangeAddress(currentRow, currentRow, 0, 7));
        cell.setCellStyle(header);
        
        currentRow++;
        currentRow++;

        row = sheet.createRow(currentRow);
        cell = row.createCell(0);
        cell.setCellValue("SUPPLIER");
        cell.setCellStyle(bold);
        
        cell = row.createCell(1);
        cell.setCellValue(supplier != null ? supplier.getName() : "ALL");
        cell.setCellStyle(bold);
        
        currentRow++;
        currentRow++;
        
        row = sheet.createRow(currentRow);
        cell = row.createCell(1);
        cell.setCellValue("PRODUCT ITEM");
        cell.setCellStyle(header);
        
        cell = row.createCell(2);
        cell.setCellValue("CASE");
        cell.setCellStyle(header);
        
        cell = row.createCell(3);
        cell.setCellValue("TIE");
        cell.setCellStyle(header);
        
        cell = row.createCell(4);
        cell.setCellValue("CTN");
        cell.setCellStyle(header);
        
        cell = row.createCell(5);
        cell.setCellValue("DOZ");
        cell.setCellStyle(header);
        
        cell = row.createCell(6);
        cell.setCellValue("PCS");
        cell.setCellStyle(header);
        
        cell = row.createCell(7);
        cell.setCellValue("REMARKS");
        cell.setCellStyle(header);
        
        currentRow++;
        
        for (BadStock item : items) {
            currentRow++;
            row = sheet.createRow(currentRow);
            row.createCell(0).setCellValue(item.getProduct().getCode());
            
            cell = row.createCell(1);
            cell.setCellValue(item.getProduct().getDescription());
            
            if (item.getProduct().hasUnit(Unit.CASE)) {
                cell = row.createCell(2);
                cell.setCellValue(item.getUnitQuantity(Unit.CASE).doubleValue());
                cell.setCellStyle(centered);
            }
            
            if (item.getProduct().hasUnit(Unit.TIE)) {
                cell = row.createCell(3);
                cell.setCellValue(item.getUnitQuantity(Unit.TIE).doubleValue());
                cell.setCellStyle(centered);
            }
            
            if (item.getProduct().hasUnit(Unit.CARTON)) {
                cell = row.createCell(4);
                cell.setCellValue(item.getUnitQuantity(Unit.CARTON).doubleValue());
                cell.setCellStyle(centered);
            }
            
            if (item.getProduct().hasUnit(Unit.DOZEN)) {
                cell = row.createCell(5);
                cell.setCellValue(item.getUnitQuantity(Unit.DOZEN).doubleValue());
                cell.setCellStyle(centered);
            }
            
            if (item.getProduct().hasUnit(Unit.PIECES)) {
                cell = row.createCell(6);
                cell.setCellValue(item.getUnitQuantity(Unit.PIECES).doubleValue());
                cell.setCellStyle(centered);
            }
        }
        
        sheet.setColumnWidth(7, 25);
        
        sheet.autoSizeColumn(0);
        sheet.autoSizeColumn(1);
        
        return workbook;
    }

}
