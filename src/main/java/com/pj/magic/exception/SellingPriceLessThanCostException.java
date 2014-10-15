package com.pj.magic.exception;

import com.pj.magic.model.SalesRequisitionItem;

public class SellingPriceLessThanCostException extends RuntimeException {

	private SalesRequisitionItem item;

	public SellingPriceLessThanCostException(SalesRequisitionItem item) {
		this.item = item;
	}

	public SalesRequisitionItem getItem() {
		return item;
	}
	
}
