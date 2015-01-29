package com.pj.magic.service;

import java.util.List;

import com.pj.magic.model.SupplierPaymentAdjustment;
import com.pj.magic.model.search.SupplierPaymentAdjustmentSearchCriteria;

public interface SupplierPaymentAdjustmentService {

	void save(SupplierPaymentAdjustment paymentAdjustment);
	
	SupplierPaymentAdjustment getSupplierPaymentAdjustment(long id);
	
	void post(SupplierPaymentAdjustment paymentAdjustment);

	SupplierPaymentAdjustment findSupplierPaymentAdjustmentBySupplierPaymentAdjustmentNumber(
			long supplierPaymentAdjustmentNumber);

	List<SupplierPaymentAdjustment> search(SupplierPaymentAdjustmentSearchCriteria criteria);

	List<SupplierPaymentAdjustment> getAllNewPaymentAdjustments();
	
}