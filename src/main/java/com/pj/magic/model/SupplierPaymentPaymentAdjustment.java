package com.pj.magic.model;

import java.math.BigDecimal;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

public class SupplierPaymentPaymentAdjustment {

	private Long id;
	private SupplierPayment parent;
	private PurchasePaymentAdjustmentType adjustmentType;
	private BigDecimal amount;
	private String referenceNumber;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public SupplierPayment getParent() {
		return parent;
	}

	public void setParent(SupplierPayment parent) {
		this.parent = parent;
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

	public String getReferenceNumber() {
		return referenceNumber;
	}

	public void setReferenceNumber(String referenceNumber) {
		this.referenceNumber = referenceNumber;
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
        if (!(obj instanceof SupplierPaymentPaymentAdjustment)) {
            return false;
        }
        SupplierPaymentPaymentAdjustment other = (SupplierPaymentPaymentAdjustment)obj;		
		return new EqualsBuilder()
			.append(id, other.getId())
			.isEquals();
	}
	
}