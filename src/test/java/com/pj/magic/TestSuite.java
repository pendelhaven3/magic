package com.pj.magic;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import com.pj.magic.dao.CustomerDaoTest;
import com.pj.magic.dao.PurchasePaymentAdjustmentDaoTest;
import com.pj.magic.dao.PurchasePaymentBankTransferDaoTest;
import com.pj.magic.dao.PurchasePaymentCashPaymentDaoTest;
import com.pj.magic.dao.PurchasePaymentCheckPaymentDaoTest;
import com.pj.magic.dao.PurchasePaymentCreditCardPaymentDaoTest;
import com.pj.magic.dao.PurchasePaymentDaoTest;
import com.pj.magic.dao.PurchasePaymentPaymentAdjustmentDaoTest;
import com.pj.magic.dao.PurchasePaymentReceivingReceiptDaoTest;

@RunWith(Suite.class)
@SuiteClasses({
	CustomerDaoTest.class,
	PurchasePaymentDaoTest.class,
	PurchasePaymentBankTransferDaoTest.class,
	PurchasePaymentCashPaymentDaoTest.class,
	PurchasePaymentCreditCardPaymentDaoTest.class,
	PurchasePaymentCheckPaymentDaoTest.class,
	PurchasePaymentAdjustmentDaoTest.class,
	PurchasePaymentPaymentAdjustmentDaoTest.class,
	PurchasePaymentReceivingReceiptDaoTest.class
})
public class TestSuite {

}