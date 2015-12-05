package com.pj.magic.model.report;

import java.util.ArrayList;
import java.util.List;

public class StockTakeoffReport {
	
	private List<StockTakeoffReportItem> items = new ArrayList<>();

	public List<StockTakeoffReportItem> getItems() {
		return items;
	}

	public void setItems(List<StockTakeoffReportItem> items) {
		this.items = items;
	}
	
}
