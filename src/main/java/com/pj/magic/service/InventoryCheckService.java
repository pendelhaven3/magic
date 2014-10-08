package com.pj.magic.service;

import java.util.List;

import com.pj.magic.model.InventoryCheck;

public interface InventoryCheckService {

	List<InventoryCheck> getAllInventoryChecks();

	void delete(InventoryCheck inventoryCheck);

	void save(InventoryCheck inventoryCheck);
	
	InventoryCheck getNonPostedInventoryCheck();
	
	InventoryCheck getInventoryCheck(long id);
	
}
