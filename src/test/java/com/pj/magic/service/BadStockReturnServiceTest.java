package com.pj.magic.service;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.test.util.ReflectionTestUtils;

import com.pj.magic.dao.BadStockReturnDao;
import com.pj.magic.dao.BadStockReturnItemDao;
import com.pj.magic.dao.PaymentTerminalAssignmentDao;
import com.pj.magic.dao.ProductDao;
import com.pj.magic.model.BadStockReturn;
import com.pj.magic.model.BadStockReturnItem;
import com.pj.magic.service.impl.BadStockReturnServiceImpl;

@RunWith(MockitoJUnitRunner.class)
public class BadStockReturnServiceTest {

	private BadStockReturnService service;
	
	@Mock private BadStockReturnDao badStockReturnDao;
	@Mock private BadStockReturnItemDao badStockReturnItemDao;
	@Mock private LoginService loginService;
	@Mock private PaymentTerminalAssignmentDao paymentTerminalAssignmentDao;
	@Mock private ProductDao productDao;
	
	@Before
	public void setUp() {
		service = new BadStockReturnServiceImpl();
		
		ReflectionTestUtils.setField(service, "badStockReturnDao", badStockReturnDao);
		ReflectionTestUtils.setField(service, "badStockReturnItemDao", badStockReturnItemDao);
		ReflectionTestUtils.setField(service, "loginService", loginService);
		ReflectionTestUtils.setField(service, "paymentTerminalAssignmentDao", paymentTerminalAssignmentDao);
		ReflectionTestUtils.setField(service, "productDao", productDao);
	}
	
	@Test
	public void save() {
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
	
}