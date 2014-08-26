package com.pj.magic.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pj.magic.dao.ProductDao;
import com.pj.magic.dao.StockQuantityConversionDao;
import com.pj.magic.dao.StockQuantityConversionItemDao;
import com.pj.magic.model.StockQuantityConversion;
import com.pj.magic.model.StockQuantityConversionItem;
import com.pj.magic.service.StockQuantityConversionService;

@Service
public class StockQuantityConversionServiceImpl implements StockQuantityConversionService {

	@Autowired private StockQuantityConversionDao stockQuantityConversionDao;
	@Autowired private StockQuantityConversionItemDao stockQuantityConversionItemDao;
	@Autowired private ProductDao productDao;
	
	@Transactional
	@Override
	public void save(StockQuantityConversion stockQuantityConversion) {
		stockQuantityConversionDao.save(stockQuantityConversion);
	}

	@Override
	public StockQuantityConversion getStockQuantityConversion(long id) {
		StockQuantityConversion stockQuantityConversion = stockQuantityConversionDao.get(id);
		loadStockQuantityConversionDetails(stockQuantityConversion);
		return stockQuantityConversion;
	}

	private void loadStockQuantityConversionDetails(
			StockQuantityConversion stockQuantityConversion) {
		stockQuantityConversion.setItems(
				stockQuantityConversionItemDao.findAllByStockQuantityConversion(stockQuantityConversion));
		for (StockQuantityConversionItem item : stockQuantityConversion.getItems()) {
			item.setProduct(productDao.get(item.getProduct().getId()));
		}
	}

	@Override
	public List<StockQuantityConversion> getAllStockQuantityConversions() {
		return stockQuantityConversionDao.getAll();
	}

	@Transactional
	@Override
	public void delete(StockQuantityConversion stockQuantityConversion) {
		stockQuantityConversionItemDao.deleteAllByStockQuantityConversion(stockQuantityConversion);
		stockQuantityConversionDao.delete(stockQuantityConversion);
	}

	@Transactional
	@Override
	public void post(StockQuantityConversion stockQuantityConversion) {
		// TODO Auto-generated method stub
		
	}

	@Transactional
	@Override
	public void save(StockQuantityConversionItem item) {
		stockQuantityConversionItemDao.save(item);
	}

	@Transactional
	@Override
	public void delete(StockQuantityConversionItem item) {
		stockQuantityConversionItemDao.delete(item);
	}

}
