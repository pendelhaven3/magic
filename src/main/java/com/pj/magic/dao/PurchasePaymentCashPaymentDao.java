package com.pj.magic.dao;

import java.util.List;

import com.pj.magic.model.PurchasePayment;
import com.pj.magic.model.PurchasePaymentCashPayment;

public interface PurchasePaymentCashPaymentDao {

	void save(PurchasePaymentCashPayment cashPayment);

	List<PurchasePaymentCashPayment> findAllByPurchasePayment(PurchasePayment purchasePayment);

	void deleteAllByPurchasePayment(PurchasePayment purchasePayment);

	void delete(PurchasePaymentCashPayment cashPayment);

}