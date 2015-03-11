package com.pj.magic.model.search;

import com.pj.magic.model.PromoType;
import com.pj.magic.model.SalesInvoice;

public class PromoRedemptionSearchCriteria {

	private SalesInvoice salesInvoice;
	private Boolean posted;
	private PromoType promoType;

	public SalesInvoice getSalesInvoice() {
		return salesInvoice;
	}

	public void setSalesInvoice(SalesInvoice salesInvoice) {
		this.salesInvoice = salesInvoice;
	}
	
	public Boolean getPosted() {
		return posted;
	}

	public void setPosted(Boolean posted) {
		this.posted = posted;
	}

	public PromoType getPromoType() {
		return promoType;
	}

	public void setPromoType(PromoType promoType) {
		this.promoType = promoType;
	}

}