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
	private User postedBy;
	private List<SalesInvoiceItem> items = new ArrayList<>();
	private String mode;
	private String remarks;
	private PricingScheme pricingScheme;
	private Long relatedSalesRequisitionNumber;

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

	public User getPostedBy() {
		return postedBy;
	}

	public void setPostedBy(User postedBy) {
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

	public Long getRelatedSalesRequisitionNumber() {
		return relatedSalesRequisitionNumber;
	}

	public void setRelatedSalesRequisitionNumber(Long relatedSalesRequisitionNumber) {
		this.relatedSalesRequisitionNumber = relatedSalesRequisitionNumber;
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

	public BigDecimal getTotalNetAmount() {
		return getTotalAmount(); // TODO: add correct implementation
	}

}
