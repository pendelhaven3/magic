package com.pj.magic.model.search;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.pj.magic.model.Product;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BadStockCardInventoryReportCriteria {

	private Product product;
	private Date fromDate;
	private Date toDate;
	private List<String> transactionTypes = new ArrayList<>();
	private String unit;
	private Date fromDateTime;

}