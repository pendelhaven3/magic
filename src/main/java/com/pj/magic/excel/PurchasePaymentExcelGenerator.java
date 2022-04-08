package com.pj.magic.excel;

import java.io.IOException;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.pj.magic.model.PurchasePayment;
import com.pj.magic.model.util.CellStyleBuilder;
import com.pj.magic.service.PrintService;

public class PurchasePaymentExcelGenerator {

	private final PrintService printService;
	
    public PurchasePaymentExcelGenerator(PrintService printService) {
    	this.printService = printService;
	}
	
    public Workbook generate(PurchasePayment purchasePayment) throws IOException {
        XSSFWorkbook workbook = new XSSFWorkbook(getClass().getResourceAsStream("/excel/purchasePayment.xlsx"));
        Sheet sheet = workbook.getSheetAt(0);
        int currentRow = 0;
        
        XSSFFont font = workbook.createFont();
        font.setFontName("Courier New");
        font.setFontHeight(6);
        
        CellStyle style = CellStyleBuilder.createStyle(workbook).setFont(font).build();
        
		String printout = printService.generateReportAsString(purchasePayment).get(0);
		for (String line : printout.split("\r\n")) {
	        Row row = sheet.createRow(currentRow);
	        Cell cell = row.createCell(0, Cell.CELL_TYPE_STRING);
	        cell.setCellValue(line);
	        cell.setCellStyle(style);
	        currentRow++;
		}
        
		return workbook;
    }
		
}
