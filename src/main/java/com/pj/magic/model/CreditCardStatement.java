package com.pj.magic.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class CreditCardStatement {

	private Long id;
	private Long statementNumber;
	private CreditCard creditCard;
	private Date statementDate;
	private List<CreditCardStatementItem> items = new ArrayList<>();

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

	public Long getStatementNumber() {
		return statementNumber;
	}

	public void setStatementNumber(Long statementNumber) {
		this.statementNumber = statementNumber;
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

}
