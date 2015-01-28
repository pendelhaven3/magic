package com.pj.magic.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.pj.magic.Constants;

public class SupplierPayment {

	private Long id;
	private Long supplierPaymentNumber;
	private Supplier supplier;
	private boolean posted;
	private Date postDate;
	private User postedBy;
	private Date createDate;
	private User encoder;
	private boolean cancelled;
	private Date cancelDate;
	private User cancelledBy;
	private List<SupplierPaymentReceivingReceipt> receivingReceipts = new ArrayList<>();
	private List<SupplierPaymentCashPayment> cashPayments = new ArrayList<>();
	private List<SupplierPaymentCreditCardPayment> creditCardPayments = new ArrayList<>();
	private List<SupplierPaymentCheckPayment> checkPayments = new ArrayList<>();
//	private List<PaymentPaymentAdjustment> adjustments = new ArrayList<>();

	public SupplierPayment() {
		// default constructor
	}
	
	public SupplierPayment(long id) {
		this.id = id;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getSupplierPaymentNumber() {
		return supplierPaymentNumber;
	}

	public void setSupplierPaymentNumber(Long supplierPaymentNumber) {
		this.supplierPaymentNumber = supplierPaymentNumber;
	}

	public Supplier getSupplier() {
		return supplier;
	}

	public void setSupplier(Supplier supplier) {
		this.supplier = supplier;
	}

	public boolean isPosted() {
		return posted;
	}

	public void setPosted(boolean posted) {
		this.posted = posted;
	}

	public Date getPostDate() {
		return postDate;
	}

	public void setPostDate(Date postDate) {
		this.postDate = postDate;
	}

	public User getPostedBy() {
		return postedBy;
	}

	public void setPostedBy(User postedBy) {
		this.postedBy = postedBy;
	}

	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	public User getEncoder() {
		return encoder;
	}

	public void setEncoder(User encoder) {
		this.encoder = encoder;
	}

	public boolean isCancelled() {
		return cancelled;
	}

	public void setCancelled(boolean cancelled) {
		this.cancelled = cancelled;
	}

	public Date getCancelDate() {
		return cancelDate;
	}

	public void setCancelDate(Date cancelDate) {
		this.cancelDate = cancelDate;
	}

	public User getCancelledBy() {
		return cancelledBy;
	}

	public void setCancelledBy(User cancelledBy) {
		this.cancelledBy = cancelledBy;
	}

	public List<SupplierPaymentReceivingReceipt> getReceivingReceipts() {
		return receivingReceipts;
	}

	public void setReceivingReceipts(
			List<SupplierPaymentReceivingReceipt> receivingReceipts) {
		this.receivingReceipts = receivingReceipts;
	}

	public BigDecimal getTotalAmount() {
		BigDecimal total = Constants.ZERO;
		for (SupplierPaymentReceivingReceipt receivingReceipt : receivingReceipts) {
			total = total.add(receivingReceipt.getReceivingReceipt().getTotalNetAmount());
		}
		return total;
	}

	public String getStatus() {
		return (posted) ? "Posted" : "New";
	}

	public List<SupplierPaymentCashPayment> getCashPayments() {
		return cashPayments;
	}

	public void setCashPayments(List<SupplierPaymentCashPayment> cashPayments) {
		this.cashPayments = cashPayments;
	}

	public boolean isNew() {
		return !posted && !cancelled;
	}

	public BigDecimal getTotalCashPayments() {
		BigDecimal total = Constants.ZERO;
		for (SupplierPaymentCashPayment cashPayment : cashPayments) {
			total = total.add(cashPayment.getAmount());
		}
		return total;
	}
	
	public BigDecimal getTotalCreditCardPayments() {
		BigDecimal total = Constants.ZERO;
		for (SupplierPaymentCreditCardPayment creditCardPayment : creditCardPayments) {
			total = total.add(creditCardPayment.getAmount());
		}
		return total;
	}
	
	public BigDecimal getTotalPayments() {
		return getTotalCashPayments().add(getTotalCreditCardPayments()).add(getTotalCheckPayments());
	}
	
	public BigDecimal getTotalCheckPayments() {
		BigDecimal total = Constants.ZERO;
		for (SupplierPaymentCheckPayment check : checkPayments) {
			total = total.add(check.getAmount());
		}
		return total;
	}
	
	public BigDecimal getOverOrShort() {
		return getTotalPayments().subtract(getTotalAmount()).add(getTotalAdjustments());
	}

	public BigDecimal getTotalAdjustments() {
		BigDecimal total = Constants.ZERO;
//		for (PaymentPaymentAdjustment adjustment : adjustments) {
//			total = total.add(adjustment.getAmount());
//		}
		return total;
	}

	public List<SupplierPaymentCreditCardPayment> getCreditCardPayments() {
		return creditCardPayments;
	}

	public void setCreditCardPayments(List<SupplierPaymentCreditCardPayment> creditCardPayments) {
		this.creditCardPayments = creditCardPayments;
	}

	public List<SupplierPaymentCheckPayment> getCheckPayments() {
		return checkPayments;
	}

	public void setCheckPayments(List<SupplierPaymentCheckPayment> checkPayments) {
		this.checkPayments = checkPayments;
	}
	
}