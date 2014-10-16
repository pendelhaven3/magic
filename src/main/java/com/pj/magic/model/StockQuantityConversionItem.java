package com.pj.magic.model;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.springframework.util.StringUtils;

public class StockQuantityConversionItem {

	private Long id;
	private StockQuantityConversion parent;
	private Product product;
	private String fromUnit;
	private String toUnit;
	private Integer quantity;

	public boolean isValid() {
		return product != null && product.hasUnit(fromUnit) && product.hasUnit(toUnit)
				&& (quantity != null && isQuantityValid());
	}
	
	public boolean isQuantityValid() {
		return product.hasAvailableUnitQuantity(fromUnit, quantity);
	}
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}
	
	public StockQuantityConversion getParent() {
		return parent;
	}

	public void setParent(StockQuantityConversion parent) {
		this.parent = parent;
	}

	public Product getProduct() {
		return product;
	}
	
	public void setProduct(Product product) {
		this.product = product;
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
			.toHashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
        if (!(obj instanceof StockQuantityConversionItem)) {
            return false;
        }
        StockQuantityConversionItem other = (StockQuantityConversionItem)obj;		
		return new EqualsBuilder()
			.append(product, other.getProduct())
			.isEquals();
	}
	
	// TODO: Remove this
	public boolean isFilledUp() {
		return product != null && !StringUtils.isEmpty(fromUnit) 
				&& !StringUtils.isEmpty(toUnit) && quantity != null;
	}

	public String getFromUnit() {
		return fromUnit;
	}

	public void setFromUnit(String fromUnit) {
		this.fromUnit = fromUnit;
	}

	public String getToUnit() {
		return toUnit;
	}

	public void setToUnit(String toUnit) {
		this.toUnit = toUnit;
	}

	public int getConvertedQuantity() {
		return product.getUnitConversion(fromUnit) / product.getUnitConversion(toUnit) * quantity;
	}
	
}
