package com.pj.magic.model;

public class UnitQuantity {

	private String unit;
	private int quantity;
	
	public UnitQuantity(String unit, int quantity) {
		this.unit = unit;
		this.quantity = quantity;
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
	
}
