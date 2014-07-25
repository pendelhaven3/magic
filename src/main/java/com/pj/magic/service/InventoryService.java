package com.pj.magic.service;

import java.util.List;

import com.pj.magic.model.Product;
import com.pj.magic.model.UnitQuantity;

public interface InventoryService {

	List<UnitQuantity> getUnitQuantities(Product product);
	
}
