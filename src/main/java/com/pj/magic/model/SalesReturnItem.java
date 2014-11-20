package com.pj.magic.model;

public class SalesReturnItem {

	private Long id;
	private SalesReturn parent;
	private SalesInvoiceItem item;
	private Integer quantity;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public SalesReturn getParent() {
		return parent;
	}

	public void setParent(SalesReturn parent) {
		this.parent = parent;
	}

	public SalesInvoiceItem getItem() {
		return item;
	}

	public void setItem(SalesInvoiceItem item) {
		this.item = item;
	}

	public Integer getQuantity() {
		return quantity;
	}

	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}

}
