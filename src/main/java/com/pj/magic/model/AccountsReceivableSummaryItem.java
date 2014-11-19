package com.pj.magic.model;


public class AccountsReceivableSummaryItem {

	private Long id;
	private AccountsReceivableSummary parent;
	private SalesInvoice salesInvoice;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public AccountsReceivableSummary getParent() {
		return parent;
	}

	public void setParent(AccountsReceivableSummary parent) {
		this.parent = parent;
	}

	public SalesInvoice getSalesInvoice() {
		return salesInvoice;
	}

	public void setSalesInvoice(SalesInvoice salesInvoice) {
		this.salesInvoice = salesInvoice;
	}

}