package com.pj.magic.model;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

public class Item {

	private String productCode;
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

	@Override
	public int hashCode() {
		return new HashCodeBuilder()
			.append(productCode)
			.append(unit)
			.toHashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
        if (!(obj instanceof Item)) {
            return false;
        }
        Item other = (Item)obj;		
		return new EqualsBuilder()
			.append(productCode, other.getProductCode())
			.append(unit, other.getUnit())
			.isEquals();
	}
	
}
