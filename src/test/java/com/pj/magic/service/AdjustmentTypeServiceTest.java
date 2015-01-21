package com.pj.magic.service;

import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.test.util.ReflectionTestUtils;

import com.pj.magic.dao.AdjustmentTypeDao;
import com.pj.magic.model.AdjustmentType;
import com.pj.magic.service.impl.AdjustmentTypeServiceImpl;

@RunWith(MockitoJUnitRunner.class)
public class AdjustmentTypeServiceTest {

	private AdjustmentTypeService service;
	
	@Mock private AdjustmentTypeDao adjustmentTypeDao;
	
	@Before
	public void setUp() {
		service = new AdjustmentTypeServiceImpl();
		
		ReflectionTestUtils.setField(service, "adjustmentTypeDao", adjustmentTypeDao);
	}

	@Test
	public void getAllAdjustmentTypes() {
		List<AdjustmentType> expected = Arrays.asList(new AdjustmentType());
		
		when(adjustmentTypeDao.getAll()).thenReturn(expected);
		
		assertSame(expected, service.getAllAdjustmentTypes());
		verify(adjustmentTypeDao).getAll();
	}
	
	@Test
	public void getType() {
		long id = 1L;
		AdjustmentType expected = new AdjustmentType(id);
		
		when(adjustmentTypeDao.get(id)).thenReturn(expected);
		
		assertSame(expected, service.getType(id));
		verify(adjustmentTypeDao).get(id);
	}
	
	@Test
	public void save() {
		AdjustmentType type = new AdjustmentType();
		
		service.save(type);
		
		verify(adjustmentTypeDao).save(type);
	}
	
	@Test
	public void findAdjustmentTypeByCode() {
		String code = "CODE";
		AdjustmentType expected = new AdjustmentType();
		
		when(adjustmentTypeDao.findByCode(code)).thenReturn(expected);
		
		assertSame(expected, service.findAdjustmentTypeByCode(code));
		verify(adjustmentTypeDao).findByCode(code);
	}
	
}