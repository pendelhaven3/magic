package com.pj.magic.service;

import java.util.List;

import com.pj.magic.model.InventoryCheck;

public interface InventoryCheckService {

	List<InventoryCheck> getAllInventoryCheck();

	void delete(InventoryCheck salesRequisition);
	
}
