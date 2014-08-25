package com.pj.magic.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pj.magic.dao.StockQuantityConversionDao;
import com.pj.magic.model.StockQuantityConversion;
import com.pj.magic.model.StockQuantityConversionItem;
import com.pj.magic.service.StockQuantityConversionService;

@Service
public class StockQuantityConversionServiceImpl implements StockQuantityConversionService {

	@Autowired private StockQuantityConversionDao stockQuantityConversionDao;
	
	@Transactional
	@Override
	public void save(StockQuantityConversion stockQuantityConversion) {
		stockQuantityConversionDao.save(stockQuantityConversion);
	}

	@Override
	public StockQuantityConversion getStockQuantityConversion(long id) {
		return stockQuantityConversionDao.get(id);
	}

	@Override
	public List<StockQuantityConversion> getAllStockQuantityConversions() {
		return stockQuantityConversionDao.getAll();
	}

	@Transactional
	@Override
	public void delete(StockQuantityConversion stockQuantityConversion) {
		// TODO Auto-generated method stub
		
	}

	@Transactional
	@Override
	public void post(StockQuantityConversion stockQuantityConversion) {
		// TODO Auto-generated method stub
		
	}

	@Transactional
	@Override
	public void save(StockQuantityConversionItem item) {
		// TODO Auto-generated method stub
		
	}

	@Transactional
	@Override
	public void delete(StockQuantityConversionItem item) {
		// TODO Auto-generated method stub
		
	}

}
