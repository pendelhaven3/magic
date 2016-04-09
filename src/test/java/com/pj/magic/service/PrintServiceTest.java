package com.pj.magic.service;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Arrays;

import javax.print.PrintException;

import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatcher;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.test.util.ReflectionTestUtils;

import com.pj.magic.dao.SalesInvoiceDao;
import com.pj.magic.model.Customer;
import com.pj.magic.model.PricingScheme;
import com.pj.magic.model.Product;
import com.pj.magic.model.Promo;
import com.pj.magic.model.PromoRedemption;
import com.pj.magic.model.PromoRedemptionReward;
import com.pj.magic.model.PromoType2Rule;
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
	@Mock private SalesInvoiceDao salesInvoiceDao;
	
	@Before
	public void setUp() throws Exception {
		printService = new PrintServiceImpl();
		ReflectionTestUtils.setField(printService, "promoRedemptionService", promoRedemptionService);
		ReflectionTestUtils.setField(printService, "salesInvoiceDao", salesInvoiceDao);
		
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
		verify(salesInvoiceDao).save(argThat(new ArgumentMatcher<SalesInvoice>() {
		
			@Override
			public boolean matches(Object argument) {
				SalesInvoice arg = (SalesInvoice)argument;
				return arg.isPrinted();
			}
		}));
	}
	
	@Test
	public void printBirCashForm() throws IOException {
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
		product.setCode("TEST");
		product.setDescription("PRODUCT DESCRIPTION HERE");
		
		SalesInvoiceItem item = new SalesInvoiceItem();
		item.setProduct(product);
		item.setUnit(Unit.CASE);
		item.setQuantity(10);
		item.setUnitPrice(new BigDecimal("4566.45"));
		salesInvoice.getItems().add(item);
		
		printService.printBirCashForm(salesInvoice);
		
		assertEquals(getExpectedPrinterOutput("salesInvoiceBirCashForm.txt"), printerUtil.getPrinterOutput());
	}
	
	@Test
	public void printBirCashForm_multiplePages() throws IOException {
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
		product.setCode("PRODUCT");
		product.setDescription("PRODUCT DESCRIPTION HERE");
		
		SalesInvoiceItem item = new SalesInvoiceItem();
		item.setProduct(product);
		item.setUnit(Unit.PIECES);
		item.setQuantity(5);
		item.setUnitPrice(new BigDecimal("26.85"));
		salesInvoice.getItems().add(item);
		
		Product product2 = new Product();
		product2.setCode("TEST");
		product2.setDescription("TEST PRODUCT DESCRIPTION HERE");
		
		SalesInvoiceItem item2 = new SalesInvoiceItem();
		item2.setProduct(product2);
		item2.setUnit(Unit.CASE);
		item2.setQuantity(10);
		item2.setUnitPrice(new BigDecimal("4566.45"));
		
		for (int i = 0; i < 11; i++) {
			salesInvoice.getItems().add(item2);
		}

		SalesInvoiceItem item3 = new SalesInvoiceItem();
		item3.setProduct(createProduct("Y-ITEM 3", "ITEM 3 DESCRIPTION"));
		item3.setUnit(Unit.CASE);
		item3.setQuantity(18);
		item3.setUnitPrice(new BigDecimal("1759.90"));
		salesInvoice.getItems().add(item3);
		
		SalesInvoiceItem item4 = new SalesInvoiceItem();
		item4.setProduct(createProduct("Z-ITEM 4", "ITEM 4 DESCRIPTION"));
		item4.setUnit(Unit.CASE);
		item4.setQuantity(3);
		item4.setUnitPrice(new BigDecimal("4240.55"));
		salesInvoice.getItems().add(item4);
		
		printService.printBirCashForm(salesInvoice);
		
		assertEquals(getExpectedPrinterOutput("salesInvoiceBirCashForm-multiplepages.txt"), printerUtil.getPrinterOutput());
	}
	
	@Test
	public void printBirCashForm_withPromoType2Reward() throws IOException {
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
		product.setCode("PRODUCT");
		product.setDescription("PRODUCT DESCRIPTION HERE");
		
		SalesInvoiceItem item = new SalesInvoiceItem();
		item.setProduct(product);
		item.setUnit(Unit.PIECES);
		item.setQuantity(5);
		item.setUnitPrice(new BigDecimal("26.85"));
		salesInvoice.getItems().add(item);
		
		Product product2 = new Product();
		product2.setCode("TEST");
		product2.setDescription("TEST PRODUCT DESCRIPTION HERE");
		
		SalesInvoiceItem item2 = new SalesInvoiceItem();
		item2.setProduct(product2);
		item2.setUnit(Unit.CASE);
		item2.setQuantity(10);
		item2.setUnitPrice(new BigDecimal("4566.45"));
		
		for (int i = 0; i < 12; i++) {
			salesInvoice.getItems().add(item2);
		}
		
		PromoType2Rule rule = new PromoType2Rule();
		rule.setPromoProduct(product);
		rule.setFreeProduct(createProduct("FREE", "FREE PRODUCT DESCRIPTION"));
		
		Promo promo = new Promo();
		promo.setPromoType2Rules(Arrays.asList(rule));
		
		PromoRedemption promoRedemption = new PromoRedemption();
		promoRedemption.setPromo(promo);
		promoRedemption.getRewards().add(createReward(rule.getFreeProduct(), Unit.PIECES, 1));
		
		when(promoRedemptionService.findAllAvailedPromoRedemptions(salesInvoice))
			.thenReturn(Arrays.asList(promoRedemption));
		
		printService.printBirCashForm(salesInvoice);
		
		assertEquals(getExpectedPrinterOutput("salesInvoiceBirCashForm-withPromoType2Reward.txt"), printerUtil.getPrinterOutput());
	}
	
	private PromoRedemptionReward createReward(Product freeProduct, String unit, int quantity) {
		PromoRedemptionReward reward = new PromoRedemptionReward();
		reward.setProduct(freeProduct);
		reward.setUnit(unit);
		reward.setQuantity(quantity);
		return reward;
	}

	private Product createProduct(String code, String description) {
		Product product = new Product();
		product.setCode(code);
		product.setDescription(description);
		return product;
	}
	
}

class PrinterUtilMock extends PrinterUtil {

	private StringBuilder printerOutput = new StringBuilder();
	
	@Override
	public void print(String data) throws PrintException {
		printerOutput.append(data);
	}
	
	public String getPrinterOutput() {
		return printerOutput.toString();
	}
	
}