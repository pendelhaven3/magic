package com.pj.magic.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pj.magic.dao.ProductDao;
import com.pj.magic.dao.ProductPriceDao;
import com.pj.magic.dao.SupplierDao;
import com.pj.magic.model.PricingScheme;
import com.pj.magic.model.Product;
import com.pj.magic.model.Supplier;
import com.pj.magic.service.ProductService;

@Service
public class ProductServiceImpl implements ProductService {

	@Autowired private ProductDao productDao;
	@Autowired private ProductPriceDao productPriceDao;
	@Autowired private SupplierDao supplierDao;
	
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

	@Transactional
	@Override
	public void addProductSupplier(Product product, Supplier supplier) {
		supplierDao.saveSupplierProduct(supplier, product);
	}
	
	@Override
	public List<Supplier> getProductSuppliers(Product product) {
		return supplierDao.findAllByProduct(product);
	}

	@Override
	public List<Supplier> getAvailableSuppliers(Product product) {
		return supplierDao.findAllNotHavingProduct(product);
	}

	@Transactional
	@Override
	public void deleteProductSupplier(Product product, Supplier supplier) {
		supplierDao.deleteSupplierProduct(supplier, product);
	}

	@Transactional
	@Override
	public void saveUnitPrices(Product product, PricingScheme pricingScheme) {
		productPriceDao.updateUnitPrices(product, pricingScheme);
	}

	@Override
	public List<Product> getAllActiveProductsBySupplier(Supplier supplier) {
		return productDao.findAllActiveBySupplier(supplier);
	}
	
}
