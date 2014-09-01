package com.pj.magic.model;

import java.math.BigDecimal;

import org.springframework.util.StringUtils;

public class PurchaseOrderItem {

	private Long id;
	private PurchaseOrder parent;
	private Product product;
	private String unit;
	private Integer quantity;
	private BigDecimal cost;
	private Integer actualQuantity;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public PurchaseOrder getParent() {
		return parent;
	}

	public void setParent(PurchaseOrder parent) {
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

	public Integer getActualQuantity() {
		return actualQuantity;
	}

	public void setActualQuantity(Integer actualQuantity) {
		this.actualQuantity = actualQuantity;
	}

	public boolean isFilledUp() {
		return product != null && !StringUtils.isEmpty(unit) && quantity != null && cost != null;
	}

	public boolean isValid() {
		return (product != null && product.isValid())
				&& product.hasUnit(unit)
				&& (quantity != null && cost != null);
	}

}
