package com.pj.magic.model.search;

import java.util.Date;

import com.pj.magic.model.Customer;

public class BadStockReturnSearchCriteria {

	private Long badStockReturnNumber;
	private Boolean posted;
	private Boolean paid;
	private Customer customer;
	private Date postDate;

	public Boolean getPosted() {
		return posted;
	}

	public void setPosted(Boolean posted) {
		this.posted = posted;
	}

	public Boolean getPaid() {
		return paid;
	}

	public void setPaid(Boolean paid) {
		this.paid = paid;
	}

	public Long getBadStockReturnNumber() {
		return badStockReturnNumber;
	}

	public void setBadStockReturnNumber(Long badStockReturnNumber) {
		this.badStockReturnNumber = badStockReturnNumber;
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
	
}