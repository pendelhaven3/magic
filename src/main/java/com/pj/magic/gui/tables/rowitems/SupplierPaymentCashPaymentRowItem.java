package com.pj.magic.gui.tables.rowitems;

import java.math.BigDecimal;
import java.util.Date;

import com.pj.magic.model.SupplierPaymentCashPayment;
import com.pj.magic.model.User;

public class SupplierPaymentCashPaymentRowItem {

	private SupplierPaymentCashPayment cashPayment;
	private BigDecimal amount;
	private Date paidDate;
	private User paidBy;

	public SupplierPaymentCashPaymentRowItem(SupplierPaymentCashPayment cashPayment) {
		this.cashPayment = cashPayment;
		amount = cashPayment.getAmount();
		paidDate = cashPayment.getPaidDate();
		paidBy = cashPayment.getPaidBy();
	}
	
	
	public SupplierPaymentCashPayment getCashPayment() {
		return cashPayment;
	}


	public void setCheck(SupplierPaymentCashPayment cashPayment) {
		this.cashPayment = cashPayment;
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
		amount = cashPayment.getAmount();
		paidDate = cashPayment.getPaidDate();
		paidBy = cashPayment.getPaidBy();
	}

	public boolean isValid() {
		return amount != null && paidDate != null && paidBy != null;
	}

	public boolean isUpdating() {
		return cashPayment.getId() != null;
	}

}