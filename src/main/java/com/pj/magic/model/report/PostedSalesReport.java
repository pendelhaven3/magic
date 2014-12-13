package com.pj.magic.model.report;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import com.pj.magic.Constants;

public class PostedSalesReport {

	private Date reportDate;
	private List<PostedSalesReportItem> items;

	public Date getReportDate() {
		return reportDate;
	}

	public void setReportDate(Date reportDate) {
		this.reportDate = reportDate;
	}

	public List<PostedSalesReportItem> getItems() {
		return items;
	}

	public void setItems(List<PostedSalesReportItem> items) {
		this.items = items;
	}

	public BigDecimal getTotalAmount() {
		BigDecimal total = Constants.ZERO;
		for (PostedSalesReportItem item : items) {
			total = total.add(item.getTotalAmount());
		}
		return total;
	}
	
	public BigDecimal getTotalDiscounts() {
		BigDecimal total = Constants.ZERO;
		for (PostedSalesReportItem item : items) {
			total = total.add(item.getTotalDiscounts());
		}
		return total;
	}
	
	public BigDecimal getTotalNetAmount() {
		BigDecimal total = Constants.ZERO;
		for (PostedSalesReportItem item : items) {
			total = total.add(item.getTotalNetAmount());
		}
		return total;
	}
	
}