package com.pj.magic.service;

import java.util.List;

import com.pj.magic.model.Product;
import com.pj.magic.model.UnitPrice;


public interface PriceService {

	List<UnitPrice> getUnitPrices(Product product);
	
}
