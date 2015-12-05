package com.pj.magic.model.report;

import com.pj.magic.model.Product;

public class StockTakeoffReportItem {

	private Product product;
	private String unit;
	private int quantityDifference;

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

	public int getQuantityDifference() {
		return quantityDifference;
	}

	public void setQuantityDifference(int quantityDifference) {
		this.quantityDifference = quantityDifference;
	}

}
