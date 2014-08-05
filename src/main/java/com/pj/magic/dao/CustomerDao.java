package com.pj.magic.dao;

import java.util.List;

import com.pj.magic.model.Customer;

public interface CustomerDao {

	void save(Customer customer);
	
	List<Customer> getAll();
	
	Customer get(long id);
	
	Customer findFirstWithCodeLike(String code);
	
	Customer findByCode(String code);
	
}
