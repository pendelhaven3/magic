package com.pj.magic.model.search;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.pj.magic.model.InventoryCheck;
import com.pj.magic.model.Product;

public class StockCardInventoryReportCriteria {

	private Product product;
	private Date fromDate;
	private Date toDate;
	private List<String> transactionTypes = new ArrayList<>();
	private String unit;
	private boolean fromLastInventoryCheck;
	private InventoryCheck inventoryCheck;

	public Product getProduct() {
		return product;
	}

	public void setProduct(Product product) {
		this.product = product;
	}

	public Date getFromDate() {
		return fromDate;
	}

	public void setFromDate(Date fromDate) {
		this.fromDate = fromDate;
	}

	public Date getToDate() {
		return toDate;
	}

	public void setToDate(Date toDate) {
		this.toDate = toDate;
	}

	public List<String> getTransactionTypes() {
		return transactionTypes;
	}

	public String getUnit() {
		return unit;
	}

	public void setUnit(String unit) {
		this.unit = unit;
	}

	public boolean isFromLastInventoryCheck() {
		return fromLastInventoryCheck;
	}

	public void setFromLastInventoryCheck(boolean fromLastInventoryCheck) {
		this.fromLastInventoryCheck = fromLastInventoryCheck;
	}

	public InventoryCheck getInventoryCheck() {
		return inventoryCheck;
	}

	public void setInventoryCheck(InventoryCheck inventoryCheck) {
		this.inventoryCheck = inventoryCheck;
	}

}