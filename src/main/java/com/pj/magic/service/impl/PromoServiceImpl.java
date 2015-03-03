package com.pj.magic.service.impl;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pj.magic.dao.ProductDao;
import com.pj.magic.dao.PromoDao;
import com.pj.magic.dao.PromoRedemptionDao;
import com.pj.magic.dao.PromoType2RuleDao;
import com.pj.magic.model.Promo;
import com.pj.magic.model.PromoType2Rule;

@Service
public class PromoServiceImpl implements PromoService {

	@Autowired private PromoDao promoDao;
	@Autowired private PromoType2RuleDao promoType2RuleDao;
	@Autowired private ProductDao productDao;
	@Autowired private PromoRedemptionDao promoRedemptionDao;
	
	@Override
	public List<Promo> getAllPromos() {
		return promoDao.getAll();
	}

	@Transactional
	@Override
	public void save(Promo promo) {
		boolean isNew = (promo.getId() == null);
		promoDao.save(promo);
		if (isNew) {
			promoRedemptionDao.insertNewPromoRedemptionSequence(promo);
		}
	}

	@Override
	public Promo getPromo(long id) {
		Promo promo = promoDao.get(id);
		loadPromoDetails(promo);
		return promo;
	}

	private void loadPromoDetails(Promo promo) {
		switch (promo.getPromoType().getId().intValue()) {
		case 2:
			promo.setPromoType2Rules(promoType2RuleDao.findAllByPromo(promo));
			for (PromoType2Rule rule : promo.getPromoType2Rules()) {
				rule.setParent(promo);
				rule.setPromoProduct(productDao.get(rule.getPromoProduct().getId()));
				rule.setFreeProduct(productDao.get(rule.getFreeProduct().getId()));
			}
			break;
		}
	}

	@Transactional
	@Override
	public void save(PromoType2Rule rule) {
		promoType2RuleDao.save(rule);
	}

	@Transactional
	@Override
	public void delete(PromoType2Rule rule) {
		promoType2RuleDao.delete(rule);
	}

	@Override
	public List<Promo> getAllActivePromos() {
		List<Promo> promos = promoDao.findAllByActive(true);
		for (Promo promo : promos) {
			loadPromoDetails(promo);
		}
		return promos;
	}

}