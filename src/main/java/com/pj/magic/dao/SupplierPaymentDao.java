package com.pj.magic.dao;

import java.util.List;

import com.pj.magic.model.SupplierPayment;
import com.pj.magic.model.search.SupplierPaymentSearchCriteria;

public interface SupplierPaymentDao {

	void save(SupplierPayment supplierPayment);
	
	SupplierPayment get(long id);
	
	List<SupplierPayment> search(SupplierPaymentSearchCriteria criteria);
	
}