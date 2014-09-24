package com.pj.magic.model;

import java.math.BigDecimal;

public class UnitPrice {

	private String unit;
	private BigDecimal price;

	public UnitPrice(String unit, BigDecimal price) {
		this.unit = unit;
		this.price = price;
	}

	public UnitPrice(UnitPrice unitPrice) {
		unit = unitPrice.getUnit();
		price = unitPrice.getPrice();
	}

	public String getUnit() {
		return unit;
	}

	public void setUnit(String unit) {
		this.unit = unit;
	}

	public BigDecimal getPrice() {
		return price;
	}

	public void setPrice(BigDecimal price) {
		this.price = price;
	}

}
