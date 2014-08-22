package com.pj.magic.service;

import java.util.List;

import com.pj.magic.model.PricingScheme;
import com.pj.magic.model.Product;
import com.pj.magic.model.Supplier;

public interface ProductService {

	List<Product> getAllProducts();
	
	List<Product> getAllActiveProducts();
	
	Product findProductByCode(String code);
	
	Product getProduct(long id);

	void save(Product createProductFromRow);
	
	Product findFirstProductWithCodeLike(String code);

	void addProductSupplier(Product product, Supplier supplier);

	List<Supplier> getProductSuppliers(Product product);

	List<Supplier> getAvailableSuppliers(Product product);

	void deleteProductSupplier(Product product, Supplier supplier);
	
	void saveUnitPrices(Product product, PricingScheme pricingScheme);
	
}
