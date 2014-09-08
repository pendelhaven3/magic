package com.pj.magic.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class PurchaseOrder {

	private Long id;
	private Long purchaseOrderNumber;
	private Supplier supplier;
	private List<PurchaseOrderItem> items = new ArrayList<>();
	private boolean posted;
	private boolean ordered;
	private PaymentTerm paymentTerm;
	private String remarks;
	private String referenceNumber;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getPurchaseOrderNumber() {
		return purchaseOrderNumber;
	}

	public void setPurchaseOrderNumber(Long purchaseOrderNumber) {
		this.purchaseOrderNumber = purchaseOrderNumber;
	}

	public Supplier getSupplier() {
		return supplier;
	}

	public void setSupplier(Supplier supplier) {
		this.supplier = supplier;
	}

	public List<PurchaseOrderItem> getItems() {
		return items;
	}

	public void setItems(List<PurchaseOrderItem> items) {
		this.items = items;
	}

	public boolean isPosted() {
		return posted;
	}

	public void setPosted(boolean posted) {
		this.posted = posted;
	}

	public String getStatus() {
		if (posted) {
			return "Posted";
		} else if (ordered) {
			return "Ordered";
		} else {
			return "New";
		}
	}

	public boolean isOrdered() {
		return ordered;
	}

	public void setOrdered(boolean ordered) {
		this.ordered = ordered;
	}

	public boolean hasItems() {
		return !items.isEmpty();
	}

	public PaymentTerm getPaymentTerm() {
		return paymentTerm;
	}

	public void setPaymentTerm(PaymentTerm paymentTerm) {
		this.paymentTerm = paymentTerm;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public String getReferenceNumber() {
		return referenceNumber;
	}

	public void setReferenceNumber(String referenceNumber) {
		this.referenceNumber = referenceNumber;
	}

	public ReceivingReceipt createReceivingReceipt() {
		ReceivingReceipt receivingReceipt = new ReceivingReceipt();
		receivingReceipt.setSupplier(supplier);
		receivingReceipt.setReceivedDate(new Date());
		return receivingReceipt;
	}
	
}
