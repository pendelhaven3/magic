package com.pj.magic.model;

public class InventoryCheckSummaryItem {

	private Product product;
	private String unit;
	private int quantity;

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

	public int getQuantityDifference() {
		return quantity - product.getUnitQuantity(unit);
	}

}