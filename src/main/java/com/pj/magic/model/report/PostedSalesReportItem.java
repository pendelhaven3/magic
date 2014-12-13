package com.pj.magic.model.report;

import java.math.BigDecimal;

import com.pj.magic.Constants;
import com.pj.magic.model.Customer;
import com.pj.magic.model.SalesInvoice;
import com.pj.magic.model.SalesReturn;

public class PostedSalesReportItem {

	private Long transactionNumber;
	private Customer customer;
	private BigDecimal totalAmount = Constants.ZERO;
	private BigDecimal totalDiscounts = Constants.ZERO;
	private BigDecimal totalNetAmount = Constants.ZERO;
	
	public PostedSalesReportItem(SalesInvoice salesInvoice) {
		transactionNumber = salesInvoice.getSalesInvoiceNumber();
		customer = salesInvoice.getCustomer();
		totalAmount = salesInvoice.getTotalAmount();
		totalDiscounts = salesInvoice.getTotalDiscounts();
		totalNetAmount = salesInvoice.getTotalNetAmount();
	}
	
	public PostedSalesReportItem(SalesReturn salesReturn) {
		transactionNumber = salesReturn.getSalesReturnNumber();
		customer = salesReturn.getSalesInvoice().getCustomer();
		totalNetAmount = salesReturn.getTotalAmount().negate();
	}
	
	public Long getTransactionNumber() {
		return transactionNumber;
	}
	
	public Customer getCustomer() {
		return customer;
	}
	
	public BigDecimal getTotalAmount() {
		return totalAmount;
	}
	
	public BigDecimal getTotalDiscounts() {
		return totalDiscounts;
	}
	
	public BigDecimal getTotalNetAmount() {
		return totalNetAmount;
	}
	
}