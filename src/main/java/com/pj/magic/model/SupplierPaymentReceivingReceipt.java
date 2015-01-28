package com.pj.magic.model;

public class SupplierPaymentReceivingReceipt {

	private Long id;
	private SupplierPayment parent;
	private ReceivingReceipt receivingReceipt;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public SupplierPayment getParent() {
		return parent;
	}

	public void setParent(SupplierPayment parent) {
		this.parent = parent;
	}

	public ReceivingReceipt getReceivingReceipt() {
		return receivingReceipt;
	}

	public void setReceivingReceipt(ReceivingReceipt receivingReceipt) {
		this.receivingReceipt = receivingReceipt;
	}

}