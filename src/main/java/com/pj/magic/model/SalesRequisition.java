package com.pj.magic.model;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

public class SalesRequisition {

	private Long id;
	private Long salesRequisitionNumber;
	private Customer customer;
	private Date createDate;
	private User encoder;
	private boolean posted;
	private List<SalesRequisitionItem> items = new ArrayList<>();
	private PricingScheme pricingScheme;
	private String mode;
	private String remarks;
	private PaymentTerm paymentTerm;

	public SalesRequisition() {
	}
	
	public SalesRequisition(long id) {
		this.id = id;
	}
	
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
	
	public int getTotalNumberOfItems() {
		return items.size();
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder()
			.append(id)
			.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
        if (!(obj instanceof SalesRequisition)) {
            return false;
        }
        SalesRequisition other = (SalesRequisition)obj;		
		return new EqualsBuilder()
			.append(id, other.getId())
			.isEquals();
	}

	public Long getSalesRequisitionNumber() {
		return salesRequisitionNumber;
	}

	public void setSalesRequisitionNumber(Long salesRequisitionNumber) {
		this.salesRequisitionNumber = salesRequisitionNumber;
	}

	public Customer getCustomer() {
		return customer;
	}

	public void setCustomer(Customer customer) {
		this.customer = customer;
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

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public SalesInvoice createSalesInvoice() {
		SalesInvoice salesInvoice = new SalesInvoice();
		salesInvoice.setCustomer(customer);
		salesInvoice.setPricingScheme(pricingScheme);
		salesInvoice.setMode(mode);
		salesInvoice.setRemarks(remarks);
		salesInvoice.setPostDate(new Date());
		salesInvoice.setRelatedSalesRequisitionNumber(salesRequisitionNumber);
		
		for (SalesRequisitionItem item : items) {
			SalesInvoiceItem invoiceItem = new SalesInvoiceItem();
			invoiceItem.setParent(salesInvoice);
			invoiceItem.setProduct(item.getProduct());
			invoiceItem.setUnit(item.getUnit());
			invoiceItem.setQuantity(item.getQuantity());
			invoiceItem.setUnitPrice(item.getUnitPrice());
			salesInvoice.getItems().add(invoiceItem);
		}
		
		return salesInvoice;
	}

	public boolean isPosted() {
		return posted;
	}

	public void setPosted(boolean posted) {
		this.posted = posted;
	}

	public User getEncoder() {
		return encoder;
	}

	public void setEncoder(User encoder) {
		this.encoder = encoder;
	}

	public PricingScheme getPricingScheme() {
		return pricingScheme;
	}

	public void setPricingScheme(PricingScheme pricingScheme) {
		this.pricingScheme = pricingScheme;
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
	
	// TODO: Remove this
	public boolean hasMinimumFieldsFilledUp() {
		return customer != null && pricingScheme != null;
	}

	public PaymentTerm getPaymentTerm() {
		return paymentTerm;
	}

	public void setPaymentTerm(PaymentTerm paymentTerm) {
		this.paymentTerm = paymentTerm;
	}
	
}
