package com.pj.magic.dao;

import java.util.List;

import com.pj.magic.model.PurchasePayment;
import com.pj.magic.model.PurchasePaymentCheckPayment;

public interface PurchasePaymentCheckPaymentDao {

	void save(PurchasePaymentCheckPayment checkPayment);

	List<PurchasePaymentCheckPayment> findAllByPurchasePayment(PurchasePayment purchasePayment);

	void deleteAllByPurchasePayment(PurchasePayment purchasePayment);

	void delete(PurchasePaymentCheckPayment checkPayment);

}