package com.pj.magic;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class AddPrintInvoiceColumn {

	@Autowired
	private JdbcTemplate jdbcTemplate;
	
	private static final String CHECK_SQL =
			"select count(*) from information_schema.columns"
			+ " where table_schema= 'magic' and table_name = 'SALES_INVOICE' and column_name = 'PRINT_INVOICE_NO'";
	
	@PostConstruct
	public void onStartUp() {
		try {
			Integer result = jdbcTemplate.queryForObject(CHECK_SQL, Integer.class);
			if (result == 0) {
				jdbcTemplate.update("alter table SALES_INVOICE add column PRINT_INVOICE_NO varchar(50)");
				System.out.println("PRINT_INVOICE_NO added");
			} else {
				System.out.println("PRINT_INVOICE_NO already exists");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
