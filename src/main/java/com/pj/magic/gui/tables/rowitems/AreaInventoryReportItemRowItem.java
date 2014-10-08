package com.pj.magic.gui.tables.rowitems;

import java.math.BigDecimal;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

import com.pj.magic.model.AreaInventoryReportItem;
import com.pj.magic.model.Product;

/*
 * Wrapper class to separate table gui concerns of inputting adjustment in items
 * from the business logic of adjustment in item model.
 */
public class AreaInventoryReportItemRowItem {

	private AreaInventoryReportItem item;
	private String productCode;
	private String unit;
	private String quantity;
	private Product product;

	public AreaInventoryReportItemRowItem(AreaInventoryReportItem item) {
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
	
	public AreaInventoryReportItem getItem() {
		return item;
	}

	public void setItem(AreaInventoryReportItem item) {
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
        if (!(obj instanceof AreaInventoryReportItemRowItem)) {
            return false;
        }
        AreaInventoryReportItemRowItem other = (AreaInventoryReportItemRowItem)obj;		
		return new EqualsBuilder()
			.append(product, other.getProduct())
			.append(unit, other.getUnit())
			.isEquals();
	}

	public int getQuantityAsInt() {
		return Integer.parseInt(quantity);
	}
	
}
