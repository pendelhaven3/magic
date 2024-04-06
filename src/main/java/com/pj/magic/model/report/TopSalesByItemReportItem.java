package com.pj.magic.model.report;

import java.math.BigDecimal;

import com.pj.magic.model.Product;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TopSalesByItemReportItem {

	private Product product;
	private String unit;
	private BigDecimal amount;

}