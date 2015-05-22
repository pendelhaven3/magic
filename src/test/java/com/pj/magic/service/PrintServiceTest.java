package com.pj.magic.service;

import static org.junit.Assert.*;

import java.io.IOException;
import java.math.BigDecimal;

import javax.print.PrintException;

import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.test.util.ReflectionTestUtils;

import com.pj.magic.model.Customer;
import com.pj.magic.model.PricingScheme;
import com.pj.magic.model.Product;
import com.pj.magic.model.SalesInvoice;
import com.pj.magic.model.SalesInvoiceItem;
import com.pj.magic.model.Unit;
import com.pj.magic.model.User;
import com.pj.magic.util.DateUtil;
import com.pj.magic.util.PrinterUtil;

@RunWith(MockitoJUnitRunner.class)
public class PrintServiceTest {

	private PrintService printService;
	private PrinterUtilMock printerUtil;
	
	@Mock private PromoRedemptionService promoRedemptionService;
	
	@Before
	public void setUp() throws Exception {
		printService = new PrintServiceImpl();
		ReflectionTestUtils.setField(printService, "promoRedemptionService", promoRedemptionService);
		
		printerUtil = new PrinterUtilMock();
		ReflectionTestUtils.setField(printService, "printerUtil", printerUtil);
	}
	
	private String getExpectedPrinterOutput(String filename) throws IOException {
		return IOUtils.toString(getClass().getClassLoader().getResourceAsStream("reports/" + filename));
	}
	
	@Test
	public void printSalesInvoice() throws IOException {
		SalesInvoice salesInvoice = new SalesInvoice();
		salesInvoice.setSalesInvoiceNumber(45514L);
		salesInvoice.setPostedBy(new User(1L, "PJ"));
		salesInvoice.setTransactionDate(DateUtil.toDate("05/22/2015"));
		salesInvoice.setPricingScheme(new PricingScheme(1L));
		
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
		
		assertEquals(getExpectedPrinterOutput("salesInvoice.txt"), printerUtil.getPrinterOutput());
	}
	
	@Test
	public void printBirForm() throws IOException {
		SalesInvoice salesInvoice = new SalesInvoice();
		salesInvoice.setSalesInvoiceNumber(45514L);
		salesInvoice.setPostedBy(new User(1L, "PJ"));
		salesInvoice.setTransactionDate(DateUtil.toDate("05/22/2015"));
		salesInvoice.setPricingScheme(new PricingScheme(1L));
		salesInvoice.setVatAmount(new BigDecimal("123.45"));
		
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
		
		printService.printBirForm(salesInvoice);
		
		assertEquals(getExpectedPrinterOutput("salesInvoiceBirForm.txt"), printerUtil.getPrinterOutput());
	}
	
}

class PrinterUtilMock extends PrinterUtil {

	private String printerOutput;
	
	@Override
	public void print(String data) throws PrintException {
		printerOutput = data;
	}
	
	public String getPrinterOutput() {
		return printerOutput;
	}
	
}