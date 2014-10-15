package com.pj.magic.model.util;

import com.pj.magic.Constants;
import com.pj.magic.model.Manufacturer;
import com.pj.magic.model.PricingScheme;
import com.pj.magic.model.ProductCategory;
import com.pj.magic.model.ProductSubcategory;

public class ProductSearchCriteria {

	private Boolean active;
	private PricingScheme pricingScheme = new PricingScheme(Constants.CANVASSER_PRICING_SCHEME_ID);
	private String code;
	private Manufacturer manufacturer;
	private ProductCategory category;
	private ProductSubcategory subcategory;
	
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

	public Manufacturer getManufacturer() {
		return manufacturer;
	}

	public void setManufacturer(Manufacturer manufacturer) {
		this.manufacturer = manufacturer;
	}

	public ProductCategory getCategory() {
		return category;
	}

	public void setCategory(ProductCategory category) {
		this.category = category;
	}

	public ProductSubcategory getSubcategory() {
		return subcategory;
	}

	public void setSubcategory(ProductSubcategory subcategory) {
		this.subcategory = subcategory;
	}

}
