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

import com.pj.magic.dao.AdjustmentInDao;
import com.pj.magic.dao.AdjustmentInItemDao;
import com.pj.magic.dao.ProductDao;
import com.pj.magic.dao.SystemDao;
import com.pj.magic.model.AdjustmentIn;
import com.pj.magic.model.AdjustmentInItem;
import com.pj.magic.model.Product;
import com.pj.magic.model.Unit;
import com.pj.magic.model.UnitQuantity;
import com.pj.magic.model.User;
import com.pj.magic.model.search.AdjustmentInSearchCriteria;
import com.pj.magic.service.impl.AdjustmentInServiceImpl;

@RunWith(MockitoJUnitRunner.class)
public class AdjustmentInServiceTest {

	private AdjustmentInService service;
	
	@Mock private AdjustmentInDao adjustmentInDao;
	@Mock private AdjustmentInItemDao adjustmentInItemDao;
	@Mock private LoginService loginService;
	@Mock private ProductDao productDao;
	@Mock private SystemDao systemDao;
	
	@Before
	public void setUp() throws Exception {
		service = new AdjustmentInServiceImpl();
		
		ReflectionTestUtils.setField(service, "adjustmentInDao", adjustmentInDao);
		ReflectionTestUtils.setField(service, "adjustmentInItemDao", adjustmentInItemDao);
		ReflectionTestUtils.setField(service, "loginService", loginService);
		ReflectionTestUtils.setField(service, "productDao", productDao);
		ReflectionTestUtils.setField(service, "systemDao", systemDao);
	}
	
	@Test
	public void saveAdjustmentIn() {
		AdjustmentIn adjustmentIn = new AdjustmentIn();
		service.save(adjustmentIn);
		
		verify(adjustmentInDao).save(adjustmentIn);
	}
	
	@Test
	public void getAdjustmentIn() {
		long id = 1L;
		AdjustmentIn expected = new AdjustmentIn();
		
		when(adjustmentInDao.get(id)).thenReturn(expected);
		
		AdjustmentIn result = service.getAdjustmentIn(id);
		assertSame(expected, result);
	}
	
	@Test
	public void saveAdjustmentInItem() {
		AdjustmentInItem item = new AdjustmentInItem();
		service.save(item);
		
		verify(adjustmentInItemDao).save(item);
	}
	
	@Test
	public void deleteAdjustmentInItem() {
		AdjustmentInItem item = new AdjustmentInItem();
		service.delete(item);
		
		verify(adjustmentInItemDao).delete(item);
	}
	
	@Test
	public void deleteAdjustmentIn() {
		AdjustmentIn adjustmentIn = new AdjustmentIn();
		service.delete(adjustmentIn);
		
		verify(adjustmentInDao).delete(adjustmentIn);
		verify(adjustmentInItemDao).deleteAllByAdjustmentIn(adjustmentIn);
	}
	
	@Test
	public void post() {
		long adjustmentInId = 1L;
		AdjustmentIn original = new AdjustmentIn(adjustmentInId);
		AdjustmentIn fromDb = new AdjustmentIn(adjustmentInId);
		User loggedInUser = new User();
		
		long productId = 10L;
		Product product = new Product(productId);
		product.getUnitQuantities().add(new UnitQuantity(Unit.CASE, 1));
		
		AdjustmentInItem item = new AdjustmentInItem();
		item.setProduct(product);
		item.setUnit(Unit.CASE);
		item.setQuantity(1);
		
		when(adjustmentInDao.get(original.getId())).thenReturn(fromDb);
		when(adjustmentInItemDao.findAllByAdjustmentIn(fromDb)).thenReturn(Arrays.asList(item));
		when(loginService.getLoggedInUser()).thenReturn(loggedInUser);
		when(productDao.get(productId)).thenReturn(product);
		when(systemDao.getCurrentDateTime()).thenReturn(new Date());
		
		service.post(original);
		
		verify(adjustmentInDao).save(fromDb);
		assertTrue(fromDb.isPosted());
		assertTrue(DateUtils.isSameDay(new Date(), fromDb.getPostDate()));
		assertSame(loggedInUser, fromDb.getPostedBy());
		
		verify(productDao).updateAvailableQuantities(argThat(new ArgumentMatcher<Product>() {

			@Override
			public boolean matches(Object argument) {
				return ((Product)argument).getUnitQuantity(Unit.CASE) == 2;
			}
		}));
	}
	
	@Test
	public void getAllNonPostedAdjustmentIns() {
		List<AdjustmentIn> expected = Arrays.asList(new AdjustmentIn());
		
		when(adjustmentInDao.search(any(AdjustmentInSearchCriteria.class))).thenReturn(expected);
		
		List<AdjustmentIn> result = service.getAllNonPostedAdjustmentIns();
		assertSame(expected, result);
		
		verify(adjustmentInDao).search(argThat(new ArgumentMatcher<AdjustmentInSearchCriteria>() {

			@Override
			public boolean matches(Object argument) {
				return !((AdjustmentInSearchCriteria)argument).getPosted();
			}
		}));
	}
	
	@Test
	public void search() {
		AdjustmentInSearchCriteria criteria = new AdjustmentInSearchCriteria();
		service.search(criteria);
		
		verify(adjustmentInDao).search(criteria);
	}
	
}