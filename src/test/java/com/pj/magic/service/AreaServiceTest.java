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

import com.pj.magic.dao.AreaDao;
import com.pj.magic.model.Area;
import com.pj.magic.service.impl.AreaServiceImpl;

@RunWith(MockitoJUnitRunner.class)
public class AreaServiceTest {

	private AreaService service;
	
	@Mock private AreaDao areaDao;
	
	@Before
	public void setUp() {
		service = new AreaServiceImpl();
		
		ReflectionTestUtils.setField(service, "areaDao", areaDao);
	}
	
	@Test
	public void save() {
		Area area = new Area();
		
		service.save(area);
		
		verify(areaDao).save(area);
	}
	
	@Test
	public void getArea() {
		Area area = new Area();
		
		when(areaDao.get(1L)).thenReturn(area);
		
		Area result = service.getArea(1L);
		assertSame(area, result);
	}

	@Test
	public void getAllAreas() {
		List<Area> areas = new ArrayList<>();
		
		when(areaDao.getAll()).thenReturn(areas);
		
		List<Area> result = service.getAllAreas();
		assertSame(areas, result);
	}
	
	@Test
	public void findAreaByName() {
		Area area = new Area();
		
		when(areaDao.findByName(anyString())).thenReturn(area);
		
		Area result = service.findAreaByName("BODEGA");
		assertSame(area, result);
	}
	
}