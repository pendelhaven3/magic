package com.pj.magic.gui.tables.rowitems;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.ParseException;

import org.apache.commons.lang.StringUtils;

import com.pj.magic.Constants;
import com.pj.magic.model.Product;
import com.pj.magic.model.ReceivingReceiptItem;
import com.pj.magic.util.NumberUtil;

/*
 * Wrapper class to separate table gui concerns of inputting purchase order items
 * from the business logic of purchase order item model.
 */
public class ReceivingReceiptItemRowItem {

	private ReceivingReceiptItem item;
	private String productCode;
	private String unit;
	private String quantity;
	private String cost;
	private Product product;

	public ReceivingReceiptItemRowItem(ReceivingReceiptItem item) {
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
	
	public ReceivingReceiptItem getItem() {
		return item;
	}

	public void setItem(ReceivingReceiptItem item) {
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
	
}
