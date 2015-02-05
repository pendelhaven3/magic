package com.pj.magic.dao;

import java.util.List;

import com.pj.magic.model.PurchasePayment;
import com.pj.magic.model.PurchasePaymentBankTransfer;

public interface PurchasePaymentBankTransferDao {

	void save(PurchasePaymentBankTransfer bankTransfer);

	List<PurchasePaymentBankTransfer> findAllByPurchasePayment(PurchasePayment purchasePayment);

	void deleteAllByPurchasePayment(PurchasePayment purchasePayment);

	void delete(PurchasePaymentBankTransfer bankTransfer);

}