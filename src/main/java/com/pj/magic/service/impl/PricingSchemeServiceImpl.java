package com.pj.magic.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pj.magic.dao.PricingSchemeDao;
import com.pj.magic.model.PricingScheme;
import com.pj.magic.service.PricingSchemeService;

@Service
public class PricingSchemeServiceImpl implements PricingSchemeService {

	@Autowired private PricingSchemeDao pricingSchemeDao;
	
	@Override
	public List<PricingScheme> getAllPricingSchemes() {
		return pricingSchemeDao.getAll();
	}

	@Transactional
	@Override
	public void save(PricingScheme pricingScheme) {
		pricingSchemeDao.save(pricingScheme);
	}

	@Override
	public PricingScheme get(long id) {
		return pricingSchemeDao.get(id);
	}

}
