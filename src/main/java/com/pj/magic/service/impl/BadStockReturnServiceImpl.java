package com.pj.magic.service.impl;

import java.util.Date;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pj.magic.dao.BadStockReturnDao;
import com.pj.magic.dao.BadStockReturnItemDao;
import com.pj.magic.dao.PaymentTerminalAssignmentDao;
import com.pj.magic.model.BadStockReturn;
import com.pj.magic.model.BadStockReturnItem;
import com.pj.magic.model.PaymentTerminalAssignment;
import com.pj.magic.model.User;
import com.pj.magic.model.search.BadStockReturnSearchCriteria;
import com.pj.magic.service.BadStockReturnService;
import com.pj.magic.service.LoginService;

@Service
public class BadStockReturnServiceImpl implements BadStockReturnService {

	@Autowired private BadStockReturnDao badStockReturnDao;
	@Autowired private BadStockReturnItemDao badStockReturnItemDao;
	@Autowired private LoginService loginService;
	@Autowired private PaymentTerminalAssignmentDao paymentTerminalAssignmentDao;
	
	@Transactional
	@Override
	public void save(BadStockReturn badStockReturn) {
		badStockReturnDao.save(badStockReturn);
	}

	@Override
	public BadStockReturn getBadStockReturn(long id) {
		BadStockReturn badStockReturn = badStockReturnDao.get(id);
		if (badStockReturn != null) {
			badStockReturn.setItems(badStockReturnItemDao.findAllByBadStockReturn(badStockReturn));
		}
		return badStockReturn;
	}

	@Override
	public List<BadStockReturn> getAllNewBadStockReturns() {
		BadStockReturnSearchCriteria criteria = new BadStockReturnSearchCriteria();
		criteria.setPosted(false);
		
		return search(criteria);
	}

	private List<BadStockReturn> search(BadStockReturnSearchCriteria criteria) {
		List<BadStockReturn> badStockReturns = badStockReturnDao.search(criteria);
		for (BadStockReturn badStockReturn : badStockReturns) {
			badStockReturn.setItems(badStockReturnItemDao.findAllByBadStockReturn(badStockReturn));
		}
		return badStockReturns;
	}

	@Transactional
	@Override
	public void save(BadStockReturnItem item) {
		badStockReturnItemDao.save(item);
	}

	@Transactional
	@Override
	public void delete(BadStockReturnItem item) {
		badStockReturnItemDao.delete(item);
	}

	@Transactional
	@Override
	public void post(BadStockReturn badStockReturn) {
		BadStockReturn updated = getBadStockReturn(badStockReturn.getId());
		updated.setPosted(true);
		updated.setPostDate(new Date());
		updated.setPostedBy(loginService.getLoggedInUser());
		badStockReturnDao.save(updated);
	}

	@Override
	public List<BadStockReturn> getAllBadStockReturns() {
		return search(new BadStockReturnSearchCriteria());
	}

	@Transactional
	@Override
	public void markAsPaid(BadStockReturn badStockReturn) {
		User user = loginService.getLoggedInUser();
		PaymentTerminalAssignment paymentTerminalAssignment = paymentTerminalAssignmentDao.findByUser(user);
		if (paymentTerminalAssignment == null) {
			throw new RuntimeException("User " + user.getUsername() + " is not assigned to payment terminal");
		}
		
		BadStockReturn updated = badStockReturnDao.get(badStockReturn.getId());
		updated.setPaid(true);
		updated.setPaidDate(new Date());
		updated.setPaidBy(loginService.getLoggedInUser());
		updated.setPaymentTerminal(paymentTerminalAssignment.getPaymentTerminal());
		badStockReturnDao.save(updated);
	}

	@Override
	public BadStockReturn findBadStockReturnByBadStockReturnNumber(long badStockReturnNumber) {
		BadStockReturn badStockReturn = badStockReturnDao.findByBadStockReturnNumber(badStockReturnNumber);
		if (badStockReturn != null) {
			badStockReturn.setItems(badStockReturnItemDao.findAllByBadStockReturn(badStockReturn));
		}
		return badStockReturn;
	}

	@Override
	public List<BadStockReturn> getUnpaidBadStockReturns() {
		BadStockReturnSearchCriteria criteria = new BadStockReturnSearchCriteria();
		criteria.setPaid(false);
		return search(criteria);
	}

}