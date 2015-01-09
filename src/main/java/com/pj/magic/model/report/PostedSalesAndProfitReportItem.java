package com.pj.magic.model.report;

import java.math.BigDecimal;
import java.util.Date;

import com.pj.magic.Constants;
import com.pj.magic.model.Customer;
import com.pj.magic.model.NoMoreStockAdjustment;
import com.pj.magic.model.SalesInvoice;
import com.pj.magic.model.SalesReturn;

public class PostedSalesAndProfitReportItem {

	private Date transactionDate;
	private String transactionType;
	private Long transactionNumber;
	private Customer customer;
	private BigDecimal totalAmount = Constants.ZERO;
	private BigDecimal totalDiscounts = Constants.ZERO;
	private BigDecimal netAmount = Constants.ZERO;
	private BigDecimal netCost = Constants.ZERO;
	private BigDecimal netProfit = Constants.ZERO;

	public PostedSalesAndProfitReportItem(SalesInvoice salesInvoice) {
		transactionDate = salesInvoice.getTransactionDate();
		transactionType = "SALES INVOICE";
		transactionNumber = salesInvoice.getSalesInvoiceNumber();
		customer = salesInvoice.getCustomer();
		totalAmount = salesInvoice.getTotalAmount();
		totalDiscounts = salesInvoice.getTotalDiscounts();
		netAmount = salesInvoice.getTotalNetAmount();
		netCost = salesInvoice.getTotalNetCost();
		netProfit = salesInvoice.getTotalNetProfit();
	}

	public PostedSalesAndProfitReportItem(SalesReturn salesReturn) {
		transactionDate = salesReturn.getPostDate();
		transactionType = "SALES RETURN";
		transactionNumber = salesReturn.getSalesReturnNumber();
		customer = salesReturn.getSalesInvoice().getCustomer();
		netAmount = salesReturn.getTotalAmount().negate();
		netCost = salesReturn.getTotalNetCost().negate();
		netProfit = salesReturn.getTotalNetProfit().negate();
	}
	
	public PostedSalesAndProfitReportItem(NoMoreStockAdjustment noMoreStockAdjustment) {
		transactionDate = noMoreStockAdjustment.getPostDate();
		transactionType = "NO MORE STOCK";
		transactionNumber = noMoreStockAdjustment.getNoMoreStockAdjustmentNumber();
		customer = noMoreStockAdjustment.getSalesInvoice().getCustomer();
		netAmount = netProfit = noMoreStockAdjustment.getTotalAmount().negate();
	}
	
	public Date getTransactionDate() {
		return transactionDate;
	}

	public void setTransactionDate(Date transactionDate) {
		this.transactionDate = transactionDate;
	}

	public Long getTransactionNumber() {
		return transactionNumber;
	}

	public void setTransactionNumber(Long transactionNumber) {
		this.transactionNumber = transactionNumber;
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

	public String getTransactionType() {
		return transactionType;
	}

	public void setTransactionType(String transactionType) {
		this.transactionType = transactionType;
	}

}