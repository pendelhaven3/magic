package com.pj.magic.service.impl;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pj.magic.dao.AdjustmentTypeDao;
import com.pj.magic.model.AdjustmentType;
import com.pj.magic.service.AdjustmentTypeService;

@Service
public class AdjustmentTypeServiceImpl implements AdjustmentTypeService {

	@Autowired private AdjustmentTypeDao adjustmentTypeDao;
	
	@Override
	public List<AdjustmentType> getAllAdjustmentTypes() {
		return adjustmentTypeDao.getAll();
	}

	@Override
	public AdjustmentType getType(long id) {
		return adjustmentTypeDao.get(id);
	}

	@Transactional
	@Override
	public void save(AdjustmentType type) {
		adjustmentTypeDao.save(type);
	}

	@Override
	public AdjustmentType findAdjustmentTypeByCode(String code) {
		return adjustmentTypeDao.findByCode(code);
	}

}