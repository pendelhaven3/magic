package com.pj.magic.service;

import java.util.List;

import com.pj.magic.model.StockQuantityConversion;
import com.pj.magic.model.StockQuantityConversionItem;

public interface StockQuantityConversionService {

	void save(StockQuantityConversion stockQuantityConversion);
	
	StockQuantityConversion getStockQuantityConversion(long id);
	
	List<StockQuantityConversion> getAllStockQuantityConversions();

	void delete(StockQuantityConversion stockQuantityConversion);

	void post(StockQuantityConversion stockQuantityConversion);

	void save(StockQuantityConversionItem item);

	void delete(StockQuantityConversionItem item);
	
}
