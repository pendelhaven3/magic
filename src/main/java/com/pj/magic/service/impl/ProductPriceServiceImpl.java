package com.pj.magic.service.impl;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pj.magic.Constants;
import com.pj.magic.dao.ProductPriceHistoryDao;
import com.pj.magic.model.PricingScheme;
import com.pj.magic.model.ProductPriceHistory;
import com.pj.magic.service.ProductPriceService;

@Service
public class ProductPriceServiceImpl implements ProductPriceService {

	@Autowired private ProductPriceHistoryDao productPriceHistoryDao;
	
	@Override
	public List<ProductPriceHistory> getAllProductPriceHistoriesByDate(Date date) {
		return productPriceHistoryDao.findAllByUpdateDateAndPricingScheme(
				date, new PricingScheme(Constants.CANVASSER_PRICING_SCHEME_ID));
	}

}