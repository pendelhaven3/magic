package com.pj.magic.service;

import java.util.List;

import com.pj.magic.model.ProductCategory;

public interface ProductCategoryService {

	void save(ProductCategory category);
	
	ProductCategory getProductCategory(long id);
	
	List<ProductCategory> getAllProductCategories();
	
}
