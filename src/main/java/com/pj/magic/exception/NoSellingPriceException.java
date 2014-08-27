package com.pj.magic.exception;

import com.pj.magic.model.SalesRequisitionItem;
import com.pj.magic.model.StockQuantityConversionItem;

public class NoSellingPriceException extends RuntimeException {

	private SalesRequisitionItem salesRequisitionItem;
	private StockQuantityConversionItem stockQuantityConversionItem;

	public NoSellingPriceException(SalesRequisitionItem item) {
		this.salesRequisitionItem = item;
	}

	public NoSellingPriceException(StockQuantityConversionItem item) {
		this.stockQuantityConversionItem = item;
	}
	
	public SalesRequisitionItem getSalesRequisitionItem() {
		return salesRequisitionItem;
	}
	
	public StockQuantityConversionItem getStockQuantityConversionItem() {
		return stockQuantityConversionItem;
	}
	
}
