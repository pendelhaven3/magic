package com.pj.magic.model;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.pj.magic.util.DateUtil;

public class SalesRequisitionTest {

	private SalesRequisition salesRequisition;
	
	@Before
	public void setUp() {
		salesRequisition = new SalesRequisition();
	}
	
	@Test
	public void constructor_test() {
		salesRequisition = new SalesRequisition(1L);
		
		assertEquals(Long.valueOf(1L), salesRequisition.getId());
	}
	
	@Test
	public void getTotalAmount() {
		SalesRequisitionItem item = mock(SalesRequisitionItem.class);
		when(item.getAmount()).thenReturn(new BigDecimal("10"));
		
		SalesRequisitionItem item2 = mock(SalesRequisitionItem.class);
		when(item2.getAmount()).thenReturn(new BigDecimal("20"));
		
		salesRequisition.setItems(Arrays.asList(item, item2));
		
		assertEquals(new BigDecimal("30.00"), salesRequisition.getTotalAmount());
	}
	
	@Test
	public void hasItems() {
		salesRequisition.getItems().add(new SalesRequisitionItem());
		
		assertTrue(salesRequisition.hasItems());
	}
	
	@Test
	public void hasItems_false() {
		assertFalse(salesRequisition.hasItems());
	}
	
	@Test
	public void getTotalNumberOfItems() {
		salesRequisition.setItems(Arrays.asList(new SalesRequisitionItem(), new SalesRequisitionItem()));
		
		assertEquals(2, salesRequisition.getTotalNumberOfItems());
	}
	
	@Test
	public void hashCode_test() {
		salesRequisition.setId(1L);
		
		assertEquals(630, salesRequisition.hashCode());
	}
	
	@Test
	public void equals_null() {
		assertFalse(salesRequisition.equals(null));
	}

	@Test
	public void equals_differentClass() {
		assertFalse(salesRequisition.equals(new Object()));
	}
	
	@Test
	public void equals() {
		salesRequisition.setId(1L);
		
		assertTrue(salesRequisition.equals(new SalesRequisition(1L)));
	}
	
	@Test
	public void createSalesInvoice() {
		salesRequisition.setCustomer(new Customer());
		salesRequisition.setPricingScheme(new PricingScheme());
		salesRequisition.setMode("DELIVERY");
		salesRequisition.setRemarks("REMARKS");
		salesRequisition.setCreateDate(DateUtil.toDate("07/09/2015"));
		salesRequisition.setTransactionDate(DateUtil.toDate("07/10/2015"));
		salesRequisition.setEncoder(new User());
		salesRequisition.setSalesRequisitionNumber(1L);
		salesRequisition.setPaymentTerm(new PaymentTerm());
		
		Product product = mock(Product.class);
		when(product.getFinalCost(Unit.CASE)).thenReturn(new BigDecimal("5"));
		
		SalesRequisitionItem item = mock(SalesRequisitionItem.class);
		when(item.getProduct()).thenReturn(product);
		when(item.getUnit()).thenReturn(Unit.CASE);
		when(item.getQuantity()).thenReturn(5);
		when(item.getUnitPrice()).thenReturn(new BigDecimal("10"));
		
		salesRequisition.getItems().add(item);
		
		SalesInvoice salesInvoice = salesRequisition.createSalesInvoice();
		
		assertSame(salesRequisition.getCustomer(), salesInvoice.getCustomer());
		assertSame(salesRequisition.getPricingScheme(), salesInvoice.getPricingScheme());
		assertEquals(salesRequisition.getMode(), salesInvoice.getMode());
		assertEquals(salesRequisition.getRemarks(), salesInvoice.getRemarks());
		assertEquals(salesRequisition.getCreateDate(), salesInvoice.getCreateDate());
		assertEquals(salesRequisition.getTransactionDate(), salesInvoice.getTransactionDate());
		assertSame(salesRequisition.getEncoder(), salesInvoice.getEncoder());
		assertEquals(salesRequisition.getSalesRequisitionNumber(), salesInvoice.getRelatedSalesRequisitionNumber());
		assertSame(salesRequisition.getPaymentTerm(), salesInvoice.getPaymentTerm());
		
		List<SalesInvoiceItem> salesInvoiceItems = salesInvoice.getItems();
		
		assertEquals(1, salesInvoiceItems.size());
		
		SalesInvoiceItem salesInvoiceItem = salesInvoiceItems.get(0);
	
		assertSame(salesInvoice, salesInvoiceItem.getParent());
		assertSame(item.getProduct(), salesInvoiceItem.getProduct());
		assertEquals(item.getUnit(), salesInvoiceItem.getUnit());
		assertEquals(item.getQuantity(), salesInvoiceItem.getQuantity());
		assertEquals(item.getUnitPrice(), salesInvoiceItem.getUnitPrice());
		assertEquals(product.getFinalCost(Unit.CASE), salesInvoiceItem.getCost());
	}

	@Test
	public void getAllItemProductManufacturers() {
		Manufacturer manufacturer = new Manufacturer(1L);
		manufacturer.setName("MANUFACTURER");
		
		Manufacturer manufacturer2 = new Manufacturer(2L);
		manufacturer2.setName("MANUFACTURER2");
		
		Product product = new Product();
		product.setManufacturer(manufacturer);
		
		Product product2 = new Product();
		product2.setManufacturer(manufacturer2);
		
		SalesRequisitionItem item = new SalesRequisitionItem();
		item.setProduct(product);
		
		SalesRequisitionItem item2 = new SalesRequisitionItem();
		item2.setProduct(product2);
		
		SalesRequisitionItem item3 = new SalesRequisitionItem();
		item3.setProduct(new Product());
		
		salesRequisition.setItems(Arrays.asList(item, item2, item3));
		
		assertEquals(Arrays.asList(manufacturer, manufacturer2), salesRequisition.getAllItemProductManufacturers());
	}
	
	@Test
	public void getSalesByManufacturer() {
		Manufacturer manufacturer = new Manufacturer(1L);
		
		Product product = new Product();
		product.setManufacturer(manufacturer);
		
		SalesRequisitionItem item = mock(SalesRequisitionItem.class);
		when(item.getAmount()).thenReturn(new BigDecimal("10"));
		when(item.getProduct()).thenReturn(product);
		
		Product product2 = new Product();
		product2.setManufacturer(new Manufacturer(2L));
		
		SalesRequisitionItem item2 = mock(SalesRequisitionItem.class);
		when(item2.getAmount()).thenReturn(new BigDecimal("20"));
		when(item2.getProduct()).thenReturn(product2);
		
		salesRequisition.setItems(Arrays.asList(item, item2));
		
		assertEquals(new BigDecimal("10.00"), salesRequisition.getSalesByManufacturer(manufacturer));
	}
	
	@Test
	public void findItemByProductAndUnit() {
		Product product = new Product(1L);
		
		SalesRequisitionItem item = new SalesRequisitionItem();
		item.setProduct(product);
		item.setUnit(Unit.CASE);
		
		salesRequisition.getItems().add(item);
		
		assertSame(item, salesRequisition.findItemByProductAndUnit(product, Unit.CASE));
	}
	
	@Test
	public void findItemByProductAndUnit_productNotMatch() {
		Product product = new Product(1L);
		
		SalesRequisitionItem item = new SalesRequisitionItem();
		item.setProduct(product);
		item.setUnit(Unit.CASE);
		
		salesRequisition.getItems().add(item);
		
		assertNull(salesRequisition.findItemByProductAndUnit(new Product(2L), Unit.CASE));
	}
	
	@Test
	public void findItemByProductAndUnit_unitNotMatch() {
		Product product = new Product(1L);
		
		SalesRequisitionItem item = new SalesRequisitionItem();
		item.setProduct(product);
		item.setUnit(Unit.CASE);
		
		salesRequisition.getItems().add(item);
		
		assertNull(salesRequisition.findItemByProductAndUnit(product, Unit.CARTON));
	}
	
}