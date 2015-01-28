package com.pj.magic.dao;

import java.util.List;

import com.pj.magic.model.SupplierPayment;
import com.pj.magic.model.SupplierPaymentReceivingReceipt;

public interface SupplierPaymentReceivingReceiptDao {

	void insert(SupplierPaymentReceivingReceipt supplierPaymentReceivingReceipt);

	List<SupplierPaymentReceivingReceipt> findAllBySupplierPayment(SupplierPayment supplierPayment);

	void deleteAllBySupplierPayment(SupplierPayment supplierPayment);

	void delete(SupplierPaymentReceivingReceipt supplierPaymentReceivingReceipt);

}