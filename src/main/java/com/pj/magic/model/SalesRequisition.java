package com.pj.magic.model;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class SalesRequisition {

	private Long salesRequisitionNumber;
	private String customerName; // TODO: Turn into Customer object
	private Date createDate;
	private String encoder; // TODO: Turn into User object
	private List<SalesRequisitionItem> items = new ArrayList<>();

	public BigDecimal getTotalAmount() {
		BigDecimal total = BigDecimal.ZERO;
		for (SalesRequisitionItem item : items) {
			total = total.add(item.getAmount());
		}
		return total.setScale(2, RoundingMode.HALF_UP);
	}
	
	public boolean hasItems() {
		return !items.isEmpty();
	}
	
	public Long getSalesRequisitionNumber() {
		return salesRequisitionNumber;
	}

	public void setSalesRequisitionNumber(Long salesRequisitionNumber) {
		this.salesRequisitionNumber = salesRequisitionNumber;
	}

	public String getCustomerName() {
		return customerName;
	}

	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}

	public List<SalesRequisitionItem> getItems() {
		return items;
	}

	public void setItems(List<SalesRequisitionItem> items) {
		this.items = items;
	}

	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	public String getEncoder() {
		return encoder;
	}

	public void setEncoder(String encoder) {
		this.encoder = encoder;
	}

}
