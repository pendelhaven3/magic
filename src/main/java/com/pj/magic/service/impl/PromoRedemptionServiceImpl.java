package com.pj.magic.service.impl;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.pj.magic.dao.ProductDao;
import com.pj.magic.dao.PromoDao;
import com.pj.magic.dao.PromoRedemptionDao;
import com.pj.magic.dao.PromoRedemptionRewardDao;
import com.pj.magic.dao.PromoRedemptionSalesInvoiceDao;
import com.pj.magic.dao.SalesInvoiceItemDao;
import com.pj.magic.exception.AlreadyPostedException;
import com.pj.magic.exception.NotEnoughStocksException;
import com.pj.magic.exception.NothingToRedeemException;
import com.pj.magic.model.Customer;
import com.pj.magic.model.Product;
import com.pj.magic.model.Promo;
import com.pj.magic.model.PromoRedemption;
import com.pj.magic.model.PromoRedemptionReward;
import com.pj.magic.model.PromoRedemptionSalesInvoice;
import com.pj.magic.model.PromoType1Rule;
import com.pj.magic.model.PromoType2Rule;
import com.pj.magic.model.PromoType3Rule;
import com.pj.magic.model.SalesInvoice;
import com.pj.magic.model.search.SalesInvoiceSearchCriteria;
import com.pj.magic.service.LoginService;
import com.pj.magic.service.PromoRedemptionService;
import com.pj.magic.service.SalesInvoiceService;

@Service
public class PromoRedemptionServiceImpl implements PromoRedemptionService {

	@Autowired private PromoRedemptionDao promoRedemptionDao;
	@Autowired private SalesInvoiceService salesInvoiceService;
	@Autowired private PromoRedemptionSalesInvoiceDao promoRedemptionSalesInvoiceDao;
	@Autowired private SalesInvoiceItemDao salesInvoiceItemDao;
	@Autowired private LoginService loginService;
	@Autowired private PromoService promoService;
	@Autowired private ProductDao productDao;
	@Autowired private PromoDao promoDao;
	@Autowired private PromoRedemptionRewardDao promoRedemptionRewardDao;
	
	@Transactional
	@Override
	public void save(PromoRedemption promoRedemption) {
		boolean isNew = (promoRedemption.getId() == null);
		promoRedemptionDao.save(promoRedemption);
		if (!isNew) {
			promoRedemptionSalesInvoiceDao.deleteAllByPromoRedemption(promoRedemption);
		}
	}

	@Override
	public List<SalesInvoice> findAllUnreedemedSalesInvoices(Promo promo, Customer customer) {
		SalesInvoiceSearchCriteria criteria = new SalesInvoiceSearchCriteria();
		criteria.setUnredeemedPromo(promo);
		criteria.setCustomer(customer);
		try {
			criteria.setTransactionDateFrom(new SimpleDateFormat("MM/dd/yyyy").parse("02/23/2015"));
		} catch (ParseException e) {
			// do nothing!
		}
		
		return salesInvoiceService.search(criteria);
	}

	@Transactional
	@Override
	public void save(PromoRedemptionSalesInvoice salesInvoice) {
		promoRedemptionSalesInvoiceDao.save(salesInvoice);
	}

	@Override
	public PromoRedemption getPromoRedemption(long id) {
		PromoRedemption promoRedemption = promoRedemptionDao.get(id);
		promoRedemption.setPromo(promoService.getPromo(promoRedemption.getPromo().getId()));
		
		promoRedemption.setRedemptionSalesInvoices(promoRedemptionSalesInvoiceDao
				.findAllByPromoRedemption(promoRedemption));
		for (PromoRedemptionSalesInvoice promoRedemptionSalesInvoice : promoRedemption.getRedemptionSalesInvoices()) {
			SalesInvoice salesInvoice = promoRedemptionSalesInvoice.getSalesInvoice();
			salesInvoice.setItems(salesInvoiceItemDao.findAllBySalesInvoice(salesInvoice));
		}
		
		if (promoRedemption.isPosted()) {
			promoRedemption.setRewards(promoRedemptionRewardDao.findAllByPromoRedemption(promoRedemption));
		}
		
		return promoRedemption;
	}

	@Transactional
	@Override
	public void delete(PromoRedemptionSalesInvoice salesInvoice) {
		promoRedemptionSalesInvoiceDao.delete(salesInvoice);
	}

	@Transactional
	@Override
	public void post(PromoRedemption promoRedemption) {
		PromoRedemption updated = getPromoRedemption(promoRedemption.getId());
		
		if (updated.isPosted()) {
			throw new AlreadyPostedException();
		}
		
		switch (updated.getPromo().getPromoType()) {
		case PROMO_TYPE_1:
			postForPromoType1(updated);
			break;
		case PROMO_TYPE_2:
			postForPromoType2(updated);
			break;
		case PROMO_TYPE_3:
			postForPromoType3(updated);
			break;
		}
		
		updated.setPosted(true);
		updated.setPostDate(new Date());
		updated.setPostedBy(loginService.getLoggedInUser());
		promoRedemptionDao.save(updated);
	}

	private void postForPromoType3(PromoRedemption promoRedemption) {
		PromoType3Rule rule = promoRedemption.getPromo().getPromoType3Rule();
		List<SalesInvoice> salesInvoices = new ArrayList<>(Collections2.transform(
				promoRedemption.getRedemptionSalesInvoices(), 
				new Function<PromoRedemptionSalesInvoice, SalesInvoice>() {

					@Override
					public SalesInvoice apply(PromoRedemptionSalesInvoice input) {
						return input.getSalesInvoice();
					}
				}));
		
		PromoRedemptionReward reward = rule.evaluate(salesInvoices);
		if (reward != null) {
			int freeQuantity = reward.getQuantity().intValue();
			Product product = productDao.get(rule.getFreeProduct().getId());
			
			if (product.getUnitQuantity(rule.getFreeUnit()) < freeQuantity) {
				throw new NotEnoughStocksException();
			}
			
			product.addUnitQuantity(rule.getFreeUnit(), -1 * freeQuantity);
			productDao.updateAvailableQuantities(product);
			
			reward.setParent(promoRedemption);
			reward.setProduct(rule.getFreeProduct());
			reward.setUnit(rule.getFreeUnit());
			reward.setQuantity(freeQuantity);
			promoRedemptionRewardDao.save(reward);
		}
	}

	private void postForPromoType2(PromoRedemption promoRedemption) {
		for (PromoType2Rule rule : promoRedemption.getPromo().getPromoType2Rules()) {
			int freeQuantity = promoRedemption.getFreeQuantity(rule);
			if (freeQuantity > 0) {
				Product product = productDao.get(rule.getFreeProduct().getId());
				
				if (product.getUnitQuantity(rule.getFreeUnit()) < freeQuantity) {
					throw new NotEnoughStocksException();
				}
				
				product.addUnitQuantity(rule.getFreeUnit(), -1 * freeQuantity);
				productDao.updateAvailableQuantities(product);
				
				PromoRedemptionReward reward = new PromoRedemptionReward();
				reward.setParent(promoRedemption);
				reward.setProduct(rule.getFreeProduct());
				reward.setUnit(rule.getFreeUnit());
				reward.setQuantity(freeQuantity);
				promoRedemptionRewardDao.save(reward);
			}
		}
	}

	private void postForPromoType1(PromoRedemption promoRedemption) {
		PromoType1Rule rule = promoRedemption.getPromo().getPromoType1Rule();
		PromoRedemptionReward reward = rule.evaluate(promoRedemption.getSalesInvoices());
		if (reward == null) {
			throw new NothingToRedeemException();
		}
		
		Product product = productDao.get(rule.getProduct().getId());
		if (product.getUnitQuantity(rule.getUnit()) < rule.getQuantity().intValue()) {
			throw new NotEnoughStocksException();
		}
		
		product.addUnitQuantity(rule.getUnit(), -1 * reward.getQuantity().intValue());
		productDao.updateAvailableQuantities(product);
		
		reward.setParent(promoRedemption);
		promoRedemptionRewardDao.save(reward);
	}

	@Override
	public List<PromoRedemption> getPromoRedemptionsByPromo(Promo promo) {
		return promoRedemptionDao.findAllByPromo(promo);
	}

	@Override
	public List<PromoRedemption> findAllBySalesInvoice(SalesInvoice salesInvoice) {
		List<PromoRedemption> promoRedemptions = promoRedemptionDao.findAllBySalesInvoice(salesInvoice);
		for (PromoRedemption promoRedemption : promoRedemptions) {
			promoRedemption.setRewards(promoRedemptionRewardDao.findAllByPromoRedemption(promoRedemption));
		}
		return promoRedemptions;
	}

}