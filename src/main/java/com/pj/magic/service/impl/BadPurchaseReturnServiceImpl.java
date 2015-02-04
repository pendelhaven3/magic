package com.pj.magic.service.impl;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pj.magic.dao.BadPurchaseReturnDao;
import com.pj.magic.dao.BadPurchaseReturnItemDao;
import com.pj.magic.dao.ProductDao;
import com.pj.magic.model.BadPurchaseReturn;
import com.pj.magic.model.BadPurchaseReturnItem;
import com.pj.magic.model.search.BadPurchaseReturnSearchCriteria;
import com.pj.magic.service.BadPurchaseReturnService;
import com.pj.magic.service.LoginService;

@Service
public class BadPurchaseReturnServiceImpl implements BadPurchaseReturnService {

	@Autowired private BadPurchaseReturnDao badPurchaseReturnDao;
	@Autowired private BadPurchaseReturnItemDao badPurchaseReturnItemDao;
	@Autowired private LoginService loginService;
	@Autowired private ProductDao productDao;
	
	@Transactional
	@Override
	public void save(BadPurchaseReturn badPurchaseReturn) {
		badPurchaseReturnDao.save(badPurchaseReturn);
	}

	@Override
	public BadPurchaseReturn getBadPurchaseReturn(long id) {
		BadPurchaseReturn badPurchaseReturn = badPurchaseReturnDao.get(id);
		if (badPurchaseReturn != null) {
			badPurchaseReturn.setItems(badPurchaseReturnItemDao.findAllByBadPurchaseReturn(badPurchaseReturn));
		}
		return badPurchaseReturn;
	}

	@Override
	public List<BadPurchaseReturn> getAllNewBadPurchaseReturns() {
		BadPurchaseReturnSearchCriteria criteria = new BadPurchaseReturnSearchCriteria();
		criteria.setPosted(false);
		
		return search(criteria);
	}

	@Override
	public List<BadPurchaseReturn> search(BadPurchaseReturnSearchCriteria criteria) {
		List<BadPurchaseReturn> badPurchaseReturns = badPurchaseReturnDao.search(criteria);
		for (BadPurchaseReturn badPurchaseReturn : badPurchaseReturns) {
			badPurchaseReturn.setItems(badPurchaseReturnItemDao.findAllByBadPurchaseReturn(badPurchaseReturn));
		}
		return badPurchaseReturns;
	}

	@Transactional
	@Override
	public void save(BadPurchaseReturnItem item) {
		badPurchaseReturnItemDao.save(item);
	}

	@Transactional
	@Override
	public void delete(BadPurchaseReturnItem item) {
		badPurchaseReturnItemDao.delete(item);
	}

	@Transactional
	@Override
	public void post(BadPurchaseReturn badPurchaseReturn) {
//		BadPurchaseReturn updated = getBadPurchaseReturn(badPurchaseReturn.getId());
//		updated.setPosted(true);
//		updated.setPostDate(new Date());
//		updated.setPostedBy(loginService.getLoggedInUser());
//		badPurchaseReturnDao.save(updated);
//		
//		for (BadPurchaseReturnItem item : updated.getItems()) {
//			Product product = productDao.get(item.getProduct().getId());
//			item.setCost(product.getFinalCost(item.getUnit()));
//			badPurchaseReturnItemDao.save(item);
//		}
	}

	@Override
	public BadPurchaseReturn findBadPurchaseReturnByBadPurchaseReturnNumber(long badPurchaseReturnNumber) {
		BadPurchaseReturn badPurchaseReturn = badPurchaseReturnDao.findByBadPurchaseReturnNumber(badPurchaseReturnNumber);
		if (badPurchaseReturn != null) {
			badPurchaseReturn.setItems(badPurchaseReturnItemDao.findAllByBadPurchaseReturn(badPurchaseReturn));
		}
		return badPurchaseReturn;
	}

}