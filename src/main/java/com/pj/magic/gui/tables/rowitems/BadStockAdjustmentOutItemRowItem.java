package com.pj.magic.gui.tables.rowitems;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

import com.pj.magic.model.BadStockAdjustmentOutItem;
import com.pj.magic.model.Product;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BadStockAdjustmentOutItemRowItem {

	private BadStockAdjustmentOutItem item;
	private Product product;
	private String unit;
	private Integer quantity;

	public BadStockAdjustmentOutItemRowItem(BadStockAdjustmentOutItem item) {
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
        if (!(obj instanceof BadStockAdjustmentOutItemRowItem)) {
            return false;
        }
        BadStockAdjustmentOutItemRowItem other = (BadStockAdjustmentOutItemRowItem)obj;       
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
    
}