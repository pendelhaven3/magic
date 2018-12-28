package com.pj.magic.model;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

public class BadStockAdjustmentInItem implements Comparable<BadStockAdjustmentInItem> {

	private Long id;
	private BadStockAdjustmentIn parent;
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
        if (!(obj instanceof BadStockAdjustmentInItem)) {
            return false;
        }
        BadStockAdjustmentInItem other = (BadStockAdjustmentInItem)obj;     
        return new EqualsBuilder()
            .append(product, other.getProduct())
            .append(unit, other.getUnit())
            .isEquals();
    }
	
    @Override
    public int compareTo(BadStockAdjustmentInItem o) {
        int result = product.compareTo(o.getProduct());
        if (result == 0) {
            return Unit.compare(unit, o.getUnit());
        } else {
            return result;
        }
    }
    
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
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

	public BadStockAdjustmentIn getParent() {
		return parent;
	}

	public void setParent(BadStockAdjustmentIn parent) {
		this.parent = parent;
	}

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }
	
}