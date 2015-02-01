package com.pj.magic.dao;

import java.util.List;

import com.pj.magic.model.SupplierPayment;
import com.pj.magic.model.SupplierPaymentBankTransfer;

public interface SupplierPaymentBankTransferDao {

	void save(SupplierPaymentBankTransfer bankTransfer);

	List<SupplierPaymentBankTransfer> findAllBySupplierPayment(SupplierPayment supplierPayment);

	void deleteAllBySupplierPayment(SupplierPayment supplierPayment);

	void delete(SupplierPaymentBankTransfer bankTransfer);

}