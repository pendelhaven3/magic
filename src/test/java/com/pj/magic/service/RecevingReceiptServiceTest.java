package com.pj.magic.service;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.util.Arrays;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.pj.magic.dao.ProductDao;
import com.pj.magic.dao.ReceivingReceiptDao;
import com.pj.magic.dao.ReceivingReceiptItemDao;
import com.pj.magic.model.Product;
import com.pj.magic.model.ReceivingReceipt;
import com.pj.magic.model.ReceivingReceiptItem;
import com.pj.magic.model.Unit;
import com.pj.magic.service.impl.ReceivingReceiptServiceImpl;

@Ignore
public class RecevingReceiptServiceTest {

	private ReceivingReceiptServiceImpl service;
	
	@Mock private ProductDao productDao;
	@Mock private ReceivingReceiptDao receivingReceiptDao;
	@Mock private ReceivingReceiptItemDao receivingReceiptItemDao;

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
		service = new ReceivingReceiptServiceImpl();
		service.setProductDao(productDao);
		service.setReceivingReceiptDao(receivingReceiptDao);
		service.setReceivingReceiptItemDao(receivingReceiptItemDao);
	}
	
	@Test
	public void finalCostOfHighestUnitUpdated() {
		Product product = new Product();
		product.setId(1L);
		product.addUnit(Unit.CASE);
		product.addUnit(Unit.CARTON);
		product.addUnit(Unit.PIECES);
		product.setUnitConversion(Unit.CASE, 36);
		product.setUnitConversion(Unit.CARTON, 6);
		product.setUnitConversion(Unit.PIECES, 1);
		product.setFinalCost(Unit.CASE, new BigDecimal("36.00"));
		product.setFinalCost(Unit.CARTON, new BigDecimal("6.00"));
		product.setFinalCost(Unit.PIECES, new BigDecimal("1.00"));
		product.addUnitQuantity(Unit.CASE, 5);
		
		ReceivingReceipt receivingReceipt = new ReceivingReceipt();
		receivingReceipt.setId(1L);
		
		ReceivingReceiptItem item = new ReceivingReceiptItem();
		item.setProduct(new Product(1L));
		item.setUnit(Unit.CASE);
		item.setCost(new BigDecimal("72.00"));
		item.setQuantity(1);
		receivingReceipt.getItems().add(item);
		
		when(productDao.get(1L)).thenReturn(product);
		when(receivingReceiptDao.get(1L)).thenReturn(receivingReceipt);
		when(receivingReceiptItemDao.findAllByReceivingReceipt(receivingReceipt))
			.thenReturn(Arrays.asList(item));
		
		service.post(receivingReceipt);
		
		assertEquals(new BigDecimal("72.00"), product.getFinalCost(Unit.CASE));
		assertEquals(new BigDecimal("12.00"), product.getFinalCost(Unit.CARTON));
		assertEquals(new BigDecimal("2.00"), product.getFinalCost(Unit.PIECES));
		assertEquals(6, product.getUnitQuantity(Unit.CASE));
		verify(productDao).updateCosts(product);
		verify(productDao).updateAvailableQuantities(product);
	}

	@Test
	public void finalCostOfMiddleUnitUpdated() {
		Product product = new Product();
		product.setId(1L);
		product.addUnit(Unit.CASE);
		product.addUnit(Unit.CARTON);
		product.addUnit(Unit.PIECES);
		product.setUnitConversion(Unit.CASE, 36);
		product.setUnitConversion(Unit.CARTON, 6);
		product.setUnitConversion(Unit.PIECES, 1);
		product.setFinalCost(Unit.CASE, new BigDecimal("36.00"));
		product.setFinalCost(Unit.CARTON, new BigDecimal("6.00"));
		product.setFinalCost(Unit.PIECES, new BigDecimal("1.00"));
		product.addUnitQuantity(Unit.CARTON, 5);
		
		ReceivingReceipt receivingReceipt = new ReceivingReceipt();
		receivingReceipt.setId(1L);
		
		ReceivingReceiptItem item = new ReceivingReceiptItem();
		item.setProduct(new Product(1L));
		item.setUnit(Unit.CARTON);
		item.setCost(new BigDecimal("12.00"));
		item.setQuantity(1);
		receivingReceipt.getItems().add(item);
		
		when(productDao.get(1L)).thenReturn(product);
		when(receivingReceiptDao.get(1L)).thenReturn(receivingReceipt);
		when(receivingReceiptItemDao.findAllByReceivingReceipt(receivingReceipt))
			.thenReturn(Arrays.asList(item));
		
		service.post(receivingReceipt);
		
		assertEquals(new BigDecimal("36.00"), product.getFinalCost(Unit.CASE));
		assertEquals(new BigDecimal("12.00"), product.getFinalCost(Unit.CARTON));
		assertEquals(new BigDecimal("2.00"), product.getFinalCost(Unit.PIECES));
		assertEquals(6, product.getUnitQuantity(Unit.CARTON));
		verify(productDao).updateCosts(product);
		verify(productDao).updateAvailableQuantities(product);
	}

	@Test
	public void finalCostOfLowestUnitUpdated() {
		Product product = new Product();
		product.setId(1L);
		product.addUnit(Unit.CASE);
		product.addUnit(Unit.CARTON);
		product.addUnit(Unit.PIECES);
		product.setUnitConversion(Unit.CASE, 36);
		product.setUnitConversion(Unit.CARTON, 6);
		product.setUnitConversion(Unit.PIECES, 1);
		product.setFinalCost(Unit.CASE, new BigDecimal("36.00"));
		product.setFinalCost(Unit.CARTON, new BigDecimal("6.00"));
		product.setFinalCost(Unit.PIECES, new BigDecimal("1.00"));
		product.addUnitQuantity(Unit.PIECES, 5);
		
		ReceivingReceipt receivingReceipt = new ReceivingReceipt();
		receivingReceipt.setId(1L);
		
		ReceivingReceiptItem item = new ReceivingReceiptItem();
		item.setProduct(new Product(1L));
		item.setUnit(Unit.PIECES);
		item.setCost(new BigDecimal("2.00"));
		item.setQuantity(1);
		receivingReceipt.getItems().add(item);
		
		when(productDao.get(1L)).thenReturn(product);
		when(receivingReceiptDao.get(1L)).thenReturn(receivingReceipt);
		when(receivingReceiptItemDao.findAllByReceivingReceipt(receivingReceipt))
			.thenReturn(Arrays.asList(item));
		
		service.post(receivingReceipt);
		
		assertEquals(new BigDecimal("36.00"), product.getFinalCost(Unit.CASE));
		assertEquals(new BigDecimal("6.00"), product.getFinalCost(Unit.CARTON));
		assertEquals(new BigDecimal("2.00"), product.getFinalCost(Unit.PIECES));
		assertEquals(6, product.getUnitQuantity(Unit.PIECES));
		verify(productDao).updateCosts(product);
		verify(productDao).updateAvailableQuantities(product);
	}

}
