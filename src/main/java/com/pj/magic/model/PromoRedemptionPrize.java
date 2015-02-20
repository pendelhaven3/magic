package com.pj.magic.model;

public class PromoRedemptionPrize {

	private Long id;
	private PromoRedemption parent;
	private Product product;
	private String unit;
	private int quantity;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public PromoRedemption getParent() {
		return parent;
	}

	public void setParent(PromoRedemption parent) {
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