package com.pj.magic.dao;

import java.util.List;

import com.pj.magic.model.BadStockInventoryCheck;
import com.pj.magic.model.BadStockInventoryCheckItem;

public interface BadStockInventoryCheckItemDao {

	void save(BadStockInventoryCheckItem item);
	
	List<BadStockInventoryCheckItem> findAllByBadStockInventoryCheck(BadStockInventoryCheck badStockInventoryCheck);

	void delete(BadStockInventoryCheckItem item);

}