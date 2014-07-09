package com.pj.magic;

import java.util.ArrayList;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import com.pj.magic.model.Item;

public class ItemsTableModel extends AbstractTableModel {
	
	private static final long serialVersionUID = 3175876080507017536L;
	
	private String[] columnNames = {"Code", "Description", "Unit", "Qty", "Unit Price", "Amount"};
	private List<Item> items = new ArrayList<>();
	
	@Override
	public int getColumnCount() {
		return columnNames.length;
	}
	
	@Override
	public int getRowCount() {
		return items.size();
	}
	
	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		Item item = items.get(rowIndex);
		switch (columnIndex) {
		case ItemsTable.PRODUCT_CODE_COLUMN_INDEX:
			return item.getProductCode();
		case ItemsTable.PRODUCT_DESCRIPTION_COLUMN_INDEX:
			return "";
		case ItemsTable.UNIT_COLUMN_INDEX:
			return item.getUnit();
		case ItemsTable.QUANTITY_COLUMN_INDEX:
			return item.getQuantity();
		case ItemsTable.UNIT_PRICE_COLUMN_INDEX:
			return "";
		case ItemsTable.AMOUNT_COLUMN_INDEX:
			return "";
		default:
			throw new RuntimeException("Fetching invalid column index: " + columnIndex);
		}
	}
	
	@Override
	public String getColumnName(int columnIndex) {
		return columnNames[columnIndex];
	}

	// TODO: Combine ItemsTableModel and BlankItemsTableModel
	public List<Item> getItems() {
		List<Item> items = new ArrayList<>();
		for (int i = 0; i < getRowCount(); i++) {
			Item item = new Item();
			item.setProductCode((String)getValueAt(i, ItemsTable.PRODUCT_CODE_COLUMN_INDEX));
			item.setUnit((String)getValueAt(i, ItemsTable.UNIT_COLUMN_INDEX));
			item.setQuantity((String)getValueAt(i, ItemsTable.QUANTITY_COLUMN_INDEX));
			if (item.isValid()) {
				items.add(item);
			}
		}
		return items;
	}
	
	public void setItems(List<Item> items) {
		this.items.clear();
		this.items.addAll(items);
		fireTableDataChanged();
	}
	
}
