package com.pj.magic.service.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.pj.magic.model.Product;
import com.pj.magic.model.UnitPrice;
import com.pj.magic.service.PriceService;

@Service
public class PriceServiceImpl implements PriceService {

	@Override
	public List<UnitPrice> getUnitPrices(Product product) {
		List<UnitPrice> unitPrices = new ArrayList<>();
		
		switch (product.getCode()) {
		case "REJGRN010":
			unitPrices.add(new UnitPrice("CSE", new BigDecimal("451.50").setScale(2, RoundingMode.HALF_UP)));
			unitPrices.add(new UnitPrice("DOZ", new BigDecimal("46.25").setScale(2, RoundingMode.HALF_UP)));
			break;
		case "ZONREG100":
			unitPrices.add(new UnitPrice("CSE", new BigDecimal("562.15").setScale(2, RoundingMode.HALF_UP)));
			unitPrices.add(new UnitPrice("PCS", new BigDecimal("37.60").setScale(2, RoundingMode.HALF_UP)));
			break;
		case "ZONREG250":
			unitPrices.add(new UnitPrice("CSE", new BigDecimal("450.50").setScale(2, RoundingMode.HALF_UP)));
			unitPrices.add(new UnitPrice("PCS", new BigDecimal("62.00").setScale(2, RoundingMode.HALF_UP)));
			break;
		}
		return unitPrices;
	}

}
