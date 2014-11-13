package com.pj.magic.service;

import java.math.BigDecimal;

import org.junit.Test;

import com.pj.magic.model.Customer;
import com.pj.magic.model.Product;
import com.pj.magic.model.SalesInvoice;
import com.pj.magic.model.SalesInvoiceItem;
import com.pj.magic.model.Unit;
import com.pj.magic.model.User;

public class PrintServiceTest {

	private PrintService printService = new PrintServiceImpl();
	
	@Test
	public void printSalesInvoice() {
		SalesInvoice salesInvoice = new SalesInvoice();
		salesInvoice.setSalesInvoiceNumber(45514L);
		salesInvoice.setPostedBy(new User(1L, "PJ"));
		
		Customer customer = new Customer();
		customer.setName("XX");
		customer.setBusinessAddress("CALOOCAN CITY");
		salesInvoice.setCustomer(customer);
		
		Product product = new Product();
		product.setDescription("PRODUCT DESCRIPTION HERE");
		
		SalesInvoiceItem item = new SalesInvoiceItem();
		item.setProduct(product);
		item.setUnit(Unit.CASE);
		item.setQuantity(10);
		item.setUnitPrice(new BigDecimal("4566.45"));
		salesInvoice.getItems().add(item);
		
		printService.print(salesInvoice);
	}
	
}
