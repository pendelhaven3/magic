package com.pj.magic.model;

import java.math.BigDecimal;

public class ReceivingReceiptItem {

	private Long id;
	private ReceivingReceipt parent;
	private Product product;
	private String unit;
	private Integer quantity;
	private BigDecimal cost;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public ReceivingReceipt getParent() {
		return parent;
	}

	public void setParent(ReceivingReceipt parent) {
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

	public Integer getQuantity() {
		return quantity;
	}

	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}

	public BigDecimal getCost() {
		return cost;
	}

	public void setCost(BigDecimal cost) {
		this.cost = cost;
	}

	public BigDecimal getAmount() {
		return cost.multiply(new BigDecimal(quantity.intValue()));
	}

}
