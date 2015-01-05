package com.pj.magic.dao;

import java.util.List;

import com.pj.magic.model.PricingScheme;
import com.pj.magic.model.Product;
import com.pj.magic.model.ProductPriceHistory;

public interface ProductPriceHistoryDao {

	void save(ProductPriceHistory history);
	
	List<ProductPriceHistory> getAll(Product product, PricingScheme pricingScheme);
	
}