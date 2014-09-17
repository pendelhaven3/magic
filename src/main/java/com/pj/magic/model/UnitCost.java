package com.pj.magic.model;

import java.math.BigDecimal;

public class UnitCost {

	private String unit;
	private BigDecimal grossCost = BigDecimal.ZERO;
	private BigDecimal finalCost = BigDecimal.ZERO;

	public UnitCost(String unit) {
		this.unit = unit;
	}
	
	public UnitCost(String unit, BigDecimal grossCost, BigDecimal finalCost) {
		this.unit = unit;
		this.grossCost = grossCost;
		this.finalCost = finalCost;
	}

	public String getUnit() {
		return unit;
	}

	public void setUnit(String unit) {
		this.unit = unit;
	}

	public BigDecimal getGrossCost() {
		return grossCost;
	}

	public void setGrossCost(BigDecimal grossCost) {
		this.grossCost = grossCost;
	}

	public BigDecimal getFinalCost() {
		return finalCost;
	}

	public void setFinalCost(BigDecimal finalCost) {
		this.finalCost = finalCost;
	}

}
