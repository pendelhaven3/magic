package com.pj.magic.gui.tables.models;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.pj.magic.Constants;
import com.pj.magic.model.AccountsReceivableSummaryItem;
import com.pj.magic.model.Customer;
import com.pj.magic.model.SalesInvoice;

public class AccountsReceivableSummary {

	private Long id;
	private Long accountReceivableSummaryNumber;
	private Customer customer;
	private List<AccountsReceivableSummaryItem> items = new ArrayList<>();

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Customer getCustomer() {
		return customer;
	}

	public Long getAccountReceivableSummaryNumber() {
		return accountReceivableSummaryNumber;
	}

	public void setAccountReceivableSummaryNumber(
			Long accountReceivableSummaryNumber) {
		this.accountReceivableSummaryNumber = accountReceivableSummaryNumber;
	}

	public void setCustomer(Customer customer) {
		this.customer = customer;
	}

	public List<AccountsReceivableSummaryItem> getItems() {
		return items;
	}

	public void setItems(List<AccountsReceivableSummaryItem> items) {
		this.items = items;
	}

	public void add(SalesInvoice salesInvoice) {
		AccountsReceivableSummaryItem item = new AccountsReceivableSummaryItem();
		item.setParent(this);
		item.setSalesInvoice(salesInvoice);
		items.add(item);
	}

	public BigDecimal getTotalAmount() {
		BigDecimal total = Constants.ZERO;
		for (AccountsReceivableSummaryItem item : items) {
			total = total.add(item.getSalesInvoice().getTotalNetAmount());
		}
		return total;
	}
	
}
