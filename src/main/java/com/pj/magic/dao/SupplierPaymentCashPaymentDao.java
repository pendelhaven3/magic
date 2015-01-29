package com.pj.magic.dao;

import java.util.List;

import com.pj.magic.model.SupplierPayment;
import com.pj.magic.model.SupplierPaymentCashPayment;

public interface SupplierPaymentCashPaymentDao {

	void save(SupplierPaymentCashPayment cashPayment);

	List<SupplierPaymentCashPayment> findAllBySupplierPayment(SupplierPayment supplierPayment);

	void deleteAllBySupplierPayment(SupplierPayment supplierPayment);

	void delete(SupplierPaymentCashPayment cashPayment);

}