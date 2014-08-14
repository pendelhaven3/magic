package com.pj.magic.dao.impl;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.stereotype.Repository;

import com.pj.magic.dao.ProductCategoryDao;
import com.pj.magic.model.ProductCategory;

@Repository
public class ProductCategoryDaoImpl implements ProductCategoryDao {

	@PersistenceContext
	private EntityManager entityManager;
	
	@Override
	public void save(ProductCategory category) {
		entityManager.persist(category);
	}

	@Override
	public ProductCategory get(long id) {
		return entityManager.find(ProductCategory.class, id);
	}

	@Override
	public List<ProductCategory> getAll() {
        return entityManager.createQuery("SELECT p FROM ProductCategory p", ProductCategory.class).getResultList();
	}

}
