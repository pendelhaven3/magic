package com.pj.magic.model.search;

import java.util.Date;

import com.pj.magic.model.Supplier;

public class SupplierPaymentSearchCriteria {

	private Boolean posted;
	private Supplier supplier;
	private Date postDate;
	private Long paymentNumber;
	private Boolean cancelled;

	public Boolean getPosted() {
		return posted;
	}

	public void setPosted(Boolean posted) {
		this.posted = posted;
	}

	public Supplier getSupplier() {
		return supplier;
	}

	public void setSupplier(Supplier supplier) {
		this.supplier = supplier;
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

	public Boolean getCancelled() {
		return cancelled;
	}

	public void setCancelled(Boolean cancelled) {
		this.cancelled = cancelled;
	}
	
}