package com.pj.magic.model.search;

import java.util.Date;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BadStockReportSearchCriteria {

	private Boolean posted;
	private Long badStockReportNumber;
	private String location;
	private Date postDateFrom;
	private Date postDateTo;

}