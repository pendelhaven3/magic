package com.pj.magic.model.search;

import java.util.Date;

import com.pj.magic.model.Customer;
import com.pj.magic.model.PaymentTerminal;
import com.pj.magic.model.util.TimePeriod;

public class PaymentCheckPaymentSearchCriteria {

	private Date paymentDate;
	private Customer customer;
	private Boolean posted;
	private PaymentTerminal paymentTerminal;
	private TimePeriod timePeriod;

	public Date getPaymentDate() {
		return paymentDate;
	}

	public void setPaymentDate(Date paymentDate) {
		this.paymentDate = paymentDate;
	}

	public Customer getCustomer() {
		return customer;
	}

	public void setCustomer(Customer customer) {
		this.customer = customer;
	}

	public Boolean getPosted() {
		return posted;
	}

	public void setPosted(Boolean posted) {
		this.posted = posted;
	}

	public PaymentTerminal getPaymentTerminal() {
		return paymentTerminal;
	}

	public void setPaymentTerminal(PaymentTerminal paymentTerminal) {
		this.paymentTerminal = paymentTerminal;
	}

	public TimePeriod getTimePeriod() {
		return timePeriod;
	}

	public void setTimePeriod(TimePeriod timePeriod) {
		this.timePeriod = timePeriod;
	}

}