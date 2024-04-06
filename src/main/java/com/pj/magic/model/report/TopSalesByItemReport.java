package com.pj.magic.model.report;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TopSalesByItemReport {

	private Date fromDate;
	private Date toDate;
	private List<TopSalesByItemReportItem> items = new ArrayList<>();

}