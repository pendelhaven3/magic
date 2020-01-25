package com.pj.magic.dao;

import java.util.List;

import com.pj.magic.model.BadStockInventoryCheck;

public interface BadStockInventoryCheckDao {

	List<BadStockInventoryCheck> getAll();

	void save(BadStockInventoryCheck badStockInventoryCheck);

	BadStockInventoryCheck get(Long id);

}