package com.pj.magic.model;

import java.math.BigDecimal;
import java.util.Date;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

public class SupplierPaymentBankTransfer {

	private Long id;
	private SupplierPayment parent;
	private String bank;
	private BigDecimal amount;
	private String referenceNumber;
	private Date transferDate;

	@Override
	public int hashCode() {
		return new HashCodeBuilder()
			.append(id)
			.toHashCode();
	}

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

	public String getBank() {
		return bank;
	}

	public void setBank(String bank) {
		this.bank = bank;
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

	public Date getTransferDate() {
		return transferDate;
	}

	public void setTransferDate(Date transferDate) {
		this.transferDate = transferDate;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
        if (!(obj instanceof SupplierPaymentBankTransfer)) {
            return false;
        }
        SupplierPaymentBankTransfer other = (SupplierPaymentBankTransfer)obj;		
		return new EqualsBuilder()
			.append(id, other.getId())
			.isEquals();
	}
	
}