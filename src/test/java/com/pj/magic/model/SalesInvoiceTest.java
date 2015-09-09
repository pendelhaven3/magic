package com.pj.magic.model;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;

import com.pj.magic.util.DateUtil;

public class SalesInvoiceTest {

	private SalesInvoice salesInvoice;
	
	@Before
	public void setUp() {
		salesInvoice = new SalesInvoice();
	}
	
	@Test
	public void getTotalAmount() {
		SalesInvoiceItem item = mock(SalesInvoiceItem.class);
		when(item.getAmount()).thenReturn(new BigDecimal("10"));
		
		SalesInvoiceItem item2 = mock(SalesInvoiceItem.class);
		when(item2.getAmount()).thenReturn(new BigDecimal("20"));

		salesInvoice.setItems(Arrays.asList(item, item2));
		
		assertEquals(new BigDecimal("30"), salesInvoice.getTotalAmount());
	}
	
	@Test
	public void getTotalNumberOfItems() {
		salesInvoice.setItems(Arrays.asList(new SalesInvoiceItem(), new SalesInvoiceItem()));
		
		assertEquals(2, salesInvoice.getTotalNumberOfItems());
	}
	
	@Test
	public void hasItems_true() {
		salesInvoice.setItems(Arrays.asList(new SalesInvoiceItem()));
		
		assertTrue(salesInvoice.hasItems());
	}
	
	@Test
	public void hasItems_false() {
		assertFalse(salesInvoice.hasItems());
	}
	
	@Test
	public void getTotalQuantity() {
		SalesInvoiceItem item = new SalesInvoiceItem();
		item.setQuantity(1);

		SalesInvoiceItem item2 = new SalesInvoiceItem();
		item2.setQuantity(2);
		
		salesInvoice.setItems(Arrays.asList(item, item2));
		
		assertEquals(3, salesInvoice.getTotalQuantity());
	}
	
	@Test
	public void getTotalDiscounts() {
		SalesInvoiceItem item = mock(SalesInvoiceItem.class);
		when(item.getDiscountedAmount()).thenReturn(new BigDecimal("10"));
		
		SalesInvoiceItem item2 = mock(SalesInvoiceItem.class);
		when(item2.getDiscountedAmount()).thenReturn(new BigDecimal("20"));

		salesInvoice.setItems(Arrays.asList(item, item2));
		
		assertEquals(new BigDecimal("30.00"), salesInvoice.getTotalDiscounts());
	}
	
	@Test
	public void getTotalNetAmount() {
		SalesInvoiceItem item = mock(SalesInvoiceItem.class);
		when(item.getNetAmount()).thenReturn(new BigDecimal("10"));
		
		SalesInvoiceItem item2 = mock(SalesInvoiceItem.class);
		when(item2.getNetAmount()).thenReturn(new BigDecimal("20"));

		salesInvoice.setItems(Arrays.asList(item, item2));
		
		assertEquals(new BigDecimal("30.00"), salesInvoice.getTotalNetAmount());
	}
	
	@Test
	public void createSalesRequisition() {
		salesInvoice.setCustomer(new Customer());
		salesInvoice.setPaymentTerm(new PaymentTerm());
		salesInvoice.setPricingScheme(new PricingScheme());
		salesInvoice.setMode("DELIVERY");
		salesInvoice.setRemarks("REMARKS");
		
		SalesInvoiceItem item = new SalesInvoiceItem();
		item.setProduct(new Product());
		item.setUnit(Unit.CASE);
		item.setQuantity(1);

		SalesInvoiceItem item2 = new SalesInvoiceItem();
		item2.setProduct(new Product());
		item2.setUnit(Unit.CARTON);
		item2.setQuantity(2);

		salesInvoice.setItems(Arrays.asList(item, item2));
		
		SalesRequisition salesRequisition = salesInvoice.createSalesRequisition();
		assertSame(salesRequisition.getCustomer(), salesInvoice.getCustomer());
		assertSame(salesRequisition.getPaymentTerm(), salesInvoice.getPaymentTerm());
		assertSame(salesRequisition.getPricingScheme(), salesInvoice.getPricingScheme());
		assertSame(salesRequisition.getMode(), salesInvoice.getMode());
		assertSame(salesRequisition.getRemarks(), salesInvoice.getRemarks());
		assertEquals(2, salesRequisition.getItems().size());
		
		SalesRequisitionItem salesRequisitionItem = salesRequisition.getItems().get(0);
		assertSame(salesRequisitionItem.getParent(), salesRequisition);
		assertSame(salesRequisitionItem.getProduct(), item.getProduct());
		assertSame(salesRequisitionItem.getUnit(), item.getUnit());
		assertEquals(salesRequisitionItem.getQuantity(), item.getQuantity());
		
		SalesRequisitionItem salesRequisitionItem2 = salesRequisition.getItems().get(1);
		assertSame(salesRequisitionItem2.getParent(), salesRequisition);
		assertSame(salesRequisitionItem2.getProduct(), item2.getProduct());
		assertSame(salesRequisitionItem2.getUnit(), item2.getUnit());
		assertEquals(salesRequisitionItem2.getQuantity(), item2.getQuantity());
	}
	
	@Test
	public void getStatus_marked() {
		salesInvoice.setMarked(true);
		
		assertEquals("Marked", salesInvoice.getStatus());
	}

	@Test
	public void getStatus_cancelled() {
		salesInvoice.setCancelled(true);
		
		assertEquals("Cancelled", salesInvoice.getStatus());
	}
	
	@Test
	public void getStatus_new() {
		assertEquals("New", salesInvoice.getStatus());
	}
	
	@Test
	public void isNew() {
		assertTrue(salesInvoice.isNew());
	}
	
	@Test
	public void isNew_marked() {
		salesInvoice.setMarked(true);
		
		assertFalse(salesInvoice.isNew());
	}

	@Test
	public void isNew_cancelled() {
		salesInvoice.setCancelled(true);
		
		assertFalse(salesInvoice.isNew());
	}
	
	@Test
	public void getVatableSales() {
		SalesInvoiceItem item = mock(SalesInvoiceItem.class);
		when(item.getNetAmount()).thenReturn(new BigDecimal("10"));
		
		SalesInvoiceItem item2 = mock(SalesInvoiceItem.class);
		when(item2.getNetAmount()).thenReturn(new BigDecimal("20"));

		salesInvoice.setItems(Arrays.asList(item, item2));
		salesInvoice.setVatAmount(new BigDecimal("3.21"));
		
		assertEquals(new BigDecimal("26.79"), salesInvoice.getVatableSales());
	}
	
	@Test
	public void getDueDate() {
		salesInvoice.setTransactionDate(DateUtil.toDate("07/07/2015"));
		
		PaymentTerm paymentTerm = new PaymentTerm();
		paymentTerm.setNumberOfDays(7);
		salesInvoice.setPaymentTerm(paymentTerm);
		
		assertEquals(DateUtil.toDate("07/14/2015"), salesInvoice.getDueDate());
	}
	
	@Test
	public void getTotalNetCost() {
		SalesInvoiceItem item = mock(SalesInvoiceItem.class);
		when(item.getNetCost()).thenReturn(new BigDecimal("10"));
		
		SalesInvoiceItem item2 = mock(SalesInvoiceItem.class);
		when(item2.getNetCost()).thenReturn(new BigDecimal("20"));

		salesInvoice.setItems(Arrays.asList(item, item2));
		
		assertEquals(new BigDecimal("30.00"), salesInvoice.getTotalNetCost());
	}

	@Test
	public void getTotalNetProfit() {
		SalesInvoiceItem item = mock(SalesInvoiceItem.class);
		when(item.getNetAmount()).thenReturn(new BigDecimal("10"));
		when(item.getNetCost()).thenReturn(new BigDecimal("5"));
		
		SalesInvoiceItem item2 = mock(SalesInvoiceItem.class);
		when(item2.getNetAmount()).thenReturn(new BigDecimal("20"));
		when(item2.getNetCost()).thenReturn(new BigDecimal("10"));

		salesInvoice.setItems(Arrays.asList(item, item2));
		
		assertEquals(new BigDecimal("15.00"), salesInvoice.getTotalNetProfit());
	}

	@Test
	public void hasProduct() {
		Product product = new Product();
		product.setCode("CODE");
		
		SalesInvoiceItem item = new SalesInvoiceItem();
		item.setProduct(product);
		
		salesInvoice.getItems().add(item);
		
		assertTrue(salesInvoice.hasProduct(product));
	}

	@Test
	public void hasProduct_false() {
		Product product = new Product();
		product.setCode("CODE");
		
		SalesInvoiceItem item = new SalesInvoiceItem();
		item.setProduct(product);
		
		salesInvoice.getItems().add(item);
		
		Product searchProduct = new Product();
		searchProduct.setCode("CODE2");
		
		assertFalse(salesInvoice.hasProduct(searchProduct));
	}
	
	@Test
	public void findItemByProductAndUnit() {
		Product product = new Product();
		product.setCode("CODE");
		
		SalesInvoiceItem item = new SalesInvoiceItem();
		item.setProduct(product);
		item.setUnit(Unit.CASE);
		
		salesInvoice.getItems().add(item);
		
		assertSame(item, salesInvoice.findItemByProductAndUnit(product, Unit.CASE));
	}

	@Test
	public void findItemByProductAndUnit_productNotMatch() {
		Product product = new Product(1L);
		product.setCode("CODE");
		
		SalesInvoiceItem item = new SalesInvoiceItem();
		item.setProduct(product);
		item.setUnit(Unit.CASE);
		
		salesInvoice.getItems().add(item);
		
		Product searchProduct = new Product();
		searchProduct.setCode("CODE2");
		
		assertNull(salesInvoice.findItemByProductAndUnit(searchProduct, Unit.CASE));
	}
	
	@Test
	public void findItemByProductAndUnit_unitNotMatch() {
		Product product = new Product();
		product.setCode("CODE");
		
		SalesInvoiceItem item = new SalesInvoiceItem();
		item.setProduct(product);
		item.setUnit(Unit.CASE);
		
		salesInvoice.getItems().add(item);
		
		assertNull(salesInvoice.findItemByProductAndUnit(product, Unit.CARTON));
	}
	
	@Test
	public void hasProductAndUnit() {
		Product product = new Product();
		product.setCode("CODE");
		
		SalesInvoiceItem item = new SalesInvoiceItem();
		item.setProduct(product);
		item.setUnit(Unit.CASE);
		
		salesInvoice.getItems().add(item);
		
		assertTrue(salesInvoice.hasProductAndUnit(product, Unit.CASE));
	}

	@Test
	public void hasProductAndUnit_false() {
		Product product = new Product();
		product.setCode("CODE");
		
		SalesInvoiceItem item = new SalesInvoiceItem();
		item.setProduct(product);
		item.setUnit(Unit.CASE);
		
		salesInvoice.getItems().add(item);
		
		assertFalse(salesInvoice.hasProductAndUnit(product, Unit.CARTON));
	}
	
	@Test
	public void getSalesByManufacturer() {
		Manufacturer manufacturer = new Manufacturer(1L);
		
		Product product = new Product();
		product.setManufacturer(manufacturer);
		
		SalesInvoiceItem item = mock(SalesInvoiceItem.class);
		when(item.getNetAmount()).thenReturn(new BigDecimal("10"));
		when(item.getProduct()).thenReturn(product);
		
		Product product2 = new Product();
		product2.setManufacturer(new Manufacturer(2L));
		
		SalesInvoiceItem item2 = mock(SalesInvoiceItem.class);
		when(item2.getNetAmount()).thenReturn(new BigDecimal("20"));
		when(item2.getProduct()).thenReturn(product2);
		
		salesInvoice.setItems(Arrays.asList(item, item2));
		
		assertEquals(new BigDecimal("10.00"), salesInvoice.getSalesByManufacturer(manufacturer));
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
		
		SalesInvoiceItem item = new SalesInvoiceItem();
		item.setProduct(product);
		
		SalesInvoiceItem item2 = new SalesInvoiceItem();
		item2.setProduct(product2);
		
		SalesInvoiceItem item3 = new SalesInvoiceItem();
		item3.setProduct(new Product());
		
		salesInvoice.setItems(Arrays.asList(item, item2, item3));
		
		assertEquals(Arrays.asList(manufacturer, manufacturer2), salesInvoice.getAllItemProductManufacturers());
	}
	
	@Test
	public void findItemByProduct() {
		Product product = new Product();
		product.setCode("CODE");
		
		SalesInvoiceItem item = new SalesInvoiceItem();
		item.setProduct(product);
		
		salesInvoice.getItems().add(item);
		
		assertSame(item, salesInvoice.findItemByProduct(product));
	}
	
	@Test
	public void findItemByProduct_noMatch() {
		Product product = new Product();
		product.setCode("CODE");
		
		SalesInvoiceItem item = new SalesInvoiceItem();
		item.setProduct(product);
		
		salesInvoice.getItems().add(item);
		
		Product searchProduct = new Product();
		searchProduct.setCode("CODE2");
		
		assertNull(salesInvoice.findItemByProduct(searchProduct));
	}
	
}