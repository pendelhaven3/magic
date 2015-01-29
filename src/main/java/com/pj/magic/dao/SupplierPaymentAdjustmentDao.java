package com.pj.magic.dao;

import java.util.List;

import com.pj.magic.model.SupplierPaymentAdjustment;
import com.pj.magic.model.search.SupplierPaymentAdjustmentSearchCriteria;

public interface SupplierPaymentAdjustmentDao {

	SupplierPaymentAdjustment get(long id);

	void save(SupplierPaymentAdjustment paymentAdjustment);

	SupplierPaymentAdjustment findBySupplierPaymentAdjustmentNumber(long supplierPaymentAdjustmentNumber);

	List<SupplierPaymentAdjustment> search(SupplierPaymentAdjustmentSearchCriteria criteria);
	
}