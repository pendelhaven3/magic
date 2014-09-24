package com.pj.magic.dao;

import com.pj.magic.model.PricingScheme;
import com.pj.magic.model.Product;

public interface ProductPriceDao {

	void save(Product product);

	void updateUnitPrices(Product product, PricingScheme pricingScheme);

}
