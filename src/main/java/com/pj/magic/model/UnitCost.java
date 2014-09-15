package com.pj.magic.model;

import java.math.BigDecimal;

// TODO: Can be merged with UnitPrice?
public class UnitCost {

	private String unit;
	private BigDecimal cost;

	public UnitCost(String unit, BigDecimal cost) {
		super();
		this.unit = unit;
		this.cost = cost;
	}

	public String getUnit() {
		return unit;
	}

	public void setUnit(String unit) {
		this.unit = unit;
	}

	public BigDecimal getCost() {
		return cost;
	}

	public void setCost(BigDecimal cost) {
		this.cost = cost;
	}

}
