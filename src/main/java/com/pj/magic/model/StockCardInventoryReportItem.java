package com.pj.magic.model;

import java.math.BigDecimal;
import java.util.Date;

public class StockCardInventoryReportItem {

	private Date transactionDate;
	private Long salesInvoiceNumber;
	private String supplierOrCustomerName;
	private String transactionType;
	private Integer addQuantity;
	private Integer lessQuantity;
	private BigDecimal currentCostOrSellingPrice;
	private BigDecimal amount;
	private String remarks;

	public Date getTransactionDate() {
		return transactionDate;
	}

	public void setTransactionDate(Date transactionDate) {
		this.transactionDate = transactionDate;
	}

	public Long getSalesInvoiceNumber() {
		return salesInvoiceNumber;
	}

	public void setSalesInvoiceNumber(Long salesInvoiceNumber) {
		this.salesInvoiceNumber = salesInvoiceNumber;
	}

	public String getSupplierOrCustomerName() {
		return supplierOrCustomerName;
	}

	public void setSupplierOrCustomerName(String supplierOrCustomerName) {
		this.supplierOrCustomerName = supplierOrCustomerName;
	}

	public String getTransactionType() {
		return transactionType;
	}

	public void setTransactionType(String transactionType) {
		this.transactionType = transactionType;
	}

	public Integer getAddQuantity() {
		return addQuantity;
	}

	public void setAddQuantity(Integer addQuantity) {
		this.addQuantity = addQuantity;
	}

	public Integer getLessQuantity() {
		return lessQuantity;
	}

	public void setLessQuantity(Integer lessQuantity) {
		this.lessQuantity = lessQuantity;
	}

	public BigDecimal getCurrentCostOrSellingPrice() {
		return currentCostOrSellingPrice;
	}

	public void setCurrentCostOrSellingPrice(
			BigDecimal currentCostOrSellingPrice) {
		this.currentCostOrSellingPrice = currentCostOrSellingPrice;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

}