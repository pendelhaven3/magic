package com.pj.magic;

import java.util.ArrayList;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import org.apache.commons.lang.StringUtils;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.pj.magic.model.Item;

@Component
@Scope("prototype")
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
			return (item.getProduct() != null) ? item.getProduct().getCode() : "";
		case ItemsTable.PRODUCT_DESCRIPTION_COLUMN_INDEX:
			return (item.getProduct() != null) ? item.getProduct().getDescription() : "";
		case ItemsTable.UNIT_COLUMN_INDEX:
			return StringUtils.defaultString(item.getUnit());
		case ItemsTable.QUANTITY_COLUMN_INDEX:
			return (item.getQuantity() != null) ? item.getQuantity() : "";
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

	public List<Item> getItems() {
		List<Item> items = new ArrayList<>();
		for (Item item : this.items) {
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
	
	public void addNewRow() {
		items.add(new Item());
		fireTableDataChanged();
	}
	
	@Override
	public void setValueAt(Object value, int rowIndex, int columnIndex) {
		Item item = items.get(rowIndex);
		String val = (String)value;
		switch (columnIndex) {
		case ItemsTable.PRODUCT_CODE_COLUMN_INDEX:
//			item.setProductCode(val);
			break;
		case ItemsTable.UNIT_COLUMN_INDEX:
			item.setUnit(val);
			break;
		case ItemsTable.QUANTITY_COLUMN_INDEX:
//			item.setQuantity(val);
			break;
		default:
			throw new RuntimeException("Setting invalid column index: " + columnIndex);
		}
		fireTableCellUpdated(rowIndex, columnIndex);
	}
	
	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		return columnIndex == ItemsTable.PRODUCT_CODE_COLUMN_INDEX
				|| columnIndex == ItemsTable.QUANTITY_COLUMN_INDEX
				|| columnIndex == ItemsTable.UNIT_COLUMN_INDEX;
	}
	
	public Item getRowItem(int rowIndex) {
		return items.get(rowIndex);
	}
	
	public void removeItem(int rowIndex) {
		items.remove(rowIndex);
		fireTableDataChanged();
	}
	
	public boolean hasItems() {
		return !items.isEmpty();
	}
	
	public void clearForNewInput() {
		items.clear();
		items.add(new Item());
	}
	
}
