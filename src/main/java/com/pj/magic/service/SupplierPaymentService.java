package com.pj.magic.service;

import java.util.List;

import com.pj.magic.model.PaymentSalesInvoice;
import com.pj.magic.model.SupplierPayment;
import com.pj.magic.model.SupplierPaymentBankTransfer;
import com.pj.magic.model.SupplierPaymentCashPayment;
import com.pj.magic.model.SupplierPaymentCheckPayment;
import com.pj.magic.model.SupplierPaymentCreditCardPayment;
import com.pj.magic.model.SupplierPaymentPaymentAdjustment;
import com.pj.magic.model.SupplierPaymentReceivingReceipt;
import com.pj.magic.model.search.SupplierPaymentSearchCriteria;

public interface SupplierPaymentService {

	void save(SupplierPayment supplierPayment);

	SupplierPayment getSupplierPayment(Long id);

	void post(SupplierPayment supplierPayment);

	List<SupplierPayment> getAllNewSupplierPayments();

	void save(SupplierPaymentReceivingReceipt paymentReceivingReceipt);
	
	List<PaymentSalesInvoice> findAllPaymentReceivingReceiptsBySupplierPayment(SupplierPayment supplierPayment);

	void save(SupplierPaymentCheckPayment checkPayment);

	void delete(SupplierPaymentReceivingReceipt paymentReceivingReceipt);

	void save(SupplierPaymentCashPayment cashPayment);

	void delete(SupplierPaymentCheckPayment checkPayment);

	void delete(SupplierPaymentCashPayment cashPayment);
	
	void save(SupplierPaymentCreditCardPayment creditCardPayment);

	void delete(SupplierPaymentCreditCardPayment creditCardPayment);

	void delete(SupplierPaymentPaymentAdjustment adjustment);

	void save(SupplierPaymentPaymentAdjustment adjustment);

	List<SupplierPayment> searchSupplierPayments(SupplierPaymentSearchCriteria criteria);

	void save(SupplierPaymentBankTransfer bankTransfer);

	void delete(SupplierPaymentBankTransfer bankTransfer);
	
}