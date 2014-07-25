package com.pj.magic.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.pj.magic.model.Product;
import com.pj.magic.model.UnitQuantity;
import com.pj.magic.service.InventoryService;

@Service
public class InventoryServiceImpl implements InventoryService {

	@Override
	public List<UnitQuantity> getUnitQuantities(Product product) {
		List<UnitQuantity> unitQuantities = new ArrayList<>();
		
		switch (product.getCode()) {
		case "REJGRN010":
			unitQuantities.add(new UnitQuantity("CSE", 5));
			unitQuantities.add(new UnitQuantity("DOZ", 10));
			break;
		case "ZONREG100":
			unitQuantities.add(new UnitQuantity("CSE", 5));
			unitQuantities.add(new UnitQuantity("PCS", 10));
			break;
		case "ZONREG250":
			unitQuantities.add(new UnitQuantity("CSE", 5));
			unitQuantities.add(new UnitQuantity("PCS", 10));
			break;
		}
		
		return unitQuantities;
	}

	@Override
	public int getQuantity(Product product, String unit) {
		for (UnitQuantity unitQuantity : getUnitQuantities(product)) {
			if (unitQuantity.getUnit().equals(unit)) {
				return unitQuantity.getQuantity();
			}
		}
		return 0;
	}

}
