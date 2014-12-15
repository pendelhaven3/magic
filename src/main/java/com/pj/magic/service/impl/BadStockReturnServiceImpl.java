package com.pj.magic.service.impl;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pj.magic.dao.BadStockReturnDao;
import com.pj.magic.dao.BadStockReturnItemDao;
import com.pj.magic.model.BadStockReturn;
import com.pj.magic.model.BadStockReturnItem;
import com.pj.magic.model.search.BadStockReturnSearchCriteria;
import com.pj.magic.service.BadStockReturnService;

@Service
public class BadStockReturnServiceImpl implements BadStockReturnService {

	@Autowired private BadStockReturnDao badStockReturnDao;
	@Autowired private BadStockReturnItemDao badStockReturnItemDao;
	
	@Transactional
	@Override
	public void save(BadStockReturn badStockReturn) {
		badStockReturnDao.save(badStockReturn);
	}

	@Override
	public BadStockReturn getBadStockReturn(long id) {
		BadStockReturn badStockReturn = badStockReturnDao.get(id);
		badStockReturn.setItems(badStockReturnItemDao.findAllByBadStockReturn(badStockReturn));
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

}