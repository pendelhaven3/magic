package com.pj.magic.model;

public class CreditCardStatementItem {

	private Long id;
	private CreditCardStatement parent;
	private PurchasePaymentCreditCardPayment creditCardPayment;

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

}
