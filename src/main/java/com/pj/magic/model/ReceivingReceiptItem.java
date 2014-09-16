package com.pj.magic.model;

import java.math.BigDecimal;

public class ReceivingReceiptItem {

	private Long id;
	private ReceivingReceipt parent;
	private Product product;
	private String unit;
	private Integer quantity;
	private BigDecimal cost;
	private BigDecimal discount1;
	private BigDecimal discount2;
	private BigDecimal discount3;
	private BigDecimal flatRate;

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

	public BigDecimal getDiscount1() {
		return discount1;
	}

	public void setDiscount1(BigDecimal discount1) {
		this.discount1 = discount1;
	}

	public BigDecimal getDiscount2() {
		return discount2;
	}

	public void setDiscount2(BigDecimal discount2) {
		this.discount2 = discount2;
	}

	public BigDecimal getDiscount3() {
		return discount3;
	}

	public void setDiscount3(BigDecimal discount3) {
		this.discount3 = discount3;
	}

	public BigDecimal getFlatRate() {
		return flatRate;
	}

	public void setFlatRate(BigDecimal flatRate) {
		this.flatRate = flatRate;
	}
	
	public BigDecimal getDiscountedAmount() {
		return null;
	}
	
	public BigDecimal getNetAmount() {
		return null;
	}

}
