package com.pj.magic.service;

import java.util.List;

import com.pj.magic.model.BadStockInventoryCheck;
import com.pj.magic.model.BadStockInventoryCheckItem;

public interface BadStockInventoryCheckService {

	List<BadStockInventoryCheck> getAllBadStockInventoryChecks();

	BadStockInventoryCheck getBadStockInventoryCheck(Long id);

	void save(BadStockInventoryCheck badStockInventoryCheck);

	void save(BadStockInventoryCheckItem item);

	void delete(BadStockInventoryCheckItem item);

	void post(BadStockInventoryCheck badStockInventoryCheck);

}
