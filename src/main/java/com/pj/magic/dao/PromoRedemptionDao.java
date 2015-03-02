package com.pj.magic.dao;

import java.util.List;

import com.pj.magic.model.Promo;
import com.pj.magic.model.PromoRedemption;

public interface PromoRedemptionDao {

	void save(PromoRedemption promoRedemption);

	PromoRedemption get(long id);

	List<PromoRedemption> findAllByPromo(Promo promo);

	void insertNewPromoRedemptionSequence(Promo promo);
	
}