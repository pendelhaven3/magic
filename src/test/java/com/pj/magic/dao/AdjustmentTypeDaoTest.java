package com.pj.magic.dao;

import static org.junit.Assert.*;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.pj.magic.model.AdjustmentType;

public class AdjustmentTypeDaoTest extends IntegrationTest {

	@Autowired private AdjustmentTypeDao adjustmentTypeDao;
	
	@Test
	public void insert() {
		AdjustmentType type = new AdjustmentType();
		type.setCode("NMS");
		type.setDescription("NO MORE STOCK");
		adjustmentTypeDao.save(type);
	}
	
	@Test
	public void update() {
		AdjustmentType type = new AdjustmentType();
		type.setId(1L);
		type.setCode("NMS");
		type.setDescription("NO MORE STOCK");
		adjustmentTypeDao.save(type);
	}
	
	@Test
	public void get() {
		assertNotNull(adjustmentTypeDao.get(1L));
	}
	
	@Test
	public void getAll() {
		assertFalse(adjustmentTypeDao.getAll().isEmpty());
	}
	
	@Test
	public void findByCode() {
		adjustmentTypeDao.findByCode("NMS");
	}
	
}