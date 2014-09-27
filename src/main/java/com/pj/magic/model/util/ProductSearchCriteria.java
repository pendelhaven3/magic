package com.pj.magic.model.util;

import com.pj.magic.Constants;
import com.pj.magic.model.PricingScheme;

public class ProductSearchCriteria {

	private boolean active;
	private PricingScheme pricingScheme = new PricingScheme(Constants.CANVASSER_PRICING_SCHEME_ID);

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public PricingScheme getPricingScheme() {
		return pricingScheme;
	}

	public void setPricingScheme(PricingScheme pricingScheme) {
		this.pricingScheme = pricingScheme;
	}

}
