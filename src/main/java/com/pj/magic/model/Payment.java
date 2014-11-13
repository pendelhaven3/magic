package com.pj.magic.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.pj.magic.Constants;

public class Payment {

	private Long id;
	private Customer customer;
	private Date paymentDate;
	private BigDecimal amountReceived;
	private List<PaymentItem> items = new ArrayList<>();

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

	public Date getPaymentDate() {
		return paymentDate;
	}

	public void setPaymentDate(Date paymentDate) {
		this.paymentDate = paymentDate;
	}

	public BigDecimal getAmountReceived() {
		return amountReceived;
	}

	public void setAmountReceived(BigDecimal amountReceived) {
		this.amountReceived = amountReceived;
	}

	public List<PaymentItem> getItems() {
		return items;
	}

	public void setItems(List<PaymentItem> items) {
		this.items = items;
	}

	public BigDecimal getTotalAmount() {
		BigDecimal total = Constants.ZERO;
		for (PaymentItem item : items) {
			total = total.add(item.getSalesInvoice().getTotalNetAmount());
		}
		return total;
	}

	public BigDecimal getChange() {
		return amountReceived.subtract(getTotalAmount());
	}

}
