package com.pj.magic.model.search;

import java.util.Date;

import com.pj.magic.model.PricingScheme;
import com.pj.magic.model.PromoType;

public class PromoSearchCriteria {

	private PromoType promoType;
	private Date promoDate;
	private Boolean active;
	private PricingScheme acceptedPricingScheme;
	
	public PromoType getPromoType() {
		return promoType;
	}

	public void setPromoType(PromoType promoType) {
		this.promoType = promoType;
	}

	public Date getPromoDate() {
		return promoDate;
	}

	public void setPromoDate(Date promoDate) {
		this.promoDate = promoDate;
	}

	public PricingScheme getAcceptedPricingScheme() {
		return acceptedPricingScheme;
	}

	/**
	 * Search criteria for promos that will accept the specified pricing scheme
	 */
	public void setAcceptedPricingScheme(PricingScheme pricingScheme) {
		this.acceptedPricingScheme = pricingScheme;
	}

	public Boolean getActive() {
		return active;
	}

	public void setActive(Boolean active) {
		this.active = active;
	}
	
}