package com.pj.magic.service;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.util.ArrayList;
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

import com.pj.magic.dao.BadStockReturnDao;
import com.pj.magic.dao.BadStockReturnItemDao;
import com.pj.magic.dao.PaymentTerminalAssignmentDao;
import com.pj.magic.dao.ProductDao;
import com.pj.magic.dao.SystemDao;
import com.pj.magic.model.BadStockReturn;
import com.pj.magic.model.BadStockReturnItem;
import com.pj.magic.model.Product;
import com.pj.magic.model.Unit;
import com.pj.magic.model.User;
import com.pj.magic.model.search.BadStockReturnSearchCriteria;
import com.pj.magic.service.impl.BadStockReturnServiceImpl;

@RunWith(MockitoJUnitRunner.class)
public class BadStockReturnServiceTest {

	private BadStockReturnService service;
	
	@Mock private BadStockReturnDao badStockReturnDao;
	@Mock private BadStockReturnItemDao badStockReturnItemDao;
	@Mock private LoginService loginService;
	@Mock private PaymentTerminalAssignmentDao paymentTerminalAssignmentDao;
	@Mock private ProductDao productDao;
	@Mock private SystemDao systemDao;
	
	@Before
	public void setUp() {
		service = new BadStockReturnServiceImpl();
		
		ReflectionTestUtils.setField(service, "badStockReturnDao", badStockReturnDao);
		ReflectionTestUtils.setField(service, "badStockReturnItemDao", badStockReturnItemDao);
		ReflectionTestUtils.setField(service, "loginService", loginService);
		ReflectionTestUtils.setField(service, "paymentTerminalAssignmentDao", paymentTerminalAssignmentDao);
		ReflectionTestUtils.setField(service, "productDao", productDao);
		ReflectionTestUtils.setField(service, "systemDao", systemDao);
	}
	
	@Test
	public void save_badStockReturn() {
		BadStockReturn badStockReturn = new BadStockReturn();
		
		service.save(badStockReturn);
		
		verify(badStockReturnDao).save(badStockReturn);
	}
	
	@Test
	public void getBadStockReturn() {
		BadStockReturn badStockReturn = new BadStockReturn();
		List<BadStockReturnItem> items = new ArrayList<>();
		
		when(badStockReturnDao.get(1L)).thenReturn(badStockReturn);
		when(badStockReturnItemDao.findAllByBadStockReturn(badStockReturn)).thenReturn(items);
		
		BadStockReturn result = service.getBadStockReturn(1L);
		assertSame(badStockReturn, result);
		assertSame(items, result.getItems());
	}
	
	@Test
	public void getBadStockReturn_recordNotFound() {
		when(badStockReturnDao.get(1L)).thenReturn(null);
		
		assertNull(service.getBadStockReturn(1L));
	}

	@Test
	public void getAllNewBadStockReturns() {
		BadStockReturn badStockReturn = new BadStockReturn();
		BadStockReturn badStockReturn2 = new BadStockReturn();
		List<BadStockReturnItem> items = new ArrayList<>();
		List<BadStockReturnItem> items2 = new ArrayList<>();
		
		List<BadStockReturn> badStockReturns = Arrays.asList(badStockReturn, badStockReturn2);
		
		when(badStockReturnDao.search(argThat(new ArgumentMatcher<BadStockReturnSearchCriteria>() {

			@Override
			public boolean matches(Object argument) {
				return !((BadStockReturnSearchCriteria)argument).getPosted();
			}
			
		}))).thenReturn(badStockReturns);
		when(badStockReturnItemDao.findAllByBadStockReturn(badStockReturn)).thenReturn(items);
		when(badStockReturnItemDao.findAllByBadStockReturn(badStockReturn2)).thenReturn(items2);
		
		List<BadStockReturn> result = service.getAllNewBadStockReturns();
		assertEquals(2, result.size());
		assertSame(badStockReturns, result);
		assertSame(items, result.get(0).getItems());
		assertSame(items2, result.get(1).getItems());
	}
	
	@Test
	public void search() {
		BadStockReturnSearchCriteria criteria = new BadStockReturnSearchCriteria();
		
		BadStockReturn badStockReturn = new BadStockReturn();
		BadStockReturn badStockReturn2 = new BadStockReturn();
		List<BadStockReturnItem> items = new ArrayList<>();
		List<BadStockReturnItem> items2 = new ArrayList<>();
		
		List<BadStockReturn> badStockReturns = Arrays.asList(badStockReturn, badStockReturn2);
		
		when(badStockReturnDao.search(criteria)).thenReturn(badStockReturns);
		when(badStockReturnItemDao.findAllByBadStockReturn(badStockReturn)).thenReturn(items);
		when(badStockReturnItemDao.findAllByBadStockReturn(badStockReturn2)).thenReturn(items2);
		
		List<BadStockReturn> result = service.search(criteria);
		assertEquals(2, result.size());
		assertSame(badStockReturns, result);
		assertSame(items, result.get(0).getItems());
		assertSame(items2, result.get(1).getItems());
	}

	@Test
	public void save_badStockReturnItem() {
		BadStockReturnItem item = new BadStockReturnItem();
		
		service.save(item);
		
		verify(badStockReturnItemDao).save(item);
	}
	
	@Test
	public void delete_badStockReturnItem() {
		BadStockReturnItem item = new BadStockReturnItem();
		
		service.delete(item);
		
		verify(badStockReturnItemDao).delete(item);
	}
	
	@Test
	public void post() {
		BadStockReturn badStockReturn = new BadStockReturn(1L);
		
		BadStockReturnItem item = new BadStockReturnItem();
		item.setUnit(Unit.CASE);
		item.setProduct(new Product(1L));
		
		Product product = mock(Product.class);
		when(product.getFinalCost(Unit.CASE)).thenReturn(new BigDecimal("15"));
		
		List<BadStockReturnItem> items = Arrays.asList(item);
		
		final User loggedInUser = new User();
		
		when(badStockReturnDao.get(1L)).thenReturn(badStockReturn);
		when(badStockReturnItemDao.findAllByBadStockReturn(badStockReturn)).thenReturn(items);
		when(loginService.getLoggedInUser()).thenReturn(loggedInUser);
		when(productDao.get(1L)).thenReturn(product);
		when(systemDao.getCurrentDateTime()).thenReturn(new Date());
		
		service.post(badStockReturn);
		
		verify(badStockReturnDao).save(argThat(new ArgumentMatcher<BadStockReturn>() {

			@Override
			public boolean matches(Object argument) {
				BadStockReturn arg = (BadStockReturn)argument;
				return arg.isPosted() && DateUtils.isSameDay(arg.getPostDate(), new Date())
						&& arg.getPostedBy() == loggedInUser;
			}
		}));
		
		verify(badStockReturnItemDao).save(argThat(new ArgumentMatcher<BadStockReturnItem>() {

			@Override
			public boolean matches(Object argument) {
				BadStockReturnItem arg = (BadStockReturnItem)argument;
				return arg.getCost().equals(new BigDecimal("15"));
			}
		}));
		
	}
	
}