package com.pj.magic.service;

import java.util.List;

import com.pj.magic.model.Product;

public interface ProductService {

	List<Product> getAllProducts();
	
	Product findProductByCode(String code);
	
	Product getProduct(int id);
	
}
