package com.pj.magic.model.search;

import java.util.Date;

import com.pj.magic.model.AdjustmentType;
import com.pj.magic.model.Customer;

public class PaymentAdjustmentSearchCriteria {

	private Long paymentAdjustmentNumber;
	private Customer customer;
	private AdjustmentType adjustmentType;
	private Boolean posted;
	private Date postDate;
	private Boolean paid;

	public Long getPaymentAdjustmentNumber() {
		return paymentAdjustmentNumber;
	}

	public void setPaymentAdjustmentNumber(Long paymentAdjustmentNumber) {
		this.paymentAdjustmentNumber = paymentAdjustmentNumber;
	}

	public Customer getCustomer() {
		return customer;
	}

	public void setCustomer(Customer customer) {
		this.customer = customer;
	}

	public AdjustmentType getAdjustmentType() {
		return adjustmentType;
	}

	public void setAdjustmentType(AdjustmentType adjustmentType) {
		this.adjustmentType = adjustmentType;
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

	public Boolean getPaid() {
		return paid;
	}

	public void setPaid(Boolean paid) {
		this.paid = paid;
	}

}