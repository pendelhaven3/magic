package com.pj.magic.gui.tables.rowitems;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.ParseException;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

import com.pj.magic.Constants;
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
	private String actualQuantity;

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
		if (item.getActualQuantity() != null) {
			actualQuantity = item.getActualQuantity().toString();
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
				&& (NumberUtil.isAmount(cost) || StringUtils.isEmpty(cost)) 
				&& (StringUtils.isEmpty(actualQuantity) || StringUtils.isNumeric(actualQuantity));
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

	public String getActualQuantity() {
		return actualQuantity;
	}

	public void setActualQuantity(String actualQuantity) {
		this.actualQuantity = actualQuantity;
	}

	public BigDecimal getCostAsBigDecimal() {
		try {
			return new BigDecimal(new DecimalFormat(Constants.AMOUNT_FORMAT).parse(cost).doubleValue())
				.setScale(2, RoundingMode.HALF_UP);
		} catch (ParseException e) {
			throw new RuntimeException(e);
		}
	}

	public int getQuantityAsInt() {
		return Integer.parseInt(quantity);
	}
	
	public String getSuggestedOrder() {
		if (product != null && product.isMaxUnit(unit)) {
			return String.valueOf(product.getSuggestedOrder(unit));
		} else {
			return "";
		}
	}

	public BigDecimal getAmount() {
		return item.getAmount();
	}
	
}
