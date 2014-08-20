package com.pj.magic.dao;

import java.util.List;

import com.pj.magic.model.Product;
import com.pj.magic.model.Supplier;

public interface SupplierDao {

	void save(Supplier supplier);
	
	Supplier get(long id);
	
	List<Supplier> getAll();

	List<Supplier> findAllByProduct(Product product);

	void saveSupplierProduct(Supplier supplier, Product product);
	
	List<Supplier> findAllNotHavingProduct(Product product);
	
}
