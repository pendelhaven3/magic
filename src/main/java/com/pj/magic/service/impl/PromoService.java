package com.pj.magic.service.impl;

import com.pj.magic.model.Promo;
import com.pj.magic.model.PromoType2Rule;

import java.util.List;

public interface PromoService {

	List<Promo> getAllPromos();

	void save(Promo promo);

	Promo getPromo(long id);

	void save(PromoType2Rule rule);

	void delete(PromoType2Rule rule);
	
}