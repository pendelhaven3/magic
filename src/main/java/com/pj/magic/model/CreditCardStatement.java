package com.pj.magic.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.pj.magic.Constants;

public class CreditCardStatement {

	private Long id;
	private CreditCard creditCard;
	private Date statementDate;
	private List<CreditCardStatementItem> items = new ArrayList<>();
	private boolean posted;

	public CreditCardStatement() {
		// default constructor
	}
	
	public CreditCardStatement(long id) {
		this.id = id;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public CreditCard getCreditCard() {
		return creditCard;
	}

	public void setCreditCard(CreditCard creditCard) {
		this.creditCard = creditCard;
	}

	public Date getStatementDate() {
		return statementDate;
	}

	public void setStatementDate(Date statementDate) {
		this.statementDate = statementDate;
	}

	public List<CreditCardStatementItem> getItems() {
		return items;
	}

	public void setItems(List<CreditCardStatementItem> items) {
		this.items = items;
	}

	public boolean isNew() {
		return id == null;
	}

	public BigDecimal getTotalAmount() {
		BigDecimal total = Constants.ZERO;
		for (CreditCardStatementItem item : items) {
			total = total.add(item.getCreditCardPayment().getAmount());
		}
		return total;
	}

	public boolean isPosted() {
		return posted;
	}

	public void setPosted(boolean posted) {
		this.posted = posted;
	}

	public String getStatus() {
		if (posted) {
			if (isFullyPaid()) {
				return "Fully Paid";
			} else if (isUnpaid()) {
				return "Posted/Unpaid";
			} else {
				return "Partially Paid";
			}
		} else {
			return "New";
		}
	}

	private boolean isUnpaid() {
		for (CreditCardStatementItem item : items) {
			if (item.isPaid()) {
				return false;
			}
		}
		return true;
	}

	private boolean isFullyPaid() {
		for (CreditCardStatementItem item : items) {
			if (!item.isPaid()) {
				return false;
			}
		}
		return true;
	}

}
