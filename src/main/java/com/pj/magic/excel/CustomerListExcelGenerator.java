package com.pj.magic.excel;

import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.pj.magic.model.Customer;
import com.pj.magic.model.util.CellStyleBuilder;

public class CustomerListExcelGenerator {

    public Workbook generate(List<Customer> customers) {
        XSSFWorkbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet();

        CellStyle centered = CellStyleBuilder.createStyle(workbook)
                .setAlignment(CellStyle.ALIGN_CENTER).build();
        CellStyle bold = CellStyleBuilder.createStyle(workbook)
                .setBold(true).build();
        
        int currentRow = 0;
        
        Row row = sheet.createRow(currentRow);
        Cell cell = row.createCell(0);
        cell.setCellValue("CUSTOMER LIST");
        sheet.addMergedRegion(new CellRangeAddress(currentRow, currentRow, 0, 5));
        cell.setCellStyle(centered);
        
        currentRow++;
        currentRow++;
        
        row = sheet.createRow(currentRow);
        cell = row.createCell(0);
        cell.setCellValue("CODE");
        cell.setCellStyle(bold);
        
        cell = row.createCell(1);
        cell.setCellValue("NAME");
        cell.setCellStyle(bold);
        
        currentRow++;
        
        for (Customer customer : customers) {
            currentRow++;
            row = sheet.createRow(currentRow);
            row.createCell(0).setCellValue(customer.getCode());
            
            cell = row.createCell(1);
            row.createCell(1).setCellValue(customer.getName());
        }
        
        currentRow++;
        currentRow++;
        
        row = sheet.createRow(currentRow);
        
        sheet.autoSizeColumn(0);
        sheet.autoSizeColumn(1);
        
        return workbook;
    }

}
