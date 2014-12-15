package com.pj.magic.service;

import java.util.List;

import com.pj.magic.model.BadStockReturn;
import com.pj.magic.model.BadStockReturnItem;

public interface BadStockReturnService {

	void save(BadStockReturn badStockReturn);
	
	BadStockReturn getBadStockReturn(long id);

	List<BadStockReturn> getAllNewBadStockReturns();

	void save(BadStockReturnItem item);

	void delete(BadStockReturnItem item);

	void post(BadStockReturn badStockReturn);

	List<BadStockReturn> getAllBadStockReturns();
	
}