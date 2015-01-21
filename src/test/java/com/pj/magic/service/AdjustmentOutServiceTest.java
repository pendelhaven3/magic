package com.pj.magic.service;

import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.time.DateUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatcher;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.test.util.ReflectionTestUtils;

import com.pj.magic.dao.AdjustmentOutDao;
import com.pj.magic.dao.AdjustmentOutItemDao;
import com.pj.magic.dao.ProductDao;
import com.pj.magic.exception.NotEnoughStocksException;
import com.pj.magic.model.AdjustmentOut;
import com.pj.magic.model.AdjustmentOutItem;
import com.pj.magic.model.Product;
import com.pj.magic.model.Unit;
import com.pj.magic.model.UnitQuantity;
import com.pj.magic.model.User;
import com.pj.magic.model.search.AdjustmentOutSearchCriteria;
import com.pj.magic.service.impl.AdjustmentOutServiceImpl;

@RunWith(MockitoJUnitRunner.class)
public class AdjustmentOutServiceTest {

	private AdjustmentOutService service;
	
	@Mock private AdjustmentOutDao adjustmentOutDao;
	@Mock private AdjustmentOutItemDao adjustmentOutItemDao;
	@Mock private LoginService loginService;
	@Mock private ProductDao productDao;
	
	@Before
	public void setUp() throws Exception {
		service = new AdjustmentOutServiceImpl();
		
		ReflectionTestUtils.setField(service, "adjustmentOutDao", adjustmentOutDao);
		ReflectionTestUtils.setField(service, "adjustmentOutItemDao", adjustmentOutItemDao);
		ReflectionTestUtils.setField(service, "loginService", loginService);
		ReflectionTestUtils.setField(service, "productDao", productDao);
	}
	
	@Test
	public void saveAdjustmentOut() {
		AdjustmentOut adjustmentOut = new AdjustmentOut();
		service.save(adjustmentOut);
		
		verify(adjustmentOutDao).save(adjustmentOut);
	}
	
	@Test
	public void getAdjustmentOut() {
		long id = 1L;
		AdjustmentOut expected = new AdjustmentOut();
		
		when(adjustmentOutDao.get(id)).thenReturn(expected);
		
		AdjustmentOut result = service.getAdjustmentOut(id);
		assertSame(expected, result);
	}
	
	@Test
	public void saveAdjustmentOutItem() {
		AdjustmentOutItem item = new AdjustmentOutItem();
		service.save(item);
		
		verify(adjustmentOutItemDao).save(item);
	}
	
	@Test
	public void deleteAdjustmentOutItem() {
		AdjustmentOutItem item = new AdjustmentOutItem();
		service.delete(item);
		
		verify(adjustmentOutItemDao).delete(item);
	}
	
	@Test
	public void deleteAdjustmentOut() {
		AdjustmentOut adjustmentOut = new AdjustmentOut();
		service.delete(adjustmentOut);
		
		verify(adjustmentOutDao).delete(adjustmentOut);
		verify(adjustmentOutItemDao).deleteAllByAdjustmentOut(adjustmentOut);
	}
	
	@Test
	public void post() {
		long adjustmentOutId = 1L;
		AdjustmentOut original = new AdjustmentOut(adjustmentOutId);
		AdjustmentOut fromDb = new AdjustmentOut(adjustmentOutId);
		User loggedInUser = new User();
		
		long productId = 10L;
		Product product = new Product(productId);
		product.getUnitQuantities().add(new UnitQuantity(Unit.CASE, 1));
		
		AdjustmentOutItem item = new AdjustmentOutItem();
		item.setProduct(product);
		item.setUnit(Unit.CASE);
		item.setQuantity(1);
		
		when(adjustmentOutDao.get(original.getId())).thenReturn(fromDb);
		when(adjustmentOutItemDao.findAllByAdjustmentOut(fromDb)).thenReturn(Arrays.asList(item));
		when(loginService.getLoggedInUser()).thenReturn(loggedInUser);
		when(productDao.get(productId)).thenReturn(product);
		
		service.post(original);
		
		verify(adjustmentOutDao).save(fromDb);
		assertTrue(fromDb.isPosted());
		assertTrue(DateUtils.isSameDay(new Date(), fromDb.getPostDate()));
		assertSame(loggedInUser, fromDb.getPostedBy());
		
		verify(productDao).updateAvailableQuantities(product);
		assertEquals(0, product.getUnitQuantity(Unit.CASE));
	}

	@Test()
	public void post_withNotEnoughStocksException() {
		long adjustmentOutId = 1L;
		AdjustmentOut original = new AdjustmentOut(adjustmentOutId);
		AdjustmentOut fromDb = new AdjustmentOut(adjustmentOutId);
		User loggedInUser = new User();
		
		long productId = 10L;
		Product product = new Product(productId);
		product.getUnitQuantities().add(new UnitQuantity(Unit.CASE, 0));
		
		AdjustmentOutItem item = new AdjustmentOutItem();
		item.setProduct(product);
		item.setUnit(Unit.CASE);
		item.setQuantity(1);
		
		when(adjustmentOutDao.get(original.getId())).thenReturn(fromDb);
		when(adjustmentOutItemDao.findAllByAdjustmentOut(fromDb)).thenReturn(Arrays.asList(item));
		when(loginService.getLoggedInUser()).thenReturn(loggedInUser);
		when(productDao.get(productId)).thenReturn(product);
		
		try {
			service.post(original);
			fail("Should have thrown NotEnoughStocksException");
		} catch (NotEnoughStocksException e) {
		}
	}
	
	@Test
	public void getAllNonPostedAdjustmentOuts() {
		List<AdjustmentOut> expected = Arrays.asList(new AdjustmentOut());
		
		when(adjustmentOutDao.search(any(AdjustmentOutSearchCriteria.class))).thenReturn(expected);
		
		List<AdjustmentOut> result = service.getAllNonPostedAdjustmentOuts();
		assertSame(expected, result);
		
		verify(adjustmentOutDao).search(argThat(new ArgumentMatcher<AdjustmentOutSearchCriteria>() {

			@Override
			public boolean matches(Object argument) {
				return !((AdjustmentOutSearchCriteria)argument).getPosted();
			}
		}));
	}
	
	@Test
	public void search() {
		AdjustmentOutSearchCriteria criteria = new AdjustmentOutSearchCriteria();
		service.search(criteria);
		
		verify(adjustmentOutDao).search(criteria);
	}
	
}