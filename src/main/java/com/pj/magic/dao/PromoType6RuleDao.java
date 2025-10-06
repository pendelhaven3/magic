package com.pj.magic.dao;

import com.pj.magic.model.Promo;
import com.pj.magic.model.PromoType6Rule;

public interface PromoType6RuleDao {

	void save(PromoType6Rule rule);

	PromoType6Rule findByPromo(Promo promo);

	void delete(PromoType6Rule rule);

}