package com.pj.magic.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.pj.magic.util.FormatterUtil;

public class InventoryCheck {

	private Long id;
	private Date inventoryDate;
	private List<AreaInventoryReport> areaReports = new ArrayList<>();
	private boolean posted;

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
	
}
