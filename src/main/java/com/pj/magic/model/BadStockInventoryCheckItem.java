package com.pj.magic.model;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BadStockInventoryCheckItem {

	private Long id;
	private BadStockInventoryCheck parent;
	private Product product;
	private String unit;
	private Integer quantity;
	private Integer quantityChange;

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
        if (!(obj instanceof BadStockInventoryCheckItem)) {
            return false;
        }
        BadStockInventoryCheckItem other = (BadStockInventoryCheckItem)obj;		
		return new EqualsBuilder()
			.append(product, other.getProduct())
			.append(unit, other.getUnit())
			.isEquals();
	}

}