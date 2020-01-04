package com.pj.magic.gui.tables.rowitems;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

import com.pj.magic.model.BadStockReportItem;
import com.pj.magic.model.Product;

import lombok.Getter;
import lombok.Setter;

/*
 * Wrapper class to separate table gui concerns of inputting Bad Stock Report items
 * from the business logic of Bad Stock Report item model.
 */
@Getter
@Setter
public class BadStockReportItemRowItem {

	private BadStockReportItem item;
	private Product product;
	private String unit;
	private Integer quantity;

	public BadStockReportItemRowItem(BadStockReportItem item) {
		this.item = item;
		reset();
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
        if (!(obj instanceof BadStockReportItemRowItem)) {
            return false;
        }
        BadStockReportItemRowItem other = (BadStockReportItemRowItem)obj;		
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

}
