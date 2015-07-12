package com.pj.magic.dao;

import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.pj.magic.model.PromoRedemption;
import com.pj.magic.model.PromoRedemptionSalesInvoice;
import com.pj.magic.model.SalesInvoice;

@Ignore

public class PromoRedemptionSalesInvoiceDaoTest extends IntegrationTest {
	
	@Autowired private PromoRedemptionSalesInvoiceDao promoRedemptionSalesInvoiceDao;
	
	@Test
	public void save() {
		PromoRedemptionSalesInvoice salesInvoice = new PromoRedemptionSalesInvoice();
		salesInvoice.setParent(new PromoRedemption(1L));
		salesInvoice.setSalesInvoice(new SalesInvoice(1L));
		
		promoRedemptionSalesInvoiceDao.save(salesInvoice);
	}
	
}