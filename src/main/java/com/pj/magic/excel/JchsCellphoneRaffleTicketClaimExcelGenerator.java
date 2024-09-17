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

import com.pj.magic.model.CellphonePromoRaffleTicketClaimSummary;
import com.pj.magic.model.PromoRaffleTicket;
import com.pj.magic.model.PromoRaffleTicketClaim;
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

		row = sheet.getRow(3);
		row.createCell(1).setCellValue(claim.getId().toString());
		
        CellStyle wrap = workbook.createCellStyle();
        wrap.setWrapText(true);
        
        CellStyle valignTop = workbook.createCellStyle();
        valignTop.setVerticalAlignment(CellStyle.VERTICAL_TOP);
        
        CellStyle valignTopAndWrap = workbook.createCellStyle();
        valignTopAndWrap.setVerticalAlignment(CellStyle.VERTICAL_TOP);
        valignTopAndWrap.setWrapText(true);

        List<PromoRaffleTicket> tickets = new ArrayList<>(claim.getTickets());
        
		Cell cell = null;
		int startRow = 6;
		int lastRow = 6;

		List<CellphonePromoRaffleTicketClaimSummary> summaries = CellphonePromoRaffleTicketClaimSummary.toSummaries(claim.getSalesInvoices());
		for (int i = 0; i < summaries.size(); i++) {
			CellphonePromoRaffleTicketClaimSummary summary = summaries.get(i);
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
				
				if (j == 0) {
					cell = row.getCell(2);
					if (cell == null) {
						cell = row.createCell(2);
					}
					
					if (summary.getNumberOfTickets() == 1) {
						cell.setCellValue(tickets.remove(0).getTicketNumberDisplay());
					} else if (summary.getNumberOfTickets() > 1) {
						String firstTicket = tickets.remove(0).getTicketNumberDisplay();
						String lastTicket = null;
						for (int k = 1; k < summary.getNumberOfTickets(); k++) {
							lastTicket = tickets.remove(0).getTicketNumberDisplay();
						}
						cell.setCellValue(firstTicket + " - " + lastTicket);
					}
				}
			}
			
			lastRow++;
			startRow = lastRow;
		}
		
		return workbook;
    }

}
