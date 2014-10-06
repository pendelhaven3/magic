package com.pj.magic.service;

import java.util.List;

import com.pj.magic.model.PricingScheme;
import com.pj.magic.model.Product;
import com.pj.magic.model.Supplier;

public interface ProductService {

	List<Product> getAllProducts();
	
	// TODO: Review references to this
	List<Product> getAllActiveProducts();
	
	List<Product> getAllActiveProducts(PricingScheme pricingScheme);
	
	Product findProductByCode(String code);
	
	Product findProductByCodeAndPricingScheme(String code, PricingScheme pricingScheme);
	
	Product getProduct(long id);

	Product getProduct(long id, PricingScheme pricingScheme);

	void save(Product product);
	
	void addProductSupplier(Product product, Supplier supplier);

	List<Supplier> getProductSuppliers(Product product);

	List<Supplier> getAvailableSuppliers(Product product);

	void deleteProductSupplier(Product product, Supplier supplier);
	
	void saveUnitCostsAndPrices(Product product, PricingScheme pricingScheme);

	List<Product> getAllActiveProductsBySupplier(Supplier supplier);
	
}
