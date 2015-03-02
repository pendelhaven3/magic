package com.pj.magic.service.impl;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
import com.pj.magic.model.PromoPrize;
import com.pj.magic.model.PromoRedemption;
import com.pj.magic.model.PromoRedemptionReward;
import com.pj.magic.model.PromoRedemptionSalesInvoice;
import com.pj.magic.model.PromoType2Rule;
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
		
		promoRedemption.setSalesInvoices(promoRedemptionSalesInvoiceDao
				.findAllByPromoRedemption(promoRedemption));
		for (PromoRedemptionSalesInvoice promoRedemptionSalesInvoice : promoRedemption.getSalesInvoices()) {
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
		
		if (updated.getPromo().getPromoType().isType1()) {
			updated.setPrizeQuantity(updated.getPrizeQuantity());
		} else {
			updated.setPrizeQuantity(0);
		}
		updated.setPosted(true);
		updated.setPostDate(new Date());
		updated.setPostedBy(loginService.getLoggedInUser());
		promoRedemptionDao.save(updated);
		
		if (updated.getPromo().getPromoType().isType1()) {
			postForPromoType1(updated);
		} else if (updated.getPromo().getPromoType().isType2()) {
			postForPromoType2(updated);
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
		if (promoRedemption.getPrizeQuantity() == 0) {
			throw new NothingToRedeemException();
		}
		
		PromoPrize prize = promoRedemption.getPromo().getPrize();
		Product product = productDao.get(prize.getProduct().getId());
		
		if (product.getUnitQuantity(prize.getUnit()) < promoRedemption.getPrizeQuantity()) {
			throw new NotEnoughStocksException();
		}
		
		product.addUnitQuantity(prize.getUnit(), -1 * promoRedemption.getPrizeQuantity());
		productDao.updateAvailableQuantities(product);
	}

	@Override
	public List<PromoRedemption> getPromoRedemptionsByPromo(Promo promo) {
		return promoRedemptionDao.findAllByPromo(promo);
	}

}