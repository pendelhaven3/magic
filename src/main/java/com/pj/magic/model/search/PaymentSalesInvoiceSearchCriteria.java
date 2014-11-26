package com.pj.magic.model.search;

import java.util.Date;

import com.pj.magic.model.Customer;

public class PaymentSalesInvoiceSearchCriteria {

	private Date paymentDate;
	private Customer customer;
	private Boolean paid;

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

	public Boolean getPaid() {
		return paid;
	}

	public void setPaid(Boolean paid) {
		this.paid = paid;
	}

}
