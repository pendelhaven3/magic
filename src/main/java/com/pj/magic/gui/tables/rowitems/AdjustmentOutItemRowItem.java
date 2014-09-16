package com.pj.magic.gui.tables.rowitems;

import java.math.BigDecimal;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

import com.pj.magic.model.AdjustmentOutItem;
import com.pj.magic.model.Product;

/*
 * Wrapper class to separate table gui concerns of inputting adjustment out items
 * from the business logic of adjustment out item model.
 */
public class AdjustmentOutItemRowItem {

	private AdjustmentOutItem item;
	private String productCode;
	private String unit;
	private String quantity;
	private Product product;

	public AdjustmentOutItemRowItem(AdjustmentOutItem item) {
		this.item = item;
		if (item.getProduct() != null) {
			productCode = item.getProduct().getCode();
			product = item.getProduct();
		}
		unit = item.getUnit();
		if (item.getQuantity() != null) {
			quantity = item.getQuantity().toString();
		}
	}
	
	public AdjustmentOutItem getItem() {
		return item;
	}

	public void setItem(AdjustmentOutItem item) {
		this.item = item;
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

	public Product getProduct() {
		return product;
	}

	public void setProduct(Product product) {
		this.product = product;
	}

	public boolean isValid() {
		return product != null && product.hasUnit(unit) 
				&& (!StringUtils.isEmpty(quantity) && StringUtils.isNumeric(quantity));
	}

	public BigDecimal getUnitPrice() {
		if (product != null && product.hasUnit(unit)) {
			return product.getUnitPrice(unit);
		} else {
			return null;
		}
	}

	public BigDecimal getAmount() {
		if (isValid()) {
			return item.getAmount();
		} else {
			return null;
		}
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
        if (!(obj instanceof AdjustmentOutItemRowItem)) {
            return false;
        }
        AdjustmentOutItemRowItem other = (AdjustmentOutItemRowItem)obj;		
		return new EqualsBuilder()
			.append(product, other.getProduct())
			.append(unit, other.getUnit())
			.isEquals();
	}

	public int getQuantityAsInt() {
		return Integer.parseInt(quantity);
	}
	
}
