package com.pj.magic.model.search;

import java.util.Date;

import com.pj.magic.model.Customer;
import com.pj.magic.model.PaymentTerminal;
import com.pj.magic.model.util.TimePeriod;

public class BadStockReturnSearchCriteria {

	private Long badStockReturnNumber;
	private Boolean posted;
	private Boolean paid;
	private Customer customer;
	private Date postDate;
	private Date paidDate;
	private TimePeriod timePeriod;
	private PaymentTerminal paymentTerminal;

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

	public Date getPaidDate() {
		return paidDate;
	}

	public void setPaidDate(Date paidDate) {
		this.paidDate = paidDate;
	}

	public TimePeriod getTimePeriod() {
		return timePeriod;
	}

	public void setTimePeriod(TimePeriod timePeriod) {
		this.timePeriod = timePeriod;
	}

	public PaymentTerminal getPaymentTerminal() {
		return paymentTerminal;
	}

	public void setPaymentTerminal(PaymentTerminal paymentTerminal) {
		this.paymentTerminal = paymentTerminal;
	}
	
}