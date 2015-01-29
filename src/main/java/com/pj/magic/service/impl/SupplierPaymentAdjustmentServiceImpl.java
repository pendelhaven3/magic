package com.pj.magic.service.impl;

import java.util.Date;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pj.magic.dao.SupplierPaymentAdjustmentDao;
import com.pj.magic.model.SupplierPaymentAdjustment;
import com.pj.magic.model.search.SupplierPaymentAdjustmentSearchCriteria;
import com.pj.magic.service.LoginService;
import com.pj.magic.service.SupplierPaymentAdjustmentService;

@Service
public class SupplierPaymentAdjustmentServiceImpl implements SupplierPaymentAdjustmentService {

	@Autowired private SupplierPaymentAdjustmentDao supplierPaymentAdjustmentDao;
	@Autowired private LoginService loginService;
	
	@Transactional
	@Override
	public void save(SupplierPaymentAdjustment paymentAdjustment) {
		supplierPaymentAdjustmentDao.save(paymentAdjustment);
	}

	@Override
	public SupplierPaymentAdjustment getSupplierPaymentAdjustment(long id) {
		return supplierPaymentAdjustmentDao.get(id);
	}

	@Transactional
	@Override
	public void post(SupplierPaymentAdjustment paymentAdjustment) {
		SupplierPaymentAdjustment updated = supplierPaymentAdjustmentDao.get(paymentAdjustment.getId());
		updated.setPosted(true);
		updated.setPostDate(new Date());
		updated.setPostedBy(loginService.getLoggedInUser());
		supplierPaymentAdjustmentDao.save(updated);
	}

	@Override
	public SupplierPaymentAdjustment findSupplierPaymentAdjustmentBySupplierPaymentAdjustmentNumber(
			long supplierPaymentAdjustmentNumber) {
		return supplierPaymentAdjustmentDao.findBySupplierPaymentAdjustmentNumber(supplierPaymentAdjustmentNumber);
	}

	@Override
	public List<SupplierPaymentAdjustment> search(SupplierPaymentAdjustmentSearchCriteria criteria) {
		return supplierPaymentAdjustmentDao.search(criteria);
	}

	@Override
	public List<SupplierPaymentAdjustment> getAllNewPaymentAdjustments() {
		SupplierPaymentAdjustmentSearchCriteria criteria = new SupplierPaymentAdjustmentSearchCriteria();
		criteria.setPosted(false);
		return search(criteria);
	}

}