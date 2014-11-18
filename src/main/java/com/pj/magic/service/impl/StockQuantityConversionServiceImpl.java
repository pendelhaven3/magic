package com.pj.magic.service.impl;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pj.magic.dao.ProductDao;
import com.pj.magic.dao.StockQuantityConversionDao;
import com.pj.magic.dao.StockQuantityConversionItemDao;
import com.pj.magic.exception.NotEnoughStocksException;
import com.pj.magic.model.Product;
import com.pj.magic.model.StockQuantityConversion;
import com.pj.magic.model.StockQuantityConversionItem;
import com.pj.magic.model.search.StockQuantityConversionSearchCriteria;
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
		StockQuantityConversion updated = getStockQuantityConversion(stockQuantityConversion.getId());
		for (StockQuantityConversionItem item : updated.getItems()) {
			// [PJ 08/26/2014] Do not update product quantity inside stock quantity conversion object
			// because it has to be "rolled back" manually when an exception happens during posting
			Product product = productDao.get(item.getProduct().getId());
			if (!product.hasAvailableUnitQuantity(item.getFromUnit(), item.getQuantity())) {
				throw new NotEnoughStocksException(item);
			} else {
				product.subtractUnitQuantity(item.getFromUnit(), item.getQuantity());
				product.addUnitQuantity(item.getToUnit(), item.getConvertedQuantity());
				productDao.updateAvailableQuantities(product);
			}
		}
		updated.setPosted(true);
		updated.setPostDate(new Date());
		stockQuantityConversionDao.save(updated);
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

	@Override
	public List<StockQuantityConversion> getAllNonPostedStockQuantityConversions() {
		StockQuantityConversionSearchCriteria criteria = new StockQuantityConversionSearchCriteria();
		criteria.setPosted(false);
		return stockQuantityConversionDao.search(criteria);
	}

	@Override
	public List<StockQuantityConversion> search(StockQuantityConversionSearchCriteria criteria) {
		return stockQuantityConversionDao.search(criteria);
	}

}
