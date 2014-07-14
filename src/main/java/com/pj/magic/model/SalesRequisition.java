package com.pj.magic.model;

import java.util.ArrayList;
import java.util.List;

public class SalesRequisition {

	private Long salesRequisitionNumber;
	private String customerName; // TODO: Turn into Customer object
	private List<SalesRequisitionItem> items = new ArrayList<>();

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

}
