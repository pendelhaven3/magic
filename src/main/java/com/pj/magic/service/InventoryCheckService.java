package com.pj.magic.service;

import java.util.List;

import com.pj.magic.model.InventoryCheck;

public interface InventoryCheckService {

	List<InventoryCheck> getAllInventoryChecks();

	void save(InventoryCheck inventoryCheck);
	
	InventoryCheck getNonPostedInventoryCheck();
	
	InventoryCheck getInventoryCheck(long id);

	void post(InventoryCheck inventoryCheck);

	void delete(InventoryCheck inventoryCheck);
	
}
