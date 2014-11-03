package com.pj.magic.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.pj.magic.Constants;
import com.pj.magic.util.FormatterUtil;

public class InventoryCheck {

	private Long id;
	private Date inventoryDate;
	private List<AreaInventoryReport> areaReports = new ArrayList<>();
	private boolean posted;
	private List<InventoryCheckSummaryItem> summaryItems = new ArrayList<>();

	public InventoryCheck() {
		// default constructor
	}
	
	public InventoryCheck(Long id) {
		this.id = id;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Date getInventoryDate() {
		return inventoryDate;
	}

	public void setInventoryDate(Date inventoryDate) {
		this.inventoryDate = inventoryDate;
	}

	public List<AreaInventoryReport> getAreaReports() {
		return areaReports;
	}

	public void setAreaReports(List<AreaInventoryReport> areaReports) {
		this.areaReports = areaReports;
	}

	@Override
	public String toString() {
		return FormatterUtil.formatDate(inventoryDate);
	}

	public boolean isPosted() {
		return posted;
	}

	public void setPosted(boolean posted) {
		this.posted = posted;
	}

	public List<InventoryCheckSummaryItem> getSummaryItems() {
		return summaryItems;
	}

	public void setSummaryItems(List<InventoryCheckSummaryItem> summaryItems) {
		this.summaryItems = summaryItems;
	}

	public BigDecimal getTotalBeginningValue() {
		BigDecimal totalValue = Constants.ZERO;
		for (InventoryCheckSummaryItem summaryItem : summaryItems) {
			totalValue = totalValue.add(summaryItem.getBeginningValue());
		}
		return totalValue;
	}

	public BigDecimal getTotalActualValue() {
		BigDecimal totalValue = Constants.ZERO;
		for (InventoryCheckSummaryItem summaryItem : summaryItems) {
			totalValue = totalValue.add(summaryItem.getActualValue());
		}
		return totalValue;
	}
	
	public List<InventoryCheckSummaryItem> getSummaryItemsWithQuantitiesOnly() {
		return new ArrayList<InventoryCheckSummaryItem>(Collections2.filter(summaryItems, 
				new Predicate<InventoryCheckSummaryItem>() {

			@Override
			public boolean apply(InventoryCheckSummaryItem input) {
				return input.getQuantity() > 0;
			}
		}));
	}
	
}
