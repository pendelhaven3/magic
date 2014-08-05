package com.pj.magic.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pj.magic.dao.CustomerDao;
import com.pj.magic.model.Customer;
import com.pj.magic.service.CustomerService;

@Service
public class CustomerServiceImpl implements CustomerService {

	@Autowired private CustomerDao customerDao;
	
	@Override
	public List<Customer> getAllCustomers() {
		return customerDao.getAll();
	}

	@Override
	public Customer findFirstCustomerWithCodeLike(String code) {
		return customerDao.findFirstWithCodeLike(code);
	}

}
