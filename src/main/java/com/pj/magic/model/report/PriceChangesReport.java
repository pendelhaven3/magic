package com.pj.magic.model.report;

import java.util.Date;
import java.util.List;

import com.pj.magic.model.ProductPriceHistory;

public class PriceChangesReport {

	private Date reportDate;
	private List<ProductPriceHistory> items;

	public Date getReportDate() {
		return reportDate;
	}

	public void setReportDate(Date reportDate) {
		this.reportDate = reportDate;
	}

	public List<ProductPriceHistory> getItems() {
		return items;
	}

	public void setItems(List<ProductPriceHistory> items) {
		this.items = items;
	}

}