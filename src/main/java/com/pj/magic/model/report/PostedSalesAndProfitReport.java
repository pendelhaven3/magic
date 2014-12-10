package com.pj.magic.model.report;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import com.pj.magic.Constants;
import com.pj.magic.model.Customer;
import com.pj.magic.model.SalesInvoice;
import com.pj.magic.util.FormatterUtil;

public class PostedSalesAndProfitReport {

	private Customer customer;
	private Date transactionDateFrom;
	private Date transactionDateTo;
	private List<SalesInvoice> salesInvoices;

	public Customer getCustomer() {
		return customer;
	}

	public void setCustomer(Customer customer) {
		this.customer = customer;
	}

	public Date getTransactionDateFrom() {
		return transactionDateFrom;
	}

	public void setTransactionDateFrom(Date transactionDateFrom) {
		this.transactionDateFrom = transactionDateFrom;
	}

	public Date getTransactionDateTo() {
		return transactionDateTo;
	}

	public void setTransactionDateTo(Date transactionDateTo) {
		this.transactionDateTo = transactionDateTo;
	}

	public List<SalesInvoice> getSalesInvoices() {
		return salesInvoices;
	}

	public void setSalesInvoices(List<SalesInvoice> salesInvoices) {
		this.salesInvoices = salesInvoices;
	}

	public String getCustomerName() {
		return (customer != null) ? customer.getCode() + " - " + customer.getName() : "ALL";
	}

	public String getTransactionDate() {
		if (transactionDateFrom.equals(transactionDateTo)) {
			return FormatterUtil.formatDate(transactionDateFrom);
		} else {
			return FormatterUtil.formatDate(transactionDateFrom) + " - " + 
						FormatterUtil.formatDate(transactionDateTo);
		}
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

	public BigDecimal getTotalNetCost() {
		BigDecimal total = Constants.ZERO;
		for (SalesInvoice salesInvoice : salesInvoices) {
			total = total.add(salesInvoice.getTotalNetCost());
		}
		return total;
	}
	
	public BigDecimal getTotalNetProfit() {
		BigDecimal total = Constants.ZERO;
		for (SalesInvoice salesInvoice : salesInvoices) {
			total = total.add(salesInvoice.getTotalNetProfit());
		}
		return total;
	}
	
}