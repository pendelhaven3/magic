package com.pj.magic.service.impl;

import com.pj.magic.model.Promo;

import java.util.List;

public interface PromoService {

	List<Promo> getAllPromos();

	void save(Promo promo);

	Promo getPromo(long id);
	
}