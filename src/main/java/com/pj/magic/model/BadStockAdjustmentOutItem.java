package com.pj.magic.model;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BadStockAdjustmentOutItem implements Comparable<BadStockAdjustmentOutItem> {

	private Long id;
	private BadStockAdjustmentOut parent;
	private Product product;
	private String unit;
	private Integer quantity;

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
        if (!(obj instanceof BadStockAdjustmentOutItem)) {
            return false;
        }
        BadStockAdjustmentOutItem other = (BadStockAdjustmentOutItem)obj;     
        return new EqualsBuilder()
            .append(product, other.getProduct())
            .append(unit, other.getUnit())
            .isEquals();
    }
	
    @Override
    public int compareTo(BadStockAdjustmentOutItem o) {
        int result = product.compareTo(o.getProduct());
        if (result == 0) {
            return Unit.compare(unit, o.getUnit());
        } else {
            return result;
        }
    }
    
}