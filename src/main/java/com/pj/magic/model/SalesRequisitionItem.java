package com.pj.magic.model;

import java.math.BigDecimal;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

public class SalesRequisitionItem {

	private Product product;
	private String unit;
	private Integer quantity;

	public boolean isValid() {
		return (product != null && product.isValid())
				&& product.getUnits().contains(unit)
				&& (quantity != null && quantity.intValue() > 0);
	}
	
	public BigDecimal getUnitPrice() {
		if (product == null || !product.isValid() || !product.hasUnit(unit)) {
			return null;
		}
		
		for (UnitPrice unitPrice : product.getUnitPrices()) {
			if (unitPrice.getUnit().equals(unit)) {
				return unitPrice.getPrice();
			}
		}
		return null;
	}
	
	public BigDecimal getAmount() {
		if (product == null || !product.isValid() || quantity == null) {
			return null;
		}
		return getUnitPrice().multiply(new BigDecimal(quantity.intValue()));
	}
	
	public Product getProduct() {
		return product;
	}
	
	public void setProduct(Product product) {
		this.product = product;
	}
	
	public String getUnit() {
		return unit;
	}

	public void setUnit(String unit) {
		this.unit = unit;
	}

	public Integer getQuantity() {
		return quantity;
	}

	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder()
			.append(product)
			.append(unit)
			.toHashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
        if (!(obj instanceof SalesRequisitionItem)) {
            return false;
        }
        SalesRequisitionItem other = (SalesRequisitionItem)obj;		
		return new EqualsBuilder()
			.append(product, other.getProduct())
			.append(unit, other.getUnit())
			.isEquals();
	}
	
}
