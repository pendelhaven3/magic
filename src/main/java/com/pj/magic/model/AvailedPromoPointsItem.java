package com.pj.magic.model;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 
 * @author PJ Miranda
 *
 */
public class AvailedPromoPointsItem {

	private Long salesInvoiceNumber;
	private Date transactionDate;
	private BigDecimal netAmount;
	private int points;
	
	public Long getSalesInvoiceNumber() {
		return salesInvoiceNumber;
	}

	public void setSalesInvoiceNumber(Long salesInvoiceNumber) {
		this.salesInvoiceNumber = salesInvoiceNumber;
	}

	public Date getTransactionDate() {
		return transactionDate;
	}

	public void setTransactionDate(Date transactionDate) {
		this.transactionDate = transactionDate;
	}

	public BigDecimal getNetAmount() {
		return netAmount;
	}

	public void setNetAmount(BigDecimal netAmount) {
		this.netAmount = netAmount;
	}

	public int getPoints() {
		return points;
	}

	public void setPoints(int points) {
		this.points = points;
	}

}