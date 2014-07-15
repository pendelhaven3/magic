package com.pj.magic.model;

import java.math.BigDecimal;

public class UnitPrice {

	private String unit;
	private BigDecimal price;

	public UnitPrice(String unit, BigDecimal price) {
		super();
		this.unit = unit;
		this.price = price;
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
