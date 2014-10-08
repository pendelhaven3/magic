package com.pj.magic.model;

import java.util.ArrayList;
import java.util.List;

public class AreaInventoryReport {

	private Long id;
	private InventoryCheck parent;
	private String area;
	private String checker;
	private String doubleChecker;
	private List<AreaInventoryReportItem> items = new ArrayList<>();

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public InventoryCheck getParent() {
		return parent;
	}

	public void setParent(InventoryCheck parent) {
		this.parent = parent;
	}

	public String getArea() {
		return area;
	}

	public void setArea(String area) {
		this.area = area;
	}

	public String getChecker() {
		return checker;
	}

	public void setChecker(String checker) {
		this.checker = checker;
	}

	public String getDoubleChecker() {
		return doubleChecker;
	}

	public void setDoubleChecker(String doubleChecker) {
		this.doubleChecker = doubleChecker;
	}

	public List<AreaInventoryReportItem> getItems() {
		return items;
	}

	public void setItems(List<AreaInventoryReportItem> items) {
		this.items = items;
	}

}
