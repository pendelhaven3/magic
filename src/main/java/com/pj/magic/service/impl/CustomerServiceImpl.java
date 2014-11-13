package com.pj.magic.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

	@Override
	public Customer findCustomerByCode(String code) {
		return customerDao.findByCode(code);
	}

	@Transactional
	@Override
	public void save(Customer customer) {
		customerDao.save(customer);
	}

	@Override
	public List<Customer> findAllWithNameLike(String name) {
		return customerDao.findAllWithNameLike(name);
	}

}
