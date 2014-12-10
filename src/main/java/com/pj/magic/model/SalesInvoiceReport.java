package com.pj.magic.model;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import com.pj.magic.Constants;

public class SalesInvoiceReport {

	private Date reportDate;
	private List<SalesInvoice> salesInvoices;

	public Date getReportDate() {
		return reportDate;
	}

	public void setReportDate(Date reportDate) {
		this.reportDate = reportDate;
	}

	public List<SalesInvoice> getSalesInvoices() {
		return salesInvoices;
	}

	public void setSalesInvoices(List<SalesInvoice> salesInvoices) {
		this.salesInvoices = salesInvoices;
	}

	public BigDecimal getTotalAmount() {
		BigDecimal total = Constants.ZERO;
		for (SalesInvoice salesInvoice : salesInvoices) {
			total = total.add(salesInvoice.getTotalAmount());
		}
		return total;
	}
	
	public BigDecimal getTotalDiscountedAmount() {
		BigDecimal total = Constants.ZERO;
		for (SalesInvoice salesInvoice : salesInvoices) {
			total = total.add(salesInvoice.getTotalDiscounts());
		}
		return total;
	}
	
	public BigDecimal getTotalNetAmount() {
		BigDecimal total = Constants.ZERO;
		for (SalesInvoice salesInvoice : salesInvoices) {
			total = total.add(salesInvoice.getTotalNetAmount());
		}
		return total;
	}
	
}