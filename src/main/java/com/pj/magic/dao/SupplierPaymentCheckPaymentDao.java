package com.pj.magic.dao;

import java.util.List;

import com.pj.magic.model.SupplierPayment;
import com.pj.magic.model.SupplierPaymentCheckPayment;

public interface SupplierPaymentCheckPaymentDao {

	void save(SupplierPaymentCheckPayment checkPayment);

	List<SupplierPaymentCheckPayment> findAllBySupplierPayment(SupplierPayment supplierPayment);

	void deleteAllBySupplierPayment(SupplierPayment supplierPayment);

	void delete(SupplierPaymentCheckPayment checkPayment);

}