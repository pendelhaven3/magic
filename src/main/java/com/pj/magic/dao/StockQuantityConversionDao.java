package com.pj.magic.dao;

import java.util.List;

import com.pj.magic.model.StockQuantityConversion;
import com.pj.magic.model.search.StockQuantityConversionSearchCriteria;

public interface StockQuantityConversionDao {

	void save(StockQuantityConversion stockQuantityConversion);
	
	StockQuantityConversion get(long id);
	
	List<StockQuantityConversion> getAll();

	void delete(StockQuantityConversion stockQuantityConversion);

	List<StockQuantityConversion> search(StockQuantityConversionSearchCriteria criteria);
	
}
