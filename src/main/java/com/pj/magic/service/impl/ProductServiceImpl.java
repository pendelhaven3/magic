package com.pj.magic.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pj.magic.model.Product;
import com.pj.magic.model.SalesRequisition;
import com.pj.magic.service.PriceService;
import com.pj.magic.service.ProductService;

@Service
public class ProductServiceImpl implements ProductService {

	private static List<Product> products; // TODO: Temporary
	
	@Autowired private PriceService priceService;
	
	@Override
	public List<Product> getAllProducts() {
		if (products != null) {
			return products;
		}
		
		products = new ArrayList<>();
		
		Product product = new Product();
		product.setId(1);
		product.setCode("REJGRN010");
		product.setDescription("REJOICE GREEN 12mlx288");
		product.setUnits(Arrays.asList("CSE", "DOZ"));
		product.setUnitPrices(priceService.getUnitPrices(product));
		products.add(product);
		
		product = new Product();
		product.setId(2);
		product.setCode("ZONREG100");
		product.setDescription("ZONROX REGULAR 100mlx144");
		product.setUnits(Arrays.asList("CSE", "PCS"));
		product.setUnitPrices(priceService.getUnitPrices(product));
		products.add(product);
		
		product = new Product();
		product.setId(3);
		product.setCode("ZONREG250");
		product.setDescription("ZONROX REGULAR 250mlx72");
		product.setUnits(Arrays.asList("CSE", "PCS"));
		product.setUnitPrices(priceService.getUnitPrices(product));
		products.add(product);
		
		return products; 
	}

	@Override
	public Product findProductByCode(String code) {
		for (Product product : getAllProducts()) {
			if (product.getCode().equals(code)) {
				return product;
			}
		}
		return null;
	}
	
}
