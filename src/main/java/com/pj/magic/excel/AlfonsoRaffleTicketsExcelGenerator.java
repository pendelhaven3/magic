package com.pj.magic.excel;

import java.io.IOException;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.pj.magic.model.PromoRaffleTicket;

public class AlfonsoRaffleTicketsExcelGenerator {

    public Workbook generate(List<PromoRaffleTicket> tickets) throws IOException {
		XSSFWorkbook workbook = new XSSFWorkbook(
				getClass().getResourceAsStream("/excel/alfonsoRaffleTickets.xlsx"));
		Sheet sheet = workbook.getSheetAt(0);

		Cell cell = null;
		Row row = null;
		for (int i = 0; i < tickets.size(); i++) {
			PromoRaffleTicket ticket = tickets.get(i);
			
			row = sheet.createRow(4 + i);
			
			cell = row.createCell(0);
			cell.setCellType(Cell.CELL_TYPE_STRING);
			cell.setCellValue(ticket.getTicketNumberDisplay());
			
			cell = row.createCell(1);
			cell.setCellValue(ticket.getCustomer().getName());
		}
		
		return workbook;
    }

}
