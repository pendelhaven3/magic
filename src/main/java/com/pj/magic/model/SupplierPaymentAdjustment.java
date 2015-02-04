package com.pj.magic.model;

import java.math.BigDecimal;
import java.util.Date;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

public class SupplierPaymentAdjustment {

	private Long id;
	private Long supplierPaymentAdjustmentNumber;
	private Supplier supplier;
	private PurchasePaymentAdjustmentType adjustmentType;
	private BigDecimal amount;
	private boolean posted;
	private Date postDate;
	private User postedBy;
	private String remarks;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getSupplierPaymentAdjustmentNumber() {
		return supplierPaymentAdjustmentNumber;
	}

	public void setSupplierPaymentAdjustmentNumber(Long supplierPaymentAdjustmentNumber) {
		this.supplierPaymentAdjustmentNumber = supplierPaymentAdjustmentNumber;
	}

	public PurchasePaymentAdjustmentType getAdjustmentType() {
		return adjustmentType;
	}

	public void setAdjustmentType(PurchasePaymentAdjustmentType adjustmentType) {
		this.adjustmentType = adjustmentType;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public boolean isPosted() {
		return posted;
	}

	public void setPosted(boolean posted) {
		this.posted = posted;
	}

	public Date getPostDate() {
		return postDate;
	}

	public void setPostDate(Date postDate) {
		this.postDate = postDate;
	}

	public User getPostedBy() {
		return postedBy;
	}

	public void setPostedBy(User postedBy) {
		this.postedBy = postedBy;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public String getStatus() {
		return (posted) ? "Posted" : "New";
	}

	public Supplier getSupplier() {
		return supplier;
	}

	public void setSupplier(Supplier supplier) {
		this.supplier = supplier;
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder()
			.append(id)
			.toHashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
        if (!(obj instanceof SupplierPaymentAdjustment)) {
            return false;
        }
        SupplierPaymentAdjustment other = (SupplierPaymentAdjustment)obj;		
		return new EqualsBuilder()
			.append(id, other.getId())
			.isEquals();
	}
	
}