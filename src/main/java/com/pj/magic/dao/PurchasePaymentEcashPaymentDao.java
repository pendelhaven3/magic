package com.pj.magic.dao;

import java.util.List;

import com.pj.magic.model.PurchasePayment;
import com.pj.magic.model.PurchasePaymentEcashPayment;
import com.pj.magic.model.search.PurchasePaymentEcashPaymentSearchCriteria;

public interface PurchasePaymentEcashPaymentDao {

	void save(PurchasePaymentEcashPayment ecashPayment);
	
	List<PurchasePaymentEcashPayment> findAllByPurchasePayment(PurchasePayment purchasePayment);

	void delete(PurchasePaymentEcashPayment ecashPayment);

	List<PurchasePaymentEcashPayment> search(PurchasePaymentEcashPaymentSearchCriteria criteria);

}