package com.pj.magic.model;

public class PaymentSalesInvoice {

	private Long id;
	private Payment parent;
	private SalesInvoice salesInvoice;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Payment getParent() {
		return parent;
	}

	public void setParent(Payment parent) {
		this.parent = parent;
	}

	public SalesInvoice getSalesInvoice() {
		return salesInvoice;
	}

	public void setSalesInvoice(SalesInvoice salesInvoice) {
		this.salesInvoice = salesInvoice;
	}

}