package com.pj.magic.dao;

import java.util.List;

import com.pj.magic.model.Product;

public interface ProductDao {

	List<Product> getAllProducts();

	Product findProductByCode(String code);

	Product getProduct(long id);
	
}
