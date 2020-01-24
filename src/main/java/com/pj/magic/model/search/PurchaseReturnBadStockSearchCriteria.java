package com.pj.magic.model.search;

import java.util.Date;

import com.pj.magic.model.Supplier;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PurchaseReturnBadStockSearchCriteria {

	private Long purchaseReturnBadStockNumber;
	private Boolean posted;
	private Supplier supplier;
	private Date postDate;
	private Boolean paid;
	private Date paidDate;

}