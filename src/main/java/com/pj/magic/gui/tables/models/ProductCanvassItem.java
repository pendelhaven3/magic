package com.pj.magic.gui.tables.models;

import java.math.BigDecimal;
import java.util.Date;

import com.pj.magic.model.Supplier;

public class ProductCanvassItem {

	private Date receivedDate;
	private Long receivingReceiptNumber;
	private Supplier supplier;
	private BigDecimal finalCost;
	private BigDecimal currentCost;
	private String remarks;
	public Date getReceivedDate() {
		return receivedDate;
	}
	public void setReceivedDate(Date receivedDate) {
		this.receivedDate = receivedDate;
	}
	public Long getReceivingReceiptNumber() {
		return receivingReceiptNumber;
	}
	public void setReceivingReceiptNumber(Long receivingReceiptNumber) {
		this.receivingReceiptNumber = receivingReceiptNumber;
	}
	public Supplier getSupplier() {
		return supplier;
	}
	public void setSupplier(Supplier supplier) {
		this.supplier = supplier;
	}
	public BigDecimal getFinalCost() {
		return finalCost;
	}
	public void setFinalCost(BigDecimal finalCost) {
		this.finalCost = finalCost;
	}
	public BigDecimal getCurrentCost() {
		return currentCost;
	}
	public void setCurrentCost(BigDecimal currentCost) {
		this.currentCost = currentCost;
	}
	public String getRemarks() {
		return remarks;
	}
	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}
	
}
