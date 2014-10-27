package com.pj.magic.gui.tables;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.pj.magic.gui.tables.models.ProductCanvassItem;
import com.pj.magic.gui.tables.models.ProductCanvassTableModel;

@Component
public class ProductCanvassTable extends MagicListTable {

	public static final int RECEIVED_DATE_COLUMN_INDEX = 0;
	public static final int RECEIVING_RECEIPT_NUMBER_COLUMN_INDEX = 1;
	public static final int SUPPLIER_COLUMN_INDEX = 2;
	public static final int FINAL_COST_COLUMN_INDEX = 3;
	public static final int CURRENT_COST_COLUMN_INDEX = 4;
	public static final int REMARKS_COLUMN_INDEX = 5;
	
	@Autowired private ProductCanvassTableModel tableModel;
	
	@Autowired
	public ProductCanvassTable(ProductCanvassTableModel tableModel) {
		super(tableModel);
	}

	public void setItems(List<ProductCanvassItem> items) {
		tableModel.setItems(items);
	}
	
}
