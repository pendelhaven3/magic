package com.pj.magic.model.search;

import java.util.Date;

import com.pj.magic.model.Customer;

public class PaymentSearchCriteria {

	private Boolean posted;
	private Customer customer;
	private Date postDate;
	private Long paymentNumber;

	public Boolean getPosted() {
		return posted;
	}

	public void setPosted(Boolean posted) {
		this.posted = posted;
	}

	public Customer getCustomer() {
		return customer;
	}

	public void setCustomer(Customer customer) {
		this.customer = customer;
	}

	public Date getPostDate() {
		return postDate;
	}

	public void setPostDate(Date postDate) {
		this.postDate = postDate;
	}

	public Long getPaymentNumber() {
		return paymentNumber;
	}

	public void setPaymentNumber(Long paymentNumber) {
		this.paymentNumber = paymentNumber;
	}
	
}