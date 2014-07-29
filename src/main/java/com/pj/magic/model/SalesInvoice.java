package com.pj.magic.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class SalesInvoice {

	private Long id;
	private Long salesInvoiceNumber;
	private String customerName; // TODO: Use Customer object instead
	private Date postDate;
	private String postedBy;
	private SalesRequisition origin;
	private List<SalesInvoiceItem> items = new ArrayList<>();

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getSalesInvoiceNumber() {
		return salesInvoiceNumber;
	}

	public void setSalesInvoiceNumber(Long salesInvoiceNumber) {
		this.salesInvoiceNumber = salesInvoiceNumber;
	}

	public String getCustomerName() {
		return customerName;
	}

	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}

	public Date getPostDate() {
		return postDate;
	}

	public void setPostDate(Date postDate) {
		this.postDate = postDate;
	}

	public String getPostedBy() {
		return postedBy;
	}

	public void setPostedBy(String postedBy) {
		this.postedBy = postedBy;
	}

	public List<SalesInvoiceItem> getItems() {
		return items;
	}

	public void setItems(List<SalesInvoiceItem> items) {
		this.items = items;
	}

	public BigDecimal getTotalAmount() {
		BigDecimal total = BigDecimal.ZERO;
		for (SalesInvoiceItem item : items) {
			total = total.add(item.getAmount());
		}
		return total;
	}

	public int getTotalNumberOfItems() {
		return items.size();
	}
	
	public boolean hasItems() {
		return !items.isEmpty();
	}

	public SalesRequisition getOrigin() {
		return origin;
	}

	public void setOrigin(SalesRequisition origin) {
		this.origin = origin;
	}

}
