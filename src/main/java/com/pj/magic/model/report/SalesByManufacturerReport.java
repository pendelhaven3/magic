package com.pj.magic.model.report;

import java.util.ArrayList;
import java.util.List;

import com.pj.magic.model.search.SalesByManufacturerReportCriteria;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SalesByManufacturerReport {

	private SalesByManufacturerReportCriteria criteria;
	private List<SalesByManufacturerReportItem> items = new ArrayList<>();

}