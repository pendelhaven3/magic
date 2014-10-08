package com.pj.magic.model;

public class AreaInventoryReportItem {

	private Long id;
	private AreaInventoryReport parent;
	private Product product;
	private String unit;
	private int quantity;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public AreaInventoryReport getParent() {
		return parent;
	}

	public void setParent(AreaInventoryReport parent) {
		this.parent = parent;
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

}
