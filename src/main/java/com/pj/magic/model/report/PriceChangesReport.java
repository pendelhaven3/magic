package com.pj.magic.model.report;

import java.util.Date;
import java.util.List;

import com.pj.magic.model.ProductPriceHistory;

public class PriceChangesReport {

	private Date fromDate;
	private Date toDate;
	private List<ProductPriceHistory> items;

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

	public List<ProductPriceHistory> getItems() {
		return items;
	}

	public void setItems(List<ProductPriceHistory> items) {
		this.items = items;
	}

}