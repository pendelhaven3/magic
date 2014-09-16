package com.pj.magic.model;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ReceivingReceipt {

	private Long id;
	private Long receivingReceiptNumber;
	private Supplier supplier;
	private Date orderDate;
	private Date receivedDate;
	private User receivedBy;
	private PaymentTerm paymentTerm; // TODO: Look for payment terms
	private String referenceNumber;
	private List<ReceivingReceiptItem> items = new ArrayList<>();
	private boolean posted;
	private String remarks;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
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

	public Date getReceivedDate() {
		return receivedDate;
	}

	public Date getOrderDate() {
		return orderDate;
	}

	public void setOrderDate(Date orderDate) {
		this.orderDate = orderDate;
	}

	public void setReceivedDate(Date receivedDate) {
		this.receivedDate = receivedDate;
	}

	public User getReceivedBy() {
		return receivedBy;
	}

	public void setReceivedBy(User receivedBy) {
		this.receivedBy = receivedBy;
	}

	public PaymentTerm getPaymentTerm() {
		return paymentTerm;
	}

	public void setPaymentTerm(PaymentTerm paymentTerm) {
		this.paymentTerm = paymentTerm;
	}

	public String getReferenceNumber() {
		return referenceNumber;
	}

	public void setReferenceNumber(String referenceNumber) {
		this.referenceNumber = referenceNumber;
	}

	public void setItems(List<ReceivingReceiptItem> items) {
		this.items = items;
	}
	
	public List<ReceivingReceiptItem> getItems() {
		return items;
	}

	public boolean isPosted() {
		return posted;
	}

	public void setPosted(boolean posted) {
		this.posted = posted;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public String getStatus() {
		return posted ? "Posted" : "New";
	}

	public int getTotalNumberOfItems() {
		return items.size();
	}

	public BigDecimal getTotalAmount() {
		BigDecimal totalAmount = BigDecimal.ZERO;
		for (ReceivingReceiptItem item : items) {
			totalAmount = totalAmount.add(item.getAmount());
		}
		return totalAmount.setScale(2, RoundingMode.HALF_UP);
	}

	public BigDecimal getTotalDiscountedAmount() {
		BigDecimal totalDiscountedAmount = BigDecimal.ZERO;
		for (ReceivingReceiptItem item : items) {
			totalDiscountedAmount = totalDiscountedAmount.add(item.getDiscountedAmount());
		}
		return totalDiscountedAmount.setScale(2, RoundingMode.HALF_UP);
	}

	public BigDecimal getTotalNetAmount() {
		BigDecimal totalNetAmount = BigDecimal.ZERO;
		for (ReceivingReceiptItem item : items) {
			totalNetAmount = totalNetAmount.add(item.getNetAmount());
		}
		return totalNetAmount.setScale(2, RoundingMode.HALF_UP);
	}
	
}
