package com.pj.magic.model.search;

import java.util.Date;

import com.pj.magic.model.Customer;
import com.pj.magic.model.Payment;
import com.pj.magic.model.SalesInvoice;

public class SalesReturnSearchCriteria {

	private Long salesReturnNumber;
	private Boolean posted;
	private Customer customer;
	private Date postDate;
	private Date postDateFrom;
	private Date postDateTo;
	private SalesInvoice salesInvoice;
	private Payment payment;

	public Long getSalesReturnNumber() {
		return salesReturnNumber;
	}

	public void setSalesReturnNumber(Long salesReturnNumber) {
		this.salesReturnNumber = salesReturnNumber;
	}

	public Boolean getPosted() {
		return posted;
	}

	public void setPosted(Boolean posted) {
		this.posted = posted;
	}

	public Date getPostDate() {
		return postDate;
	}

	public void setPostDate(Date postDate) {
		this.postDate = postDate;
	}

	public Customer getCustomer() {
		return customer;
	}

	public void setCustomer(Customer customer) {
		this.customer = customer;
	}

	public Date getPostDateFrom() {
		return postDateFrom;
	}

	public void setPostDateFrom(Date postDateFrom) {
		this.postDateFrom = postDateFrom;
	}

	public Date getPostDateTo() {
		return postDateTo;
	}

	public void setPostDateTo(Date postDateTo) {
		this.postDateTo = postDateTo;
	}

	public SalesInvoice getSalesInvoice() {
		return salesInvoice;
	}

	public void setSalesInvoice(SalesInvoice salesInvoice) {
		this.salesInvoice = salesInvoice;
	}

	public Payment getPayment() {
		return payment;
	}

	public void setPayment(Payment payment) {
		this.payment = payment;
	}

}