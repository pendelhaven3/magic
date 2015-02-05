package com.pj.magic.dao;

import java.util.List;

import com.pj.magic.model.PurchasePayment;
import com.pj.magic.model.PurchasePaymentCreditCardPayment;

public interface PurchasePaymentCreditCardPaymentDao {

	void save(PurchasePaymentCreditCardPayment creditCardPayment);

	List<PurchasePaymentCreditCardPayment> findAllByPurchasePayment(PurchasePayment purchasePayment);

	void deleteAllByPurchasePayment(PurchasePayment purchasePayment);

	void delete(PurchasePaymentCreditCardPayment creditCardPayment);

}