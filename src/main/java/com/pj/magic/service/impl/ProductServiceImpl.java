package com.pj.magic.service.impl;

import java.util.HashSet;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pj.magic.dao.PricingSchemeDao;
import com.pj.magic.dao.ProductDao;
import com.pj.magic.dao.ProductPriceDao;
import com.pj.magic.dao.ReceivingReceiptDao;
import com.pj.magic.dao.SupplierDao;
import com.pj.magic.gui.tables.models.ProductCanvassItem;
import com.pj.magic.model.PricingScheme;
import com.pj.magic.model.Product;
import com.pj.magic.model.Supplier;
import com.pj.magic.model.util.ProductSearchCriteria;
import com.pj.magic.service.ProductService;

@Service
public class ProductServiceImpl implements ProductService {

	@Autowired private ProductDao productDao;
	@Autowired private ProductPriceDao productPriceDao;
	@Autowired private SupplierDao supplierDao;
	@Autowired private PricingSchemeDao pricingSchemeDao;
	@Autowired private ReceivingReceiptDao receivingReceiptDao;
	
	@Override
	public List<Product> getAllProducts() {
		return productDao.getAll();
	}

	@Override
	public Product findProductByCode(String code) {
		return productDao.findByCode(code);
	}

	@Override
	public Product findProductByCodeAndPricingScheme(String code, PricingScheme pricingScheme) {
		return productDao.findByCodeAndPricingScheme(code, pricingScheme);
	}

	@Override
	public Product getProduct(long id) {
		return productDao.get(id);
	}

	@Transactional
	@Override
	public void save(Product product) {
		boolean inserting = (product.getId() == null);
		if (inserting) {
			productDao.save(product);
			productPriceDao.createUnitPrices(product);
		} else {
			boolean updateCostAndPrice = hasUnitConversionChange(product);
			productDao.save(product);
			if (updateCostAndPrice) {
				product.autoCalculateCostsOfSmallerUnits();
				productDao.updateCosts(product);
			}
			if (updateCostAndPrice) {
				for (PricingScheme pricingScheme : pricingSchemeDao.getAll()) {
					Product p = productDao.findByIdAndPricingScheme(product.getId(), pricingScheme);
					p.autoCalculatePricesOfSmallerUnits();
					productPriceDao.updateUnitPrices(p, pricingScheme);
				}
			}
		}
	}

	private boolean hasUnitConversionChange(Product product) {
		Product fromDb = productDao.get(product.getId());
		return !new HashSet<>(product.getUnitConversions()).equals(new HashSet<>(fromDb.getUnitConversions()));
	}

	@Override
	public List<Product> getAllActiveProducts() {
		ProductSearchCriteria criteria = new ProductSearchCriteria();
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
	public void saveUnitCostsAndPrices(Product product, PricingScheme pricingScheme) {
		productPriceDao.updateUnitPrices(product, pricingScheme);
		productDao.updateCosts(product);
	}

	@Override
	public List<Product> getAllActiveProductsBySupplier(Supplier supplier) {
		return productDao.findAllActiveBySupplier(supplier);
	}

	@Override
	public Product getProduct(long id, PricingScheme pricingScheme) {
		return productDao.findByIdAndPricingScheme(id, pricingScheme);
	}

	@Override
	public List<Product> getAllActiveProducts(PricingScheme pricingScheme) {
		ProductSearchCriteria criteria = new ProductSearchCriteria();
		criteria.setActive(true);
		criteria.setPricingScheme(pricingScheme);
		
		return productDao.search(criteria);
	}

	@Override
	public List<Product> searchProducts(ProductSearchCriteria criteria) {
		return productDao.search(criteria);
	}

	@Override
	public List<ProductCanvassItem> getProductCanvass(Product product) {
		return receivingReceiptDao.getProductCanvassItems(product);
	}

}
