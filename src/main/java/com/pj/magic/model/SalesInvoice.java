package com.pj.magic.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class SalesInvoice {

	private Long id;
	private Long salesInvoiceNumber;
	private Customer customer;
	private Date postDate;
	private String postedBy;
	private SalesRequisition origin;
	private List<SalesInvoiceItem> items = new ArrayList<>();
	private String mode = "WALK-IN"; // TODO: replace with actual value
	private String remarks = "DUMMY REMARKS"; // TODO: replace with actual value
	private PricingScheme pricingScheme = new PricingScheme(); // TODO: replace with actual value
	private String encodedBy;

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

	public Customer getCustomer() {
		return customer;
	}

	public void setCustomer(Customer customer) {
		this.customer = customer;
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

	public String getMode() {
		return mode;
	}

	public void setMode(String mode) {
		this.mode = mode;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public PricingScheme getPricingScheme() {
		return pricingScheme;
	}

	public void setPricingScheme(PricingScheme pricingScheme) {
		this.pricingScheme = pricingScheme;
	}

	public int getTotalQuantity() {
		int total = 0;
		for (SalesInvoiceItem item : items) {
			total += item.getQuantity();
		}
		return total;
	}
	
	public BigDecimal getTotalDiscountedAmount() {
		return BigDecimal.ZERO; // TODO: add implementation
	}

	public String getEncodedBy() {
		return encodedBy;
	}

	public void setEncodedBy(String encodedBy) {
		this.encodedBy = encodedBy;
	}
	
	public BigDecimal getTotalNetAmount() {
		return getTotalAmount(); // TODO: add correct implementation
	}
}
