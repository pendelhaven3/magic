package com.pj.magic.gui.tables.models;

import java.util.ArrayList;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import org.springframework.stereotype.Component;

import com.pj.magic.model.InventoryCheckSummaryItem;

@Component
public class InventoryCheckSummaryTableModel extends AbstractTableModel {

	private static final String[] columnNames = 
		{"Code", "Description", "Unit", "Existing Qty", "Counted Qty", "Difference"};
	private static final int PRODUCT_CODE_COLUMN_INDEX = 0;
	private static final int PRODUCT_DESCRIPTION_COLUMN_INDEX = 1;
	private static final int UNIT_COLUMN_INDEX = 2;
	private static final int CURRENT_QUANTITY_COLUMN_INDEX = 3;
	private static final int COUNTED_QUANTITY_COLUMN_INDEX = 4;
	private static final int QUANTITY_DIFFERENCE_COLUMN_INDEX = 5;
	
	private List<InventoryCheckSummaryItem> items = new ArrayList<>();
	
	public void setItems(List<InventoryCheckSummaryItem> items) {
		this.items = items;
		fireTableDataChanged();
	}
	
	@Override
	public int getRowCount() {
		return items.size();
	}

	@Override
	public int getColumnCount() {
		return columnNames.length;
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		InventoryCheckSummaryItem item = items.get(rowIndex);
		switch (columnIndex) {
		case PRODUCT_CODE_COLUMN_INDEX:
			return item.getProduct().getCode();
		case PRODUCT_DESCRIPTION_COLUMN_INDEX:
			return item.getProduct().getDescription();
		case UNIT_COLUMN_INDEX:
			return item.getUnit();
		case CURRENT_QUANTITY_COLUMN_INDEX:
			return item.getProduct().getUnitQuantity(item.getUnit());
		case COUNTED_QUANTITY_COLUMN_INDEX:
			return item.getQuantity();
		case QUANTITY_DIFFERENCE_COLUMN_INDEX:
			return item.getQuantityDifference();
		default:
			throw new RuntimeException("Fetching invalid column index: " + columnIndex);
		}
	}

	@Override
	public String getColumnName(int column) {
		return columnNames[column];
	}
	
	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		return false;
	}
	
}
