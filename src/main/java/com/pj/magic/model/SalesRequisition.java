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

	public void validate() {
		for (SalesRequisitionItem item : items) {
			if (!item.isValid()) {
				
			}
		}
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

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public SalesInvoice createSalesInvoice() {
		SalesInvoice salesInvoice = new SalesInvoice();
		salesInvoice.setCustomerName(customerName);
		salesInvoice.setPostDate(new Date());
		salesInvoice.setPostedBy("PJ POST"); // TODO: to be implemented
		
		for (SalesRequisitionItem item : items) {
			SalesInvoiceItem invoiceItem = new SalesInvoiceItem();
			invoiceItem.setParent(salesInvoice);
			invoiceItem.setProduct(item.getProduct());
			invoiceItem.setUnit(item.getUnit());
			invoiceItem.setQuantity(item.getQuantity());
			salesInvoice.getItems().add(invoiceItem);
		}
		
		return salesInvoice;
	}
	
}
