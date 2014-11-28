package com.pj.magic.gui.tables;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.pj.magic.gui.tables.models.StockCardInventoryReportTableModel;
import com.pj.magic.model.StockCardInventoryReportItem;

@Component
public class StockCardInventoryReportTable extends MagicListTable {

	public static final int TRANSACTION_DATE_COLUMN_INDEX = 0;
	public static final int TRANSACTION_NUMBER_COLUMN_INDEX = 1;
	public static final int SUPPLIER_OR_CUSTOMER_NAME_COLUMN_INDEX = 2;
	public static final int TRANSACTION_TYPE_COLUMN_INDEX = 3;
	public static final int UNIT_COLUMN_INDEX = 4;
	public static final int ADD_QUANTITY_COLUMN_INDEX = 5;
	public static final int LESS_QUANTITY_COLUMN_INDEX = 6;
	public static final int CURRENT_COST_COLUMN_INDEX = 7;
	public static final int AMOUNT_COLUMN_INDEX = 8;
	public static final int REFERENCE_NUMBER_COLUMN_INDEX = 9;
	
	@Autowired private StockCardInventoryReportTableModel tableModel;
	
	@Autowired
	public StockCardInventoryReportTable(StockCardInventoryReportTableModel tableModel) {
		super(tableModel);
	}

	public void setItems(List<StockCardInventoryReportItem> items) {
		tableModel.setItems(items);
	}
	
}
