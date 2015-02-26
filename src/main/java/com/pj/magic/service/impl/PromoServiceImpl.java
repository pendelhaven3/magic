package com.pj.magic.service.impl;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pj.magic.dao.PromoDao;
import com.pj.magic.model.Promo;

@Service
public class PromoServiceImpl implements PromoService {

	@Autowired private PromoDao promoDao;
	
	@Override
	public List<Promo> getAllPromos() {
		return promoDao.getAll();
	}

	@Transactional
	@Override
	public void save(Promo promo) {
		promoDao.save(promo);
	}

	@Override
	public Promo getPromo(long id) {
		return promoDao.get(id);
	}

}
