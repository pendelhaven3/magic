package com.pj.magic.model.search;

import java.util.Date;

import com.pj.magic.model.Product;

public class StockCardInventoryReportSearchCriteria {

	private Product product;
	private Date fromDate;
	private Date toDate;

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

}