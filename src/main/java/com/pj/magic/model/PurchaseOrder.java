package com.pj.magic.model;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class PurchaseOrder {

	private Long id;
	private Long purchaseOrderNumber;
	private Supplier supplier;
	private List<PurchaseOrderItem> items = new ArrayList<>();
	private boolean posted;
	private boolean delivered;
	private PaymentTerm paymentTerm;
	private String remarks;
	private String referenceNumber;
	private Date postDate;
	private User createdBy;

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
		} else if (delivered) {
			return "Delivered";
		} else {
			return "New";
		}
	}

	public boolean isDelivered() {
		return delivered;
	}

	public void setDelivered(boolean delivered) {
		this.delivered = delivered;
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
		receivingReceipt.setPaymentTerm(paymentTerm);
		receivingReceipt.setReferenceNumber(referenceNumber);
		receivingReceipt.setRemarks(remarks);
		receivingReceipt.setRelatedPurchaseOrderNumber(purchaseOrderNumber);
		
		for (PurchaseOrderItem item : items) {
			if (item.getActualQuantity() > 0) {
				ReceivingReceiptItem receivingReceiptItem = new ReceivingReceiptItem();
				receivingReceiptItem.setParent(receivingReceipt);
				receivingReceiptItem.setProduct(item.getProduct());
				receivingReceiptItem.setUnit(item.getUnit());
				receivingReceiptItem.setQuantity(item.getActualQuantity());
				receivingReceiptItem.setCost(item.getCost());
				receivingReceipt.getItems().add(receivingReceiptItem);
			}
		}
		
		return receivingReceipt;
	}
	
	public int getTotalQuantity() {
		int totalQuantity = 0;
		for (PurchaseOrderItem item : items) {
			totalQuantity += item.getQuantity();
		}
		return totalQuantity;
	}

	public int getTotalNumberOfItems() {
		return items.size();
	}

	public BigDecimal getTotalAmount() {
		BigDecimal total = BigDecimal.ZERO;
		for (PurchaseOrderItem item : items) {
			total = total.add(item.getAmount());
		}
		return total.setScale(2, RoundingMode.HALF_UP);
	}

	public Date getPostDate() {
		return postDate;
	}

	public void setPostDate(Date postDate) {
		this.postDate = postDate;
	}

	public User getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(User createdBy) {
		this.createdBy = createdBy;
	}
	
}
