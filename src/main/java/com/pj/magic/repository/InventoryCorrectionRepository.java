package com.pj.magic.repository;

import java.util.List;

import com.pj.magic.model.InventoryCorrection;
import com.pj.magic.model.Product;

public interface InventoryCorrectionRepository {

	List<InventoryCorrection> getAll();
	
	InventoryCorrection get(long id);
	
	void save(InventoryCorrection inventoryCorrection);

	InventoryCorrection findMostRecentByProduct(Product product);
	
}
