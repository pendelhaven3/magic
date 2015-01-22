package com.pj.magic.service.impl;

import java.util.Date;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pj.magic.dao.PaymentAdjustmentDao;
import com.pj.magic.dao.PaymentTerminalAssignmentDao;
import com.pj.magic.model.PaymentAdjustment;
import com.pj.magic.model.PaymentTerminalAssignment;
import com.pj.magic.model.User;
import com.pj.magic.service.LoginService;
import com.pj.magic.service.PaymentAdjustmentService;

@Service
public class PaymentAdjustmentServiceImpl implements PaymentAdjustmentService {

	@Autowired private PaymentAdjustmentDao paymentAdjustmentDao;
	@Autowired private LoginService loginService;
	@Autowired private PaymentTerminalAssignmentDao paymentTerminalAssignmentDao;
	
	@Transactional
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

	@Transactional
	@Override
	public void post(PaymentAdjustment paymentAdjustment) {
		PaymentAdjustment updated = paymentAdjustmentDao.get(paymentAdjustment.getId());
		updated.setPosted(true);
		updated.setPostDate(new Date());
		updated.setPostedBy(loginService.getLoggedInUser());
		paymentAdjustmentDao.save(updated);
	}

	@Transactional
	@Override
	public void markAsPaid(PaymentAdjustment paymentAdjustment) {
		User user = loginService.getLoggedInUser();
		PaymentTerminalAssignment paymentTerminalAssignment = paymentTerminalAssignmentDao.findByUser(user);
		if (paymentTerminalAssignment == null) {
			throw new RuntimeException("User " + user.getUsername() + " is not assigned to payment terminal");
		}
		
		PaymentAdjustment updated = paymentAdjustmentDao.get(paymentAdjustment.getId());
		updated.setPaid(true);
		updated.setPaidDate(new Date());
		updated.setPaidBy(loginService.getLoggedInUser());
		updated.setPaymentTerminal(paymentTerminalAssignment.getPaymentTerminal());
		paymentAdjustmentDao.save(updated);
	}

}