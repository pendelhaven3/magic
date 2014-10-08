package com.pj.magic.dao;

import java.util.List;

import com.pj.magic.model.InventoryCheck;

public interface InventoryCheckDao {

	void save(InventoryCheck inventoryCheck);
	
	InventoryCheck get(long id);
	
	List<InventoryCheck> getAll();
	
}
