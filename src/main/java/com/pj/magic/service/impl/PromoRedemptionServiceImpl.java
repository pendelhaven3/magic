package com.pj.magic.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pj.magic.dao.ProductDao;
import com.pj.magic.dao.PromoRedemptionDao;
import com.pj.magic.dao.PromoRedemptionRewardDao;
import com.pj.magic.dao.PromoRedemptionSalesInvoiceDao;
import com.pj.magic.dao.SalesInvoiceItemDao;
import com.pj.magic.exception.AlreadyPostedException;
import com.pj.magic.exception.NotEnoughStocksException;
import com.pj.magic.exception.NothingToRedeemException;
import com.pj.magic.model.AvailedPromoPointsItem;
import com.pj.magic.model.Customer;
import com.pj.magic.model.Product;
import com.pj.magic.model.Promo;
import com.pj.magic.model.PromoRedemption;
import com.pj.magic.model.PromoRedemptionReward;
import com.pj.magic.model.PromoRedemptionSalesInvoice;
import com.pj.magic.model.PromoType;
import com.pj.magic.model.PromoType1Rule;
import com.pj.magic.model.PromoType2Rule;
import com.pj.magic.model.PromoType3Rule;
import com.pj.magic.model.SalesInvoice;
import com.pj.magic.model.search.PromoRedemptionSearchCriteria;
import com.pj.magic.model.search.PromoSearchCriteria;
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
		criteria.setTransactionDateFrom(promo.getStartDate());
		criteria.setPricingScheme(promo.getPricingScheme());
		
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
		case PROMO_TYPE_4:
			break;
		}
		
		updated.setPosted(true);
		updated.setPostDate(new Date());
		updated.setPostedBy(loginService.getLoggedInUser());
		promoRedemptionDao.save(updated);
	}

	private void postForPromoType3(PromoRedemption promoRedemption) {
		promoRedemption.validateSalesInvoicesPricingScheme();
		
		PromoType3Rule rule = promoRedemption.getPromo().getPromoType3Rule();
		PromoRedemptionReward reward = rule.evaluate(promoRedemption.getSalesInvoices());
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
		promoRedemption.validateSalesInvoicesPricingScheme();
		
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

	private List<PromoRedemption> search(PromoRedemptionSearchCriteria criteria) {
		List<PromoRedemption> promoRedemptions = promoRedemptionDao.search(criteria);
		for (PromoRedemption promoRedemption : promoRedemptions) {
			promoRedemption.setRewards(promoRedemptionRewardDao.findAllByPromoRedemption(promoRedemption));
		}
		return promoRedemptions;
	}

	@Override
	public List<PromoRedemption> findAllAvailedPromoRedemptions(SalesInvoice salesInvoice) {
		PromoRedemptionSearchCriteria criteria = new PromoRedemptionSearchCriteria();
		criteria.setSalesInvoice(salesInvoice);
		criteria.setPosted(true);
		criteria.setPromoType(PromoType.PROMO_TYPE_2);
		
		List<PromoRedemption> promoRedemptions = search(criteria);
		for (PromoRedemption promoRedemption : promoRedemptions) {
			promoRedemption.setPromo(promoService.getPromo(promoRedemption.getPromo().getId()));
		}
		
		return promoRedemptions;
	}

	@Override
	public List<AvailedPromoPointsItem> findAllAvailedPromoPoints(SalesInvoice salesInvoice) {
		List<AvailedPromoPointsItem> items = new ArrayList<>();
		
		PromoSearchCriteria criteria = new PromoSearchCriteria();
		criteria.setPromoType(PromoType.PROMO_TYPE_4);
		criteria.setPromoDate(salesInvoice.getTransactionDate());
		criteria.setAcceptedPricingScheme(salesInvoice.getPricingScheme());
		
		for (Promo promo : promoService.search(criteria)) {
			AvailedPromoPointsItem item = promo.evaluateForPoints(salesInvoice);
			if (item.hasPoints()) {
				items.add(item);
			}
		}
		return items;
	}

}