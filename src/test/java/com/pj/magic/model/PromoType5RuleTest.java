package com.pj.magic.model;

import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

public class PromoType5RuleTest {

	private PromoType5Rule rule;
	
	@Before
	public void setUp() {
		rule = new PromoType5Rule();
	}
	
	@Test
	public void getQualifyingAmount() {
		rule.setPromoProducts(createPromoProducts());
		rule.setTargetAmount(new BigDecimal("100"));
		
		SalesInvoiceItem item = new SalesInvoiceItem();
		item.setProduct(createProduct());
		item.setUnit(Unit.CASE);
		item.setQuantity(3);
		item.setUnitPrice(new BigDecimal("100"));
		
		SalesInvoice salesInvoice = new SalesInvoice();
		salesInvoice.setItems(Arrays.asList(item));
		
		assertTrue(new BigDecimal("300").compareTo(rule.getQualifyingAmount(salesInvoice)) == 0);
	}

	@Test
	public void getQualifyingAmount_withSalesReturnAndNoMoreStockAdjustment() {
		rule.setPromoProducts(createPromoProducts());
		rule.setTargetAmount(new BigDecimal("100"));
		
		SalesInvoiceItem salesInvoiceItem = new SalesInvoiceItem();
		salesInvoiceItem.setProduct(createProduct());
		salesInvoiceItem.setUnit(Unit.CASE);
		salesInvoiceItem.setQuantity(3);
		salesInvoiceItem.setUnitPrice(new BigDecimal("100"));
		
		SalesReturnItem salesReturnItem = new SalesReturnItem();
		salesReturnItem.setSalesInvoiceItem(salesInvoiceItem);
		salesReturnItem.setQuantity(1);
		
		SalesReturn salesReturn = new SalesReturn();
		salesReturn.setItems(Arrays.asList(salesReturnItem));
		
		NoMoreStockAdjustmentItem noMoreStockAdjustmentItem = new NoMoreStockAdjustmentItem();
		noMoreStockAdjustmentItem.setSalesInvoiceItem(salesInvoiceItem);
		noMoreStockAdjustmentItem.setQuantity(1);
		
		NoMoreStockAdjustment noMoreStockAdjustment = new NoMoreStockAdjustment();
		noMoreStockAdjustment.setItems(Arrays.asList(noMoreStockAdjustmentItem));
		
		SalesInvoice salesInvoice = new SalesInvoice();
		salesInvoice.setItems(Arrays.asList(salesInvoiceItem));
		salesInvoice.setSalesReturns(Arrays.asList(salesReturn));
		salesInvoice.setNoMoreStockAdjustments(Arrays.asList(noMoreStockAdjustment));
		
		assertTrue(new BigDecimal("100").compareTo(rule.getQualifyingAmount(salesInvoice)) == 0);
	}
	
	private List<PromoType5RulePromoProduct> createPromoProducts() {
		PromoType5RulePromoProduct promoProduct = new PromoType5RulePromoProduct();
		promoProduct.setParent(rule);
		promoProduct.setProduct(createProduct());
		return Arrays.asList(promoProduct);
	}

	private Product createProduct() {
		Product product = new Product();
		product.setId(1L);
		product.setCode("PRODUCT");
		return product;
	}

}
