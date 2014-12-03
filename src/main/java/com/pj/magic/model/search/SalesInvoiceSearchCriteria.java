package com.pj.magic.model.search;

import java.util.Date;

import com.pj.magic.model.Customer;

public class SalesInvoiceSearchCriteria {

	private Boolean marked;
	private Boolean cancelled;
	private Long salesInvoiceNumber;
	private Customer customer;
	private Date transactionDate;
	private Date transactionDateFrom;
	private Date transactionDateTo;
	private Boolean paid;

	public Boolean isMarked() {
		return marked;
	}

	public void setMarked(Boolean marked) {
		this.marked = marked;
	}

	public Boolean isCancelled() {
		return cancelled;
	}

	public void setCancelled(Boolean cancelled) {
		this.cancelled = cancelled;
	}

	public Long getSalesInvoiceNumber() {
		return salesInvoiceNumber;
	}

	public void setSalesInvoiceNumber(Long salesInvoiceNumber) {
		this.salesInvoiceNumber = salesInvoiceNumber;
	}

	public Customer getCustomer() {
		return customer;
	}

	public void setCustomer(Customer customer) {
		this.customer = customer;
	}

	public Date getTransactionDate() {
		return transactionDate;
	}

	public void setTransactionDate(Date transactionDate) {
		this.transactionDate = transactionDate;
	}

	public Boolean getMarked() {
		return marked;
	}

	public Boolean getCancelled() {
		return cancelled;
	}

	public Date getTransactionDateFrom() {
		return transactionDateFrom;
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

	public Boolean getPaid() {
		return paid;
	}

	public void setPaid(Boolean paid) {
		this.paid = paid;
	}

}