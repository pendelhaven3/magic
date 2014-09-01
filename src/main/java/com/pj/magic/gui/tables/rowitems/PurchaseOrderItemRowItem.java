package com.pj.magic.gui.tables.rowitems;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

import com.pj.magic.model.Product;
import com.pj.magic.model.PurchaseOrderItem;
import com.pj.magic.util.NumberUtil;

/*
 * Wrapper class to separate table gui concerns of inputting purchase order items
 * from the business logic of purchase order item model.
 */
public class PurchaseOrderItemRowItem {

	private PurchaseOrderItem item;
	private String productCode;
	private String unit;
	private String quantity;
	private String cost;
	private Product product;

	public PurchaseOrderItemRowItem(PurchaseOrderItem item) {
		this.item = item;
		if (item.getProduct() != null) {
			productCode = item.getProduct().getCode();
			product = item.getProduct();
		}
		unit = item.getUnit();
		if (item.getQuantity() != null) {
			quantity = item.getQuantity().toString();
		}
		if (item.getCost() != null) {
			cost = item.getCost().toString();
		}
	}
	
	public PurchaseOrderItem getItem() {
		return item;
	}

	public void setItem(PurchaseOrderItem item) {
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

	public String getCost() {
		return cost;
	}

	public void setCost(String cost) {
		this.cost = cost;
	}

	public Product getProduct() {
		return product;
	}

	public void setProduct(Product product) {
		this.product = product;
	}

	public boolean isValid() {
		return product != null && product.hasUnit(unit) && StringUtils.isNumeric(quantity)
				&& NumberUtil.isAmount(cost);
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
        if (!(obj instanceof PurchaseOrderItemRowItem)) {
            return false;
        }
        PurchaseOrderItemRowItem other = (PurchaseOrderItemRowItem)obj;		
		return new EqualsBuilder()
			.append(product, other.getProduct())
			.append(unit, other.getUnit())
			.isEquals();
	}
	
}
