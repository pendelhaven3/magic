package com.pj.magic.model;

import java.math.BigDecimal;
import java.util.Date;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

public class SupplierPaymentCreditCardPayment {

	private Long id;
	private SupplierPayment parent;
	private BigDecimal amount;
	private CreditCard creditCard;
	private Date transactionDate;
	private String approvalCode;

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

	public CreditCard getCreditCard() {
		return creditCard;
	}

	public void setCreditCard(CreditCard creditCard) {
		this.creditCard = creditCard;
	}

	public Date getTransactionDate() {
		return transactionDate;
	}

	public void setTransactionDate(Date transactionDate) {
		this.transactionDate = transactionDate;
	}

	public String getApprovalCode() {
		return approvalCode;
	}

	public void setApprovalCode(String approvalCode) {
		this.approvalCode = approvalCode;
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

}