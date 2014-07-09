package com.pj.magic.model;

import org.apache.commons.lang.StringUtils;

public class Item {

	private String productCode;
	private String productDescription;
	private String unit;
	private String quantity;

	public boolean isValid() {
		return !StringUtils.isEmpty(productCode)
				&& !StringUtils.isEmpty(unit)
				&& !StringUtils.isEmpty(quantity);
	}
	
	public String getProductCode() {
		return productCode;
	}

	public void setProductCode(String productCode) {
		this.productCode = productCode;
	}

	public String getUnit() {
		return unit;
	}

	public void setUnit(String unit) {
		this.unit = unit;
	}

	public String getQuantity() {
		return quantity;
	}

	public void setQuantity(String quantity) {
		this.quantity = quantity;
	}

	public String getProductDescription() {
		return productDescription;
	}

	public void setProductDescription(String productDescription) {
		this.productDescription = productDescription;
	}

}
