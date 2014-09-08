package com.pj.magic.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ReceivingReceipt {

	private Long id;
	private Long receivingReceiptNumber;
	private Supplier supplier;
	private Date receivedDate;
	private User receivedBy;
	private PaymentTerm paymentTerm; // TODO: Look for payment terms
	private String referenceNumber;
	private List<ReceivingReceiptItem> items = new ArrayList<>();

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
	
}
