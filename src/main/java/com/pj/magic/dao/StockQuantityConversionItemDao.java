package com.pj.magic.dao;

import java.util.List;

import com.pj.magic.model.StockQuantityConversion;
import com.pj.magic.model.StockQuantityConversionItem;

public interface StockQuantityConversionItemDao {

	void save(StockQuantityConversionItem item);
	
	List<StockQuantityConversionItem> findAllByStockQuantityConversion(StockQuantityConversion stockQuantityConversion);

	void delete(StockQuantityConversionItem item);

	void deleteAllByStockQuantityConversion(StockQuantityConversion stockQuantityConversion);
	
}
