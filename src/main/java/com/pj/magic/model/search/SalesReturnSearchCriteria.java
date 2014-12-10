package com.pj.magic.model.search;

import java.util.Date;

import com.pj.magic.model.Customer;

public class SalesReturnSearchCriteria {

	private Boolean posted;
	private Customer customer;
	private Date transactionDateFrom;
	private Date transactionDateTo;

	public Boolean getPosted() {
		return posted;
	}

	public void setPosted(Boolean posted) {
		this.posted = posted;
	}

	public Date getTransactionDateFrom() {
		return transactionDateFrom;
	}

	public Customer getCustomer() {
		return customer;
	}

	public void setCustomer(Customer customer) {
		this.customer = customer;
	}

	public void setTransactionDateFrom(Date transactionDateFrom) {
		this.transactionDateFrom = transactionDateFrom;
	}

	public Date getTransactionDateTo() {
		return transactionDateTo;
	}

	public void setTransactionDateTo(Date transactionDateTo) {
		this.transactionDateTo = transactionDateTo;
	}

}