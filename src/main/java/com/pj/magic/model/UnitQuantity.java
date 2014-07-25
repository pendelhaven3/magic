package com.pj.magic.model;

public class UnitQuantity {

	private String unit;
	private Integer quantity;
	
	public UnitQuantity(String unit, Integer quantity) {
		this.unit = unit;
		this.quantity = quantity;
	}

	public String getUnit() {
		return unit;
	}

	public void setUnit(String unit) {
		this.unit = unit;
	}

	public Integer getQuantity() {
		return quantity;
	}

	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}

}
