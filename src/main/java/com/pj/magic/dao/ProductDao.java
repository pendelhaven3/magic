package com.pj.magic.dao;

import java.util.List;

import com.pj.magic.model.PricingScheme;
import com.pj.magic.model.Product;

public interface ProductDao {

	List<Product> getAll();

	Product findByCode(String code);

	Product get(long id);
	
	void updateAvailableQuantities(Product product);
	
	void save(Product product);
	
	Product findFirstWithCodeLike(String code);

	List<Product> search(Product criteria);
	
	List<Product> findAllWithPricingScheme(PricingScheme pricingScheme);
	
}
