package com.pj.magic.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pj.magic.dao.ProductDao;
import com.pj.magic.model.Product;
import com.pj.magic.service.InventoryService;
import com.pj.magic.service.PriceService;
import com.pj.magic.service.ProductService;

@Service
public class ProductServiceImpl implements ProductService {

	@Autowired private PriceService priceService;
	@Autowired private InventoryService inventoryService;
	@Autowired private ProductDao productDao;
	
	@Override
	public List<Product> getAllProducts() {
		return productDao.getAllProducts();
	}

	@Override
	public Product findProductByCode(String code) {
		return productDao.findProductByCode(code);
	}

	@Override
	public Product getProduct(long id) {
		return productDao.getProduct(id);
	}
	
}
