package com.pj.magic.excel;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.pj.magic.model.PromoRaffleTicket;
import com.pj.magic.model.PromoRaffleTicketClaim;
import com.pj.magic.model.PromoRaffleTicketClaimSummary;
import com.pj.magic.model.SalesInvoice;
import com.pj.magic.util.FormatterUtil;

public class JchsCellphoneRaffleTicketClaimExcelGenerator {

    public Workbook generate(PromoRaffleTicketClaim claim) throws IOException {
		XSSFWorkbook workbook = new XSSFWorkbook(
				getClass().getResourceAsStream("/excel/jchsCellphoneRaffleTicketClaim.xlsx"));
		Sheet sheet = workbook.getSheetAt(0);

		Row row = sheet.getRow(0);
		row.createCell(1).setCellValue(claim.getCustomer().getCode());
		
		row = sheet.getRow(1);
		row.createCell(1).setCellValue(claim.getCustomer().getName());
		
        CellStyle wrap = workbook.createCellStyle();
        wrap.setWrapText(true);
        
        CellStyle valignTop = workbook.createCellStyle();
        valignTop.setVerticalAlignment(CellStyle.VERTICAL_TOP);
        
        CellStyle valignTopAndWrap = workbook.createCellStyle();
        valignTopAndWrap.setVerticalAlignment(CellStyle.VERTICAL_TOP);
        valignTopAndWrap.setWrapText(true);

        List<PromoRaffleTicket> tickets = new ArrayList<>(claim.getTickets());
        
		Cell cell = null;
		int startRow = 5;
		int lastRow = 5;

		List<PromoRaffleTicketClaimSummary> summaries = PromoRaffleTicketClaimSummary.toSummaries(claim.getSalesInvoices());
		for (int i = 0; i < summaries.size(); i++) {
			PromoRaffleTicketClaimSummary summary = summaries.get(i);
			if (summary.getNumberOfTickets() == 0) {
				continue;
			}
			
			row = sheet.getRow(startRow);
			if (row == null) {
				row = sheet.createRow(startRow);
			}
			
			cell = row.getCell(0);
			if (cell == null) {
				cell = row.createCell(0);
			}
			
			cell.setCellValue(FormatterUtil.formatDate(summary.getTransactionDate()));
			
			List<SalesInvoice> salesInvoices = summary.getSalesInvoices();
			for (int j = 0; j < salesInvoices.size(); j++) {
				row = sheet.getRow(startRow + j);
				if (row == null) {
					row = sheet.createRow(startRow + j);
				}
				
				cell = row.getCell(1);
				if (cell == null) {
					cell = row.createCell(1);
				}
				cell.setCellValue(String.valueOf(salesInvoices.get(j).getSalesInvoiceNumber()));
				
				if (lastRow < startRow + j) {
					lastRow = startRow + j;
				}
			}
			
			for (int j = 0; j < summary.getNumberOfTickets(); j++) {
				row = sheet.getRow(startRow + j);
				if (row == null) {
					row = sheet.createRow(startRow + j);
				}
				
				cell = row.getCell(2);
				if (cell == null) {
					cell = row.createCell(2);
				}
				cell.setCellValue(String.valueOf(tickets.remove(0).getTicketNumberDisplay()));
				
				if (lastRow < startRow + j) {
					lastRow = startRow + j;
				}
			}
			
			lastRow++;
			startRow = lastRow;
		}
		
		return workbook;
    }

}
