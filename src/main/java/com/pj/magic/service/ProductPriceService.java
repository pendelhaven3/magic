package com.pj.magic.service;

import java.util.Date;
import java.util.List;

import com.pj.magic.model.ProductPriceHistory;

public interface ProductPriceService {

	List<ProductPriceHistory> getAllProductPriceHistoriesByDate(Date date);
	
}