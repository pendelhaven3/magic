package com.pj.magic.service;

import java.util.List;

import com.pj.magic.model.Product;

public interface ProductService {

	List<Product> getAllProducts();
	
	List<Product> getAllActiveProducts();
	
	Product findProductByCode(String code);
	
	Product getProduct(long id);

	void save(Product createProductFromRow);
	
	Product findFirstProductWithCodeLike(String code);
	
}
