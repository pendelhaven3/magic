package com.pj.magic.dao;

import java.util.List;

import com.pj.magic.model.SupplierPayment;
import com.pj.magic.model.SupplierPaymentCreditCardPayment;

public interface SupplierPaymentCreditCardPaymentDao {

	void save(SupplierPaymentCreditCardPayment creditCardPayment);

	List<SupplierPaymentCreditCardPayment> findAllBySupplierPayment(SupplierPayment supplierPayment);

	void deleteAllBySupplierPayment(SupplierPayment supplierPayment);

	void delete(SupplierPaymentCreditCardPayment creditCardPayment);

}