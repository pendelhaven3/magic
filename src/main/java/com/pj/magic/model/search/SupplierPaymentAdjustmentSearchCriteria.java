package com.pj.magic.model.search;

import java.util.Date;

import com.pj.magic.model.AdjustmentType;
import com.pj.magic.model.Supplier;

public class SupplierPaymentAdjustmentSearchCriteria {

	private Long supplierPaymentAdjustmentNumber;
	private Supplier supplier;
	private AdjustmentType adjustmentType;
	private Boolean posted;
	private Date postDate;
	private Date postDateFrom;
	private Date postDateTo;

	public Long getPaymentAdjustmentNumber() {
		return supplierPaymentAdjustmentNumber;
	}

	public void setPaymentAdjustmentNumber(Long paymentAdjustmentNumber) {
		this.supplierPaymentAdjustmentNumber = paymentAdjustmentNumber;
	}

	public Supplier getSupplier() {
		return supplier;
	}

	public void setSupplier(Supplier supplier) {
		this.supplier = supplier;
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

}