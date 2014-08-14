package com.pj.magic.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pj.magic.dao.ProductDao;
import com.pj.magic.dao.ProductPriceDao;
import com.pj.magic.model.Product;
import com.pj.magic.service.ProductService;

@Service
public class ProductServiceImpl implements ProductService {

	@Autowired private ProductDao productDao;
	@Autowired private ProductPriceDao productPriceDao;
	
	@Override
	public List<Product> getAllProducts() {
		return productDao.getAll();
	}

	@Override
	public Product findProductByCode(String code) {
		return productDao.findByCode(code);
	}

	@Override
	public Product getProduct(long id) {
		return productDao.get(id);
	}

	@Transactional
	@Override
	public void save(Product product) {
		boolean inserting = (product.getId() == null);
		productDao.save(product);
		if (inserting) {
			productPriceDao.save(product);
		}
	}

	@Override
	public Product findFirstProductWithCodeLike(String code) {
		return productDao.findFirstWithCodeLike(code);
	}

	@Override
	public List<Product> getAllActiveProducts() {
		Product criteria = new Product();
		criteria.setActive(true);

		return productDao.search(criteria);
	}
	
}
