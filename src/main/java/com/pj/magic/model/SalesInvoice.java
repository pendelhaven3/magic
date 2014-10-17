package com.pj.magic.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class SalesInvoice {

	private Long id;
	private Long salesInvoiceNumber;
	private Customer customer;
	private Date createDate;
	private User createdBy;
	private Date postDate;
	private User postedBy;
	private List<SalesInvoiceItem> items = new ArrayList<>();
	private String mode;
	private String remarks;
	private PricingScheme pricingScheme;
	private Long relatedSalesRequisitionNumber;
	private PaymentTerm paymentTerm;
	private Date cancelDate;
	private User cancelledBy;
	private boolean cancelled;
	private boolean posted;

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

	public PaymentTerm getPaymentTerm() {
		return paymentTerm;
	}

	public void setPaymentTerm(PaymentTerm paymentTerm) {
		this.paymentTerm = paymentTerm;
	}
	
	public SalesRequisition createSalesRequisition() {
		SalesRequisition salesRequisition = new SalesRequisition();
		salesRequisition.setCustomer(customer);
		salesRequisition.setPaymentTerm(paymentTerm);
		salesRequisition.setPricingScheme(pricingScheme);
		salesRequisition.setMode(mode);
		salesRequisition.setRemarks(remarks);
		
		for (SalesInvoiceItem invoiceItem : items) {
			SalesRequisitionItem item = new SalesRequisitionItem();
			item.setParent(salesRequisition);
			item.setProduct(invoiceItem.getProduct());
			item.setUnit(invoiceItem.getUnit());
			item.setQuantity(invoiceItem.getQuantity());
			salesRequisition.getItems().add(item);
		}
		
		return salesRequisition;
	}

	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	public User getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(User createdBy) {
		this.createdBy = createdBy;
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

	public boolean isCancelled() {
		return cancelled;
	}

	public void setCancelled(boolean cancelled) {
		this.cancelled = cancelled;
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
		} else if (cancelled) {
			return "Cancelled";
		} else {
			return "Created";
		}
	}
	
}
