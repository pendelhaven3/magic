package com.pj.magic.model.report;

import java.util.ArrayList;
import java.util.List;

public class StockUptakeReport {
	
	private List<StockUptakeReportItem> items = new ArrayList<>();

	public List<StockUptakeReportItem> getItems() {
		return items;
	}

	public void setItems(List<StockUptakeReportItem> items) {
		this.items = items;
	}
	
}
