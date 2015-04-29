package com.pj.magic.service.impl;

import com.pj.magic.model.Promo;
import com.pj.magic.model.PromoType2Rule;
import com.pj.magic.model.PromoType3Rule;
import com.pj.magic.model.PromoType3RulePromoProduct;

import java.util.List;

public interface PromoService {

	List<Promo> getAllPromos();

	void save(Promo promo);

	Promo getPromo(long id);

	void save(PromoType2Rule rule);

	void delete(PromoType2Rule rule);

	List<Promo> getAllActivePromos();

	void save(PromoType3RulePromoProduct promoProduct);

	void delete(PromoType3RulePromoProduct promoProduct);

	void addAllPromoProducts(PromoType3Rule rule);

	void removeAllPromoProducts(PromoType3Rule rule);
	
}