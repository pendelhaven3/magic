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
import com.pj.magic.model.PromoRedemptionSalesInvoice;
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
	@Autowired private ProductDao productDao;
	@Autowired private PromoDao promoDao;
	
	@Transactional
	@Override
	public void save(PromoRedemption promoRedemption) {
		boolean isNew = (promoRedemption.getId() == null);
		promoRedemption.setPromo(promoDao.get(1L)); // TODO: hardcoded promo id
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
	public List<PromoRedemption> getAllPromoRedemptions() {
		return promoRedemptionDao.getAll();
	}

	@Override
	public PromoRedemption getPromoRedemption(long id) {
		PromoRedemption promoRedemption = promoRedemptionDao.get(id);
		promoRedemption.setSalesInvoices(promoRedemptionSalesInvoiceDao
				.findAllByPromoRedemption(promoRedemption));
		for (PromoRedemptionSalesInvoice promoRedemptionSalesInvoice : promoRedemption.getSalesInvoices()) {
			SalesInvoice salesInvoice = promoRedemptionSalesInvoice.getSalesInvoice();
			salesInvoice.setItems(salesInvoiceItemDao.findAllBySalesInvoice(salesInvoice));
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
		
		if (updated.getPrizeQuantity() == 0) {
			throw new NothingToRedeemException();
		}
		
		updated.setPrizeQuantity(updated.getPrizeQuantity());
		updated.setPosted(true);
		updated.setPostDate(new Date());
		updated.setPostedBy(loginService.getLoggedInUser());
		promoRedemptionDao.save(updated);
		
		PromoPrize prize = updated.getPromo().getPrize();
		Product product = productDao.get(prize.getProduct().getId());
		
		if (product.getUnitQuantity(prize.getUnit()) < updated.getPrizeQuantity()) {
			throw new NotEnoughStocksException();
		}
		
		product.addUnitQuantity(prize.getUnit(), -1 * updated.getPrizeQuantity());
		productDao.updateAvailableQuantities(product);
	}

}