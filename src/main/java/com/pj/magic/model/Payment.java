package com.pj.magic.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.pj.magic.Constants;

public class Payment {

	private Long id;
	private Long paymentNumber;
	private Customer customer;
	private boolean posted;
	private Date postDate;
	private User postedBy;
	private List<PaymentSalesInvoice> salesInvoices = new ArrayList<>();
	private List<PaymentCheckPayment> checkPayments = new ArrayList<>();
	private List<PaymentCashPayment> cashPayments = new ArrayList<>();
	private List<PaymentAdjustment> adjustments = new ArrayList<>();

	public Payment() {
		// default constructor
	}
	
	public Payment(long id) {
		this.id = id;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Customer getCustomer() {
		return customer;
	}

	public void setCustomer(Customer customer) {
		this.customer = customer;
	}

	public Long getPaymentNumber() {
		return paymentNumber;
	}

	public void setPaymentNumber(Long paymentNumber) {
		this.paymentNumber = paymentNumber;
	}

	public boolean isPosted() {
		return posted;
	}

	public void setPosted(boolean posted) {
		this.posted = posted;
	}

	public List<PaymentSalesInvoice> getSalesInvoices() {
		return salesInvoices;
	}

	public void setSalesInvoices(List<PaymentSalesInvoice> salesInvoices) {
		this.salesInvoices = salesInvoices;
	}

	public List<PaymentCheckPayment> getCheckPayments() {
		return checkPayments;
	}

	public void setCheckPayments(List<PaymentCheckPayment> checkPayments) {
		this.checkPayments = checkPayments;
	}

	public BigDecimal getTotalAmountDue() {
		BigDecimal total = Constants.ZERO;
		for (PaymentSalesInvoice salesInvoice : salesInvoices) {
			total = total.add(salesInvoice.getAmountDue());
		}
		return total;
	}

	public BigDecimal getTotalCheckPayments() {
		BigDecimal total = Constants.ZERO;
		for (PaymentCheckPayment check : checkPayments) {
			total = total.add(check.getAmount());
		}
		return total;
	}
	
	public BigDecimal getTotalPayments() {
		return getTotalCashPayments().add(getTotalCheckPayments());
	}
	
	public BigDecimal getOverOrShort() {
		return getTotalPayments().subtract(getTotalAmountDue()).add(getTotalAdjustments());
	}

	public BigDecimal getTotalCashPayments() {
		BigDecimal total = Constants.ZERO;
		for (PaymentCashPayment cashPayment : cashPayments) {
			total = total.add(cashPayment.getAmount());
		}
		return total;
	}

	public BigDecimal getTotalAdjustments() {
		BigDecimal total = Constants.ZERO;
		for (PaymentAdjustment adjustment : adjustments) {
			total = total.add(adjustment.getAmount());
		}
		return total;
	}

	public List<PaymentCashPayment> getCashPayments() {
		return cashPayments;
	}

	public void setCashPayments(List<PaymentCashPayment> cashPayments) {
		this.cashPayments = cashPayments;
	}

	public List<PaymentAdjustment> getAdjustments() {
		return adjustments;
	}

	public void setAdjustments(List<PaymentAdjustment> adjustments) {
		this.adjustments = adjustments;
	}

	public BigDecimal getTotalNetAmount() {
		BigDecimal total = Constants.ZERO;
		for (PaymentSalesInvoice paymentSalesInvoice : salesInvoices) {
			total = total.add(paymentSalesInvoice.getSalesInvoice().getTotalNetAmount());
		}
		return total;
	}
	
	public BigDecimal getTotalAdjustmentAmount() {
		BigDecimal total = Constants.ZERO;
		for (PaymentSalesInvoice paymentSalesInvoice : salesInvoices) {
			if (paymentSalesInvoice.getAdjustmentAmount() != null) {
				total = total.add(paymentSalesInvoice.getAdjustmentAmount());
			}
		}
		return total;
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
	
}