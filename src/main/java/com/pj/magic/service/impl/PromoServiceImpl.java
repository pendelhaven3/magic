package com.pj.magic.service.impl;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pj.magic.dao.ProductDao;
import com.pj.magic.dao.PromoDao;
import com.pj.magic.dao.PromoRedemptionDao;
import com.pj.magic.dao.PromoType1RuleDao;
import com.pj.magic.dao.PromoType2RuleDao;
import com.pj.magic.dao.PromoType3RuleDao;
import com.pj.magic.dao.PromoType3RulePromoProductDao;
import com.pj.magic.model.Promo;
import com.pj.magic.model.PromoType2Rule;
import com.pj.magic.model.PromoType3Rule;
import com.pj.magic.model.PromoType3RulePromoProduct;

@Service
public class PromoServiceImpl implements PromoService {

	@Autowired private PromoDao promoDao;
	@Autowired private PromoType1RuleDao promoType1RuleDao;
	@Autowired private PromoType2RuleDao promoType2RuleDao;
	@Autowired private PromoType3RuleDao promoType3RuleDao;
	@Autowired private ProductDao productDao;
	@Autowired private PromoRedemptionDao promoRedemptionDao;
	@Autowired private PromoType3RulePromoProductDao promoType3RulePromoProductDao;
	
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
		} else {
			if (promo.getPromoType().isType1()) {
				promoType1RuleDao.save(promo.getPromoType1Rule());
			} else if (promo.getPromoType().isType3()) {
				promoType3RuleDao.save(promo.getPromoType3Rule());
			}
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
		case 1:
			promo.setPromoType1Rule(promoType1RuleDao.findByPromo(promo));
			break;
		case 2:
			promo.setPromoType2Rules(promoType2RuleDao.findAllByPromo(promo));
			for (PromoType2Rule rule : promo.getPromoType2Rules()) {
				rule.setParent(promo);
				rule.setPromoProduct(productDao.get(rule.getPromoProduct().getId()));
				rule.setFreeProduct(productDao.get(rule.getFreeProduct().getId()));
			}
			break;
		case 3:
			PromoType3Rule rule = promoType3RuleDao.findByPromo(promo);
			if (rule != null) {
				rule.setPromoProducts(promoType3RulePromoProductDao.findAllByRule(rule));
			}
			promo.setPromoType3Rule(rule);
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

	@Transactional
	@Override
	public void save(PromoType3RulePromoProduct promoProduct) {
		promoType3RulePromoProductDao.save(promoProduct);
	}

	@Transactional
	@Override
	public void delete(PromoType3RulePromoProduct promoProduct) {
		promoType3RulePromoProductDao.delete(promoProduct);		
	}

}