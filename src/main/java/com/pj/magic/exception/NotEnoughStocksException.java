package com.pj.magic.exception;

import com.pj.magic.model.SalesRequisitionItem;
import com.pj.magic.model.StockQuantityConversionItem;

public class NotEnoughStocksException extends RuntimeException {

	private SalesRequisitionItem salesRequisitionItem;
	private StockQuantityConversionItem stockQuantityConversionItem;

	public NotEnoughStocksException(SalesRequisitionItem item) {
		this.salesRequisitionItem = item;
	}

	public NotEnoughStocksException(StockQuantityConversionItem item) {
		this.stockQuantityConversionItem = item;
	}
	
	public SalesRequisitionItem getSalesRequisitionItem() {
		return salesRequisitionItem;
	}
	
	public StockQuantityConversionItem getStockQuantityConversionItem() {
		return stockQuantityConversionItem;
	}
	
}
