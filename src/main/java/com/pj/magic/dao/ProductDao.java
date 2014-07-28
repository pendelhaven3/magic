package com.pj.magic.dao;

import java.util.List;

import com.pj.magic.model.Product;

public interface ProductDao {

	List<Product> getAll();

	Product findProductByCode(String code);

	Product get(long id);
	
	void updateAvailableQuantities(Product product);
	
	void save(Product product);
}
