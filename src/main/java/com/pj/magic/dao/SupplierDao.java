package com.pj.magic.dao;

import java.util.List;

import com.pj.magic.model.Supplier;

public interface SupplierDao {

	void save(Supplier supplier);
	
	Supplier get(long id);
	
	List<Supplier> getAll();
	
}
