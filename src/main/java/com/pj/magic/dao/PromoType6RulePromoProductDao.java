package com.pj.magic.dao;

import java.util.List;

import com.pj.magic.model.PromoType6Rule;
import com.pj.magic.model.PromoType6RulePromoProduct;

public interface PromoType6RulePromoProductDao {

	void save(PromoType6RulePromoProduct promoProduct);
	
	List<PromoType6RulePromoProduct> findAllByRule(PromoType6Rule rule);

	void delete(PromoType6RulePromoProduct promoProduct);

	void deleteAllByRule(PromoType6Rule rule);

}