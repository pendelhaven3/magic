package com.pj.magic.exception;

import com.pj.magic.model.SalesRequisitionItem;

public class NotEnoughStocksException extends RuntimeException {

	private SalesRequisitionItem item;

	public NotEnoughStocksException(SalesRequisitionItem item) {
		this.item = item;
	}
	
	public SalesRequisitionItem getItem() {
		return item;
	}
	
}
