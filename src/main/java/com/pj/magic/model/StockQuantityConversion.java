package com.pj.magic.model;

import java.util.ArrayList;
import java.util.List;

public class StockQuantityConversion {

	private Long id;
	private Long stockQuantityConversionNumber;
	private String remarks;
	private List<StockQuantityConversionItem> items = new ArrayList<>();

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getStockQuantityConversionNumber() {
		return stockQuantityConversionNumber;
	}

	public void setStockQuantityConversionNumber(Long stockQuantityConversionNumber) {
		this.stockQuantityConversionNumber = stockQuantityConversionNumber;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public List<StockQuantityConversionItem> getItems() {
		return items;
	}

	public void setItems(List<StockQuantityConversionItem> items) {
		this.items = items;
	}

	public int getTotalNumberOfItems() {
		return items.size();
	}

	public boolean hasItems() {
		return !items.isEmpty();
	}
	
}
