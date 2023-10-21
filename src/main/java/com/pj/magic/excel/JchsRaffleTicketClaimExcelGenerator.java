package com.pj.magic.excel;

import java.io.IOException;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.pj.magic.model.PromoRaffleTicket;
import com.pj.magic.model.PromoRaffleTicketClaim;
import com.pj.magic.model.SalesInvoice;
import com.pj.magic.model.util.CellStyleBuilder;
import com.pj.magic.util.FormatterUtil;

public class JchsRaffleTicketClaimExcelGenerator {

    public Workbook generate(PromoRaffleTicketClaim claim) throws IOException {
		XSSFWorkbook workbook = new XSSFWorkbook(
				getClass().getResourceAsStream("/excel/raffleTicketClaim.xlsx"));
		Sheet sheet = workbook.getSheetAt(0);

		Row row = sheet.getRow(0);
		row.createCell(1).setCellValue(claim.getCustomer().getCode());
		
		row = sheet.getRow(1);
		row.createCell(1).setCellValue(claim.getCustomer().getName());
		
		row = sheet.getRow(3);
		row.createCell(1).setCellValue(FormatterUtil.formatDate(claim.getTransactionDate()));
		
		Cell cell = null;
		
		List<SalesInvoice> salesInvoices = claim.getSalesInvoices();
		for (int i = 0; i < salesInvoices.size(); i++) {
			row = sheet.getRow(6 + i);
			if (row == null) {
				row = sheet.createRow(6 + i);
			}
			
			cell = row.getCell(0);
			if (cell == null) {
				cell = row.createCell(0);
			}
			
			cell.setCellValue(salesInvoices.get(i).getSalesInvoiceNumber());
		}
		
        CellStyle rightAlign = CellStyleBuilder.createStyle(workbook).setAlignment(CellStyle.ALIGN_RIGHT).build();
		
		List<PromoRaffleTicket> tickets = claim.getTickets();
		for (int i = 0; i < tickets.size(); i++) {
			row = sheet.getRow(6 + i);
			if (row == null) {
				row = sheet.createRow(6 + i);
			}
			
			cell = row.getCell(1);
			if (cell == null) {
				cell = row.createCell(1);
			}
			
			cell.setCellValue(tickets.get(i).getTicketNumberDisplay());
			cell.setCellStyle(rightAlign);
		}
		
		return workbook;
    }

}
