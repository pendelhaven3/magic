package com.pj.magic.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class InventoryCheck {

	private Long id;
	private Date inventoryDate;
	private List<AreaInventoryReport> areaReports = new ArrayList<>();

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

}
