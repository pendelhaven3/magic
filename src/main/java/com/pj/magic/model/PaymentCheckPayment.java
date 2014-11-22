package com.pj.magic.model;

import java.math.BigDecimal;

public class PaymentCheckPayment {

	private Long id;
	private Payment parent;
	private String bank;
	private String checkNumber;
	private BigDecimal amount;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Payment getParent() {
		return parent;
	}

	public void setParent(Payment parent) {
		this.parent = parent;
	}

	public String getBank() {
		return bank;
	}

	public void setBank(String bank) {
		this.bank = bank;
	}

	public String getCheckNumber() {
		return checkNumber;
	}

	public void setCheckNumber(String checkNumber) {
		this.checkNumber = checkNumber;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

}