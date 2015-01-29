package com.pj.magic.model;

import java.math.BigDecimal;
import java.util.Date;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

public class SupplierPaymentCreditCardPayment {

	private Long id;
	private SupplierPayment parent;
	private BigDecimal amount;
	private String bank;
	private Date paidDate;
	private User paidBy;

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

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public Date getPaidDate() {
		return paidDate;
	}

	public void setPaidDate(Date paidDate) {
		this.paidDate = paidDate;
	}

	public User getReceivedBy() {
		return paidBy;
	}

	public void setPaidBy(User paidBy) {
		this.paidBy = paidBy;
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
        if (!(obj instanceof SupplierPaymentCreditCardPayment)) {
            return false;
        }
        SupplierPaymentCreditCardPayment other = (SupplierPaymentCreditCardPayment)obj;		
		return new EqualsBuilder()
			.append(id, other.getId())
			.isEquals();
	}

	public String getBank() {
		return bank;
	}

	public void setBank(String bank) {
		this.bank = bank;
	}

	public User getPaidBy() {
		return paidBy;
	}
	
}