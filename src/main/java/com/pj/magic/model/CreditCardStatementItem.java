package com.pj.magic.model;

import java.util.Date;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

public class CreditCardStatementItem {

	private Long id;
	private CreditCardStatement parent;
	private PurchasePaymentCreditCardPayment creditCardPayment;
	private boolean paid;
	private Date paidDate;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public CreditCardStatement getParent() {
		return parent;
	}

	public void setParent(CreditCardStatement parent) {
		this.parent = parent;
	}

	public PurchasePaymentCreditCardPayment getCreditCardPayment() {
		return creditCardPayment;
	}

	public void setCreditCardPayment(PurchasePaymentCreditCardPayment creditCardPayment) {
		this.creditCardPayment = creditCardPayment;
	}

	public boolean isPaid() {
		return paid;
	}

	public void setPaid(boolean paid) {
		this.paid = paid;
	}

	public Date getPaidDate() {
		return paidDate;
	}

	public void setPaidDate(Date paidDate) {
		this.paidDate = paidDate;
	}

	public boolean isNew() {
		return id == null;
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder()
				.append(id)
				.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		
		CreditCardStatementItem other = (CreditCardStatementItem) obj;
		return new EqualsBuilder()
				.append(id, other.getId())
				.isEquals();
	}

}
