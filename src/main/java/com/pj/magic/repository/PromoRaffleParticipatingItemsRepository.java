package com.pj.magic.repository;

import java.util.List;

import com.pj.magic.model.Product;
import com.pj.magic.model.Promo;

public interface PromoRaffleParticipatingItemsRepository {
	
	boolean doesRepositoryExists();

	void createRepository();

	List<Product> findAllByPromo(Promo promo);

	Product findByPromoAndProduct(Promo promo, Product product);

	void add(Promo promo, Product product);
	
	void delete(Promo promo, Product product);

}
