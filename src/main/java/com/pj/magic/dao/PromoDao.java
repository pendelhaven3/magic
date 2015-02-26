package com.pj.magic.dao;

import com.pj.magic.model.Promo;

import java.util.List;

public interface PromoDao {

	List<Promo> getAll();

	Promo get(long id);

	void save(Promo promo);
	
}