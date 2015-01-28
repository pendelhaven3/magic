package com.pj.magic.service.impl;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pj.magic.dao.ReceivingReceiptItemDao;
import com.pj.magic.dao.SupplierPaymentCashPaymentDao;
import com.pj.magic.dao.SupplierPaymentCheckPaymentDao;
import com.pj.magic.dao.SupplierPaymentCreditCardPaymentDao;
import com.pj.magic.dao.SupplierPaymentDao;
import com.pj.magic.dao.SupplierPaymentReceivingReceiptDao;
import com.pj.magic.model.PaymentSalesInvoice;
import com.pj.magic.model.SupplierPayment;
import com.pj.magic.model.SupplierPaymentCashPayment;
import com.pj.magic.model.SupplierPaymentCheckPayment;
import com.pj.magic.model.SupplierPaymentCreditCardPayment;
import com.pj.magic.model.SupplierPaymentReceivingReceipt;
import com.pj.magic.model.search.SupplierPaymentSearchCriteria;
import com.pj.magic.service.LoginService;
import com.pj.magic.service.SupplierPaymentService;

@Service
public class SupplierPaymentServiceImpl implements SupplierPaymentService {

	@Autowired private SupplierPaymentDao supplierPaymentDao;
	@Autowired private ReceivingReceiptItemDao receivingReceiptItemDao;
	@Autowired private LoginService loginService;
	@Autowired private SupplierPaymentReceivingReceiptDao supplierPaymentReceivingReceiptDao;
	@Autowired private SupplierPaymentCashPaymentDao supplierPaymentCashPaymentDao;
	@Autowired private SupplierPaymentCreditCardPaymentDao supplierPaymentCreditCardPaymentDao;
	@Autowired private SupplierPaymentCheckPaymentDao supplierPaymentCheckPaymentDao;
	
	@Transactional
	@Override
	public void save(SupplierPayment supplierPayment) {
		if (supplierPayment.getId() == null) {
			supplierPayment.setEncoder(loginService.getLoggedInUser());
		}
		supplierPaymentDao.save(supplierPayment);
	}

	@Override
	public SupplierPayment getSupplierPayment(Long id) {
		SupplierPayment supplierPayment = supplierPaymentDao.get(id);
		loadPaymentDetails(supplierPayment);
		return supplierPayment;
	}

	private void loadPaymentDetails(SupplierPayment supplierPayment) {
		supplierPayment.setReceivingReceipts(
				supplierPaymentReceivingReceiptDao.findAllBySupplierPayment(supplierPayment));
		for (SupplierPaymentReceivingReceipt receivingReceipt : supplierPayment.getReceivingReceipts()) {
			receivingReceipt.getReceivingReceipt().setItems(
					receivingReceiptItemDao.findAllByReceivingReceipt(receivingReceipt.getReceivingReceipt()));
		}
		supplierPayment.setCashPayments(supplierPaymentCashPaymentDao.findAllBySupplierPayment(supplierPayment));
		supplierPayment.setCreditCardPayments(
				supplierPaymentCreditCardPaymentDao.findAllBySupplierPayment(supplierPayment));
		supplierPayment.setCheckPayments(supplierPaymentCheckPaymentDao.findAllBySupplierPayment(supplierPayment));
//		supplierPayment.setAdjustments(paymentPaymentAdjustmentDao.findAllByPayment(supplierPayment));
	}
	
	@Override
	public void post(SupplierPayment supplierPayment) {
	}

	@Override
	public List<SupplierPayment> getAllNewSupplierPayments() {
		SupplierPaymentSearchCriteria criteria = new SupplierPaymentSearchCriteria();
		criteria.setPosted(false);
		return search(criteria);
	}

	private List<SupplierPayment> search(SupplierPaymentSearchCriteria criteria) {
		return supplierPaymentDao.search(criteria);
	}

	@Transactional
	@Override
	public void save(SupplierPaymentReceivingReceipt paymentReceivingReceipt) {
		supplierPaymentReceivingReceiptDao.insert(paymentReceivingReceipt);
	}

	@Override
	public List<PaymentSalesInvoice> findAllPaymentReceivingReceiptsBySupplierPayment(
			SupplierPayment supplierPayment) {
		return null;
	}

	@Transactional
	@Override
	public void save(SupplierPaymentCheckPayment checkPayment) {
		supplierPaymentCheckPaymentDao.save(checkPayment);
	}

	@Transactional
	@Override
	public void delete(SupplierPaymentReceivingReceipt paymentReceivingReceipt) {
		supplierPaymentReceivingReceiptDao.delete(paymentReceivingReceipt);
	}

	@Transactional
	@Override
	public void save(SupplierPaymentCashPayment cashPayment) {
		supplierPaymentCashPaymentDao.save(cashPayment);
	}

	@Transactional
	@Override
	public void delete(SupplierPaymentCheckPayment checkPayment) {
		supplierPaymentCheckPaymentDao.delete(checkPayment);
	}

	@Transactional
	@Override
	public void delete(SupplierPaymentCashPayment cashPayment) {
		supplierPaymentCashPaymentDao.delete(cashPayment);
	}

	@Transactional
	@Override
	public void save(SupplierPaymentCreditCardPayment creditCardPayment) {
		supplierPaymentCreditCardPaymentDao.save(creditCardPayment);
	}

	@Transactional
	@Override
	public void delete(SupplierPaymentCreditCardPayment creditCardPayment) {
		supplierPaymentCreditCardPaymentDao.delete(creditCardPayment);
	}
	
}