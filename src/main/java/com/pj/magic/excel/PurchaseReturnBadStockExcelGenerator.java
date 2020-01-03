package com.pj.magic.excel;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.pj.magic.dao.SystemDao;
import com.pj.magic.model.PurchaseReturnBadStock;
import com.pj.magic.model.PurchaseReturnBadStockItem;
import com.pj.magic.model.util.CellStyleBuilder;
import com.pj.magic.util.FormatterUtil;

public class PurchaseReturnBadStockExcelGenerator {

    private SystemDao systemDao;
    
    public PurchaseReturnBadStockExcelGenerator(SystemDao systemDao) {
        this.systemDao = systemDao;
    }

    public Workbook generate(PurchaseReturnBadStock purchaseOrder) {
//        purchaseOrder.setSupplier(supplierDao.get(purchaseOrder.getSupplier().getId()));
        
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
        cell.setCellValue("PURCHASE RETURN - BAD STOCK");
        sheet.addMergedRegion(new CellRangeAddress(currentRow, currentRow, 0, 8));
        cell.setCellStyle(centered);
        
        currentRow++;
        currentRow++;
        
        row = sheet.createRow(currentRow);
        row.createCell(0).setCellValue("SUPPLIER: " + purchaseOrder.getSupplier().getName());
        row.createCell(7).setCellValue("PRBS#");
        row.createCell(8).setCellValue(purchaseOrder.getPurchaseReturnBadStockNumber());
        
        currentRow++;
        
        row = sheet.createRow(currentRow);
        row.createCell(0).setCellValue("ADDRESS: " + purchaseOrder.getSupplier().getAddress());
        row.createCell(7).setCellValue("DATE:");
        cell = row.createCell(8);
        cell.setCellValue(FormatterUtil.formatDate(systemDao.getCurrentDateTime()));
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
        cell.setCellValue("UNIT COST");
        cell.setCellStyle(centered);
        
        cell = row.createCell(8);
        cell.setCellValue("AMOUNT");
        cell.setCellStyle(centered);
        
        currentRow++;
        
        for (PurchaseReturnBadStockItem item : purchaseOrder.getItems()) {
            currentRow++;
            row = sheet.createRow(currentRow);
            row.createCell(0).setCellValue(item.getProduct().getDescription());
            row.createCell(5).setCellValue(item.getUnit());
            row.createCell(6).setCellValue(item.getQuantity());
            cell = row.createCell(7);
            cell.setCellValue(item.getUnitCost().doubleValue());
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
        row.createCell(0).setCellValue("TOTAL ITEMS: " + purchaseOrder.getTotalItems());

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
