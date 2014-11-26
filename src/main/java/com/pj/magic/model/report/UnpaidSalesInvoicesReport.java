package com.pj.magic.model.report;

import java.math.BigDecimal;
import java.util.List;

import com.pj.magic.Constants;
import com.pj.magic.model.SalesInvoice;

public class UnpaidSalesInvoicesReport {

	private List<SalesInvoice> salesInvoices;

	public List<SalesInvoice> getSalesInvoices() {
		return salesInvoices;
	}

	public void setSalesInvoices(List<SalesInvoice> salesInvoices) {
		this.salesInvoices = salesInvoices;
	}
	
	public BigDecimal getTotalAmount() {
		BigDecimal total = Constants.ZERO;
		for (SalesInvoice salesInvoice : salesInvoices) {
			total = total.add(salesInvoice.getTotalNetAmount());
		}
		return total;
	}
	
}