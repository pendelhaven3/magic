package com.pj.magic.model.util;

import java.math.BigDecimal;

import com.pj.magic.model.InventoryCheckSummaryItem;
import com.pj.magic.model.Product;

public class InventoryCheckSummaryPrintItem {

	private Product product;
	private String unit;
	private int quantity;
	private BigDecimal cost;
	private BigDecimal quantityValue;

	public InventoryCheckSummaryPrintItem(InventoryCheckSummaryItem item, boolean beginningInventory) {
		product = item.getProduct();
		unit = item.getUnit();
		cost = item.getCost();
		quantity = (beginningInventory) ? item.getBeginningInventory() : item.getQuantity();
		quantityValue = (beginningInventory) ? item.getBeginningValue() : item.getActualValue();
	}

	public Product getProduct() {
		return product;
	}

	public void setProduct(Product product) {
		this.product = product;
	}

	public String getUnit() {
		return unit;
	}

	public void setUnit(String unit) {
		this.unit = unit;
	}

	public int getQuantity() {
		return quantity;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}

	public BigDecimal getCost() {
		return cost;
	}

	public void setCost(BigDecimal cost) {
		this.cost = cost;
	}

	public BigDecimal getQuantityValue() {
		return quantityValue;
	}

	public void setQuantityValue(BigDecimal quantityValue) {
		this.quantityValue = quantityValue;
	}

}