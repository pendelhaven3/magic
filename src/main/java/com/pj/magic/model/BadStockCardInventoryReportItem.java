package com.pj.magic.model;

import java.math.BigDecimal;
import java.util.Date;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BadStockCardInventoryReportItem {

	private Date postDate;
	private Long transactionNumber;
	private String supplierOrCustomerName;
	private String transactionType;
	private Integer addQuantity;
	private Integer lessQuantity;
	private BigDecimal currentCost;
	private BigDecimal amount;
	private String referenceNumber;
	private String unit;

}