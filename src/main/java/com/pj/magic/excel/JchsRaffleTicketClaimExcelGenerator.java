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
import com.pj.magic.util.FormatterUtil;

public class JchsRaffleTicketClaimExcelGenerator {

    public Workbook generate(PromoRaffleTicketClaim claim) throws IOException {
		XSSFWorkbook workbook = new XSSFWorkbook(
				getClass().getResourceAsStream("/excel/raffleTicketClaim.xlsx"));
		Sheet sheet = workbook.getSheetAt(0);

		List<PromoRaffleTicket> tickets = new ArrayList<>(claim.getTickets());
		
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
        
		Cell cell = null;
		int baseRow = 5;

		List<PromoRaffleTicketClaimSummary> summaries = PromoRaffleTicketClaimSummary.toSummaries(claim.getSalesInvoices());
		for (int i = 0; i < summaries.size(); i++) {
			PromoRaffleTicketClaimSummary summary = summaries.get(i);
			
			row = sheet.getRow(baseRow + i);
			if (row == null) {
				row = sheet.createRow(baseRow + i);
			}
			
			cell = row.getCell(0);
			if (cell == null) {
				cell = row.createCell(0);
			}
			
			cell.setCellValue(FormatterUtil.formatDate(summary.getTransactionDate()));
			cell.setCellStyle(valignTop);
			
			cell = row.getCell(1);
			if (cell == null) {
				cell = row.createCell(1);
			}
			
			cell.setCellValue(summary.getSalesInvoicesAsString());
			cell.setCellStyle(valignTopAndWrap);
			
			cell = row.getCell(2);
			if (cell == null) {
				cell = row.createCell(2);
			}
			
			StringBuilder sb = new StringBuilder();
			int summaryTickets = summary.getNumberOfTickets();
			for (int x = 0; x < summaryTickets; x++) {
				PromoRaffleTicket ticket = tickets.remove(0);
				if (sb.length() > 0) {
					sb.append("\r\n");
				}
				sb.append(ticket.getTicketNumberDisplay());
			}
			
			cell.setCellValue(sb.toString());
			cell.setCellStyle(valignTopAndWrap);
		}
		
		return workbook;
    }

}
