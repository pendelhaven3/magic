package com.pj.magic.gui.tables.rowitems;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

import com.pj.magic.model.BadStockAdjustmentInItem;
import com.pj.magic.model.Product;

/*
 * Wrapper class to separate table gui concerns of inputting adjustment in items
 * from the business logic of adjustment in item model.
 */
public class BadStockAdjustmentInItemRowItem {

	private BadStockAdjustmentInItem item;
	private Product product;
	private String unit;
	private Integer quantity;

	public BadStockAdjustmentInItemRowItem(BadStockAdjustmentInItem item) {
		this.item = item;
		if (item.getProduct() != null) {
			product = item.getProduct();
		}
		unit = item.getUnit();
		if (item.getQuantity() != null) {
			quantity = item.getQuantity();
		}
	}
	
    public String getProductCode() {
        return (product != null) ? product.getCode() : null;
    }
	
    public boolean isValid() {
        return product != null && !StringUtils.isEmpty(unit) && quantity != null;  
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
        if (!(obj instanceof BadStockAdjustmentInItemRowItem)) {
            return false;
        }
        BadStockAdjustmentInItemRowItem other = (BadStockAdjustmentInItemRowItem)obj;       
        return new EqualsBuilder()
            .append(product, other.getProduct())
            .append(unit, other.getUnit())
            .isEquals();
    }

    public boolean isUpdating() {
        return item.getId() != null;
    }

    public void reset() {
        if (item.getId() != null) {
            product = item.getProduct();
            unit = item.getUnit();
            quantity = item.getQuantity();
        }
    }

    public boolean hasValidProduct() {
        return product != null;
    }
    
    public boolean hasValidUnit() {
        return !StringUtils.isEmpty(unit);
    }
    
	public BadStockAdjustmentInItem getItem() {
		return item;
	}

	public void setItem(BadStockAdjustmentInItem item) {
		this.item = item;
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

	public Product getProduct() {
		return product;
	}

	public void setProduct(Product product) {
		this.product = product;
	}

}