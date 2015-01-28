package com.pj.magic.gui.tables.rowitems;

import java.math.BigDecimal;
import java.util.Date;

import org.apache.commons.lang.StringUtils;

import com.pj.magic.model.SupplierPaymentCreditCardPayment;
import com.pj.magic.model.User;

public class SupplierPaymentCreditCardPaymentRowItem {

	private SupplierPaymentCreditCardPayment creditCardPayment;
	private String bank;
	private BigDecimal amount;
	private Date paidDate;
	private User paidBy;

	public SupplierPaymentCreditCardPaymentRowItem(SupplierPaymentCreditCardPayment creditCardPayment) {
		this.creditCardPayment = creditCardPayment;
		amount = creditCardPayment.getAmount();
		bank = creditCardPayment.getBank();
		paidDate = creditCardPayment.getPaidDate();
		paidBy = creditCardPayment.getPaidBy();
	}
	
	
	public SupplierPaymentCreditCardPayment getCreditCardPayment() {
		return creditCardPayment;
	}


	public void setCreditCardPayment(SupplierPaymentCreditCardPayment creditCardPayment) {
		this.creditCardPayment = creditCardPayment;
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


	public User getPaidBy() {
		return paidBy;
	}


	public void setPaidBy(User paidBy) {
		this.paidBy = paidBy;
	}


	public void reset() {
		amount = creditCardPayment.getAmount();
		bank = creditCardPayment.getBank();
		paidDate = creditCardPayment.getPaidDate();
		paidBy = creditCardPayment.getPaidBy();
	}

	public boolean isValid() {
		return amount != null && !StringUtils.isEmpty(bank) && paidDate != null && paidBy != null;
	}

	public boolean isUpdating() {
		return creditCardPayment.getId() != null;
	}

	public String getBank() {
		return bank;
	}

	public void setBank(String bank) {
		this.bank = bank;
	}

}