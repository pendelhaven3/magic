package com.pj.magic.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pj.magic.dao.SupplierDao;
import com.pj.magic.model.Supplier;
import com.pj.magic.service.SupplierService;

@Service
public class SupplierServiceImpl implements SupplierService {

	@Autowired private SupplierDao supplierDao;
	
	@Transactional
	@Override
	public void save(Supplier supplier) {
		supplierDao.save(supplier);
	}

	@Override
	public Supplier getSupplier(long id) {
		return supplierDao.get(id);
	}

	@Override
	public List<Supplier> getAllSuppliers() {
		return supplierDao.getAll();
	}

}
