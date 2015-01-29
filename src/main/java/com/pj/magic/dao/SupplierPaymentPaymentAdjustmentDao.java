package com.pj.magic.dao;

import java.util.List;

import com.pj.magic.model.SupplierPayment;
import com.pj.magic.model.SupplierPaymentPaymentAdjustment;

public interface SupplierPaymentPaymentAdjustmentDao {

	void save(SupplierPaymentPaymentAdjustment paymentAdjustment);

	List<SupplierPaymentPaymentAdjustment> findAllBySupplierPayment(SupplierPayment supplierPayment);

	void deleteAllBySupplierPayment(SupplierPayment supplierPayment);

	void delete(SupplierPaymentPaymentAdjustment paymentAdjustment);
	
}
