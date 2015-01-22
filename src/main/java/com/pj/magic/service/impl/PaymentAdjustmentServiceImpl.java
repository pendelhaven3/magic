package com.pj.magic.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pj.magic.dao.PaymentAdjustmentDao;
import com.pj.magic.model.PaymentAdjustment;
import com.pj.magic.service.PaymentAdjustmentService;

@Service
public class PaymentAdjustmentServiceImpl implements PaymentAdjustmentService {

	@Autowired private PaymentAdjustmentDao paymentAdjustmentDao;
	
	@Override
	public void save(PaymentAdjustment paymentAdjustment) {
		paymentAdjustmentDao.save(paymentAdjustment);
	}

	@Override
	public PaymentAdjustment getPaymentAdjustment(long id) {
		return paymentAdjustmentDao.get(id);
	}

	@Override
	public List<PaymentAdjustment> getAllPaymentAdjustments() {
		return paymentAdjustmentDao.getAll();
	}

}