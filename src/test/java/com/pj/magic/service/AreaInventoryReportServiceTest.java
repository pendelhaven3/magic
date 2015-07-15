package com.pj.magic.service;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.test.util.ReflectionTestUtils;

import com.pj.magic.dao.AreaInventoryReportDao;
import com.pj.magic.dao.AreaInventoryReportItemDao;
import com.pj.magic.dao.ProductDao;
import com.pj.magic.model.AreaInventoryReport;
import com.pj.magic.model.AreaInventoryReportItem;
import com.pj.magic.model.InventoryCheck;
import com.pj.magic.model.Product;
import com.pj.magic.model.User;
import com.pj.magic.model.search.AreaInventoryReportSearchCriteria;
import com.pj.magic.service.impl.AreaInventoryReportServiceImpl;

@RunWith(MockitoJUnitRunner.class)
public class AreaInventoryReportServiceTest {

	private AreaInventoryReportService service;
	
	@Mock private AreaInventoryReportDao areaInventoryReportDao;
	@Mock private AreaInventoryReportItemDao areaInventoryReportItemDao;
	@Mock private ProductDao productDao;
	@Mock private LoginService loginService;
	
	@Before
	public void setUp() {
		service = new AreaInventoryReportServiceImpl();
		
		ReflectionTestUtils.setField(service, "areaInventoryReportDao", areaInventoryReportDao);
		ReflectionTestUtils.setField(service, "areaInventoryReportItemDao", areaInventoryReportItemDao);
		ReflectionTestUtils.setField(service, "productDao", productDao);
		ReflectionTestUtils.setField(service, "loginService", loginService);
	}
	
	@Test
	public void getAll() {
		List<AreaInventoryReport> reports = new ArrayList<>();
		when(areaInventoryReportDao.getAll()).thenReturn(reports);
		
		assertSame(reports, service.getAllAreaInventoryReports());
	}
	
	@Test
	public void save_areaInventoryReport_new() {
		AreaInventoryReport report = new AreaInventoryReport();
		User user = new User();
		
		when(loginService.getLoggedInUser()).thenReturn(user);
		
		service.save(report);
		
		verify(areaInventoryReportDao).save(report);
		assertSame(user, report.getCreatedBy());
	}
	
	@Test
	public void save_areaInventoryReport_existing() {
		AreaInventoryReport report = new AreaInventoryReport();
		report.setId(1L);
		
		service.save(report);
		
		verify(areaInventoryReportDao).save(report);
	}

	@Test
	public void getAreaInventoryReport() {
		
		Product product = new Product();
		Product product2 = new Product();
		
		AreaInventoryReportItem item = new AreaInventoryReportItem();
		item.setProduct(new Product(1L));

		AreaInventoryReportItem item2 = new AreaInventoryReportItem();
		item2.setProduct(new Product(2L));
		
		AreaInventoryReport report = new AreaInventoryReport();
		List<AreaInventoryReportItem> items = Arrays.asList(item, item2);
		
		when(areaInventoryReportDao.get(1L)).thenReturn(report);
		when(areaInventoryReportItemDao.findAllByAreaInventoryReport(report)).thenReturn(items);
		when(productDao.get(1L)).thenReturn(product);
		when(productDao.get(2L)).thenReturn(product2);
		
		AreaInventoryReport result = service.getAreaInventoryReport(1L);
		assertSame(report, result);
		assertSame(items, result.getItems());
		assertSame(product, result.getItems().get(0).getProduct());
		assertSame(product2, result.getItems().get(1).getProduct());
	}

	@Test
	public void delete_areaInventoryReportItem() {
		AreaInventoryReportItem item = new AreaInventoryReportItem();
		
		service.delete(item);
		
		verify(areaInventoryReportItemDao).delete(item);
	}

	@Test
	public void save_areaInventoryReportItem() {
		AreaInventoryReportItem item = new AreaInventoryReportItem();
		
		service.save(item);
		
		verify(areaInventoryReportItemDao).save(item);
	}
	
	@Test
	public void findByInventoryCheckAndReportNumber() {
		AreaInventoryReport report = new AreaInventoryReport();
		when(areaInventoryReportDao.findByInventoryCheckAndReportNumber(any(InventoryCheck.class), anyInt()))
			.thenReturn(report);
		
		AreaInventoryReport result = service.findByInventoryCheckAndReportNumber(new InventoryCheck(), 1);
		assertSame(report, result);
	}
	
	@Test
	public void findAllAreaInventoryReportsByInventoryCheck() {
		InventoryCheck inventoryCheck = new InventoryCheck();
		List<AreaInventoryReport> reports = new ArrayList<>();
		
		when(areaInventoryReportDao.findAllByInventoryCheck(inventoryCheck)).thenReturn(reports);
		
		List<AreaInventoryReport> result = service.findAllAreaInventoryReportsByInventoryCheck(inventoryCheck);
		assertSame(reports, result);
	}
	
	@Test
	public void markAsReviewed() {
		AreaInventoryReport report = new AreaInventoryReport();
		
		service.markAsReviewed(report);
		
		verify(areaInventoryReportDao).save(report);
		assertTrue(report.isReviewed());
	}
	
	@Test
	public void search() {
		AreaInventoryReportSearchCriteria criteria = new AreaInventoryReportSearchCriteria();
		List<AreaInventoryReport> reports = new ArrayList<>();
		
		when(areaInventoryReportDao.search(criteria)).thenReturn(reports);
		
		List<AreaInventoryReport> result = service.search(criteria);
		assertSame(reports, result);
	}
	
}