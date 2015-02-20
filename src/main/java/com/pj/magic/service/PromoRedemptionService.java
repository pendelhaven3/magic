package com.pj.magic.service;

import java.util.List;

import com.pj.magic.model.Customer;
import com.pj.magic.model.Promo;
import com.pj.magic.model.PromoRedemption;
import com.pj.magic.model.PromoRedemptionSalesInvoice;
import com.pj.magic.model.SalesInvoice;

public interface PromoRedemptionService {

	void save(PromoRedemption promoRedemption);

	List<SalesInvoice> findAllUnreedemedSalesInvoices(Promo promo, Customer customer);
	
	void save(PromoRedemptionSalesInvoice salesInvoice);

	List<PromoRedemption> getAllPromoRedemptions();
	
	PromoRedemption getPromoRedemption(long id);

	void delete(PromoRedemptionSalesInvoice salesInvoice);
	
}