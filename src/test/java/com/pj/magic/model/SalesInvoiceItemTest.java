package com.pj.magic.model;

import static org.junit.Assert.*;
import org.junit.Test;

public class SalesInvoiceItemTest {

	@Test
	public void compareTo_ProductDescriptionLess() {
		Product product1 = new Product();
		product1.setDescription("A");
		
		Product product2 = new Product();
		product2.setDescription("B");

		SalesInvoiceItem item1 = new SalesInvoiceItem();
		item1.setProduct(product1);
		
		SalesInvoiceItem item2 = new SalesInvoiceItem();
		item2.setProduct(product2);
		
		assertEquals(-1, item1.compareTo(item2));
	}
	
	@Test
	public void compareTo_ProductDescriptionGreater() {
		Product product1 = new Product();
		product1.setDescription("B");
		
		Product product2 = new Product();
		product2.setDescription("A");

		SalesInvoiceItem item1 = new SalesInvoiceItem();
		item1.setProduct(product1);
		
		SalesInvoiceItem item2 = new SalesInvoiceItem();
		item2.setProduct(product2);
		
		assertEquals(1, item1.compareTo(item2));
	}
	
	@Test
	public void compareTo_ProductDescriptionSame() {
		Product product1 = new Product();
		product1.setDescription("A");
		
		Product product2 = new Product();
		product2.setDescription("A");

		SalesInvoiceItem item1 = new SalesInvoiceItem();
		item1.setProduct(product1);
		item1.setUnit(Unit.PIECES);
		
		SalesInvoiceItem item2 = new SalesInvoiceItem();
		item2.setProduct(product2);
		item2.setUnit(Unit.CASE);
		
		assertEquals(-1, item1.compareTo(item2));
	}
	
}
