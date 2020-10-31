package com.pj.magic.model.report;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.pj.magic.Constants;
import com.pj.magic.model.Manufacturer;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class InventoryReport {
	
	private Manufacturer manufacturer;
	private List<InventoryReportItem> items = new ArrayList<>();

	public BigDecimal getTotalCost() {
		BigDecimal total = Constants.ZERO;
		for (InventoryReportItem item : items) {
			total = total.add(item.getTotalCost());
		}
		return total;
	}
	
}
