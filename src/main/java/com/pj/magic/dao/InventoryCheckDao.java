package com.pj.magic.dao;

import java.util.List;

import com.pj.magic.model.InventoryCheck;
import com.pj.magic.model.InventoryCheckSummaryItem;

public interface InventoryCheckDao {

	void save(InventoryCheck inventoryCheck);
	
	InventoryCheck get(long id);
	
	List<InventoryCheck> getAll();
	
	List<InventoryCheck> search(InventoryCheck criteria);
	
	List<InventoryCheckSummaryItem> getSummaryItems(InventoryCheck inventoryCheck);
	
}
