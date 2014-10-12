package com.pj.magic.model.util;

import com.pj.magic.Constants;
import com.pj.magic.model.PricingScheme;

public class ProductSearchCriteria {

	private Boolean active;
	private PricingScheme pricingScheme = new PricingScheme(Constants.CANVASSER_PRICING_SCHEME_ID);
	private String code;
	
	public Boolean getActive() {
		return active;
	}

	public void setActive(Boolean active) {
		this.active = active;
	}

	public PricingScheme getPricingScheme() {
		return pricingScheme;
	}

	public void setPricingScheme(PricingScheme pricingScheme) {
		this.pricingScheme = pricingScheme;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

}
