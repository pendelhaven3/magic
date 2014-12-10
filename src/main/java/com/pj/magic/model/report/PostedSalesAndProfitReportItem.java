package com.pj.magic.model.report;

import java.math.BigDecimal;
import java.util.Date;

import com.pj.magic.model.Customer;
import com.pj.magic.model.SalesInvoice;
import com.pj.magic.model.SalesReturn;

public class PostedSalesAndProfitReportItem {

	private Date transactionDate;
	private Long referenceNumber;
	private Customer customer;
	private BigDecimal totalAmount;
	private BigDecimal totalDiscounts;
	private BigDecimal netAmount;
	private BigDecimal netCost;
	private BigDecimal netProfit;

	public PostedSalesAndProfitReportItem(SalesInvoice salesInvoice) {
		transactionDate = salesInvoice.getTransactionDate();
		referenceNumber = salesInvoice.getSalesInvoiceNumber();
		customer = salesInvoice.getCustomer();
		totalAmount = salesInvoice.getTotalAmount();
		totalDiscounts = salesInvoice.getTotalDiscounts();
		netAmount = salesInvoice.getTotalNetAmount();
		netCost = salesInvoice.getTotalNetCost();
		netProfit = salesInvoice.getTotalNetProfit();
	}

	public PostedSalesAndProfitReportItem(SalesReturn salesReturn) {
		transactionDate = salesReturn.getPostDate();
		referenceNumber = salesReturn.getSalesReturnNumber();
		customer = salesReturn.getSalesInvoice().getCustomer();
		netAmount = salesReturn.getTotalAmount().negate();
		netCost = salesReturn.getTotalNetCost().negate();
		netProfit = salesReturn.getTotalNetProfit().negate();
	}
	
	public Date getTransactionDate() {
		return transactionDate;
	}

	public void setTransactionDate(Date transactionDate) {
		this.transactionDate = transactionDate;
	}

	public Long getReferenceNumber() {
		return referenceNumber;
	}

	public void setReferenceNumber(Long referenceNumber) {
		this.referenceNumber = referenceNumber;
	}

	public Customer getCustomer() {
		return customer;
	}

	public void setCustomer(Customer customer) {
		this.customer = customer;
	}

	public BigDecimal getTotalAmount() {
		return totalAmount;
	}

	public void setTotalAmount(BigDecimal totalAmount) {
		this.totalAmount = totalAmount;
	}

	public BigDecimal getTotalDiscounts() {
		return totalDiscounts;
	}

	public void setTotalDiscounts(BigDecimal totalDiscounts) {
		this.totalDiscounts = totalDiscounts;
	}

	public BigDecimal getNetAmount() {
		return netAmount;
	}

	public void setNetAmount(BigDecimal netAmount) {
		this.netAmount = netAmount;
	}

	public BigDecimal getNetCost() {
		return netCost;
	}

	public void setNetCost(BigDecimal netCost) {
		this.netCost = netCost;
	}

	public BigDecimal getNetProfit() {
		return netProfit;
	}

	public void setNetProfit(BigDecimal netProfit) {
		this.netProfit = netProfit;
	}

}