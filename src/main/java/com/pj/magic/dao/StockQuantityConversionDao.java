package com.pj.magic.dao;

import java.util.List;

import com.pj.magic.model.StockQuantityConversion;

public interface StockQuantityConversionDao {

	void save(StockQuantityConversion stockQuantityConversion);
	
	StockQuantityConversion get(long id);
	
	List<StockQuantityConversion> getAll();
	
}
