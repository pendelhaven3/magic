package com.pj.magic.service;

import java.util.List;

import com.pj.magic.model.InventoryCheck;

public interface InventoryCheckService {

	List<InventoryCheck> getAllInventoryChecks();

	void delete(InventoryCheck salesRequisition);

	void save(InventoryCheck inventoryCheck);
	
	InventoryCheck getNonPostedInventoryCheck();
	
}
