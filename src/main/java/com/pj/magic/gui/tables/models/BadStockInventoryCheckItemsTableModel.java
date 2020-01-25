package com.pj.magic.gui.tables.models;

import java.util.ArrayList;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.pj.magic.gui.tables.BadStockInventoryCheckItemsTable;
import com.pj.magic.gui.tables.rowitems.BadStockInventoryCheckItemRowItem;
import com.pj.magic.model.BadStockInventoryCheck;
import com.pj.magic.model.BadStockInventoryCheckItem;
import com.pj.magic.service.BadStockInventoryCheckService;
import com.pj.magic.service.ProductService;

@Component
public class BadStockInventoryCheckItemsTableModel extends AbstractTableModel {
	
	private static final String[] columnNames = {"Code", "Description", "Unit", "Qty", "Qty Change"};
	
	@Autowired private ProductService productService;
	@Autowired private BadStockInventoryCheckService badStockInventoryCheckService;
	
	private List<BadStockInventoryCheckItemRowItem> rowItems = new ArrayList<>();
	private BadStockInventoryCheck badStockInventoryCheck;
	
	@Override
	public int getColumnCount() {
		return columnNames.length;
	}
	
	@Override
	public int getRowCount() {
		return rowItems.size();
	}
	
	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		BadStockInventoryCheckItemRowItem rowItem = rowItems.get(rowIndex);
		switch (columnIndex) {
		case BadStockInventoryCheckItemsTable.PRODUCT_CODE_COLUMN_INDEX:
			return rowItem.getProductCode();
		case BadStockInventoryCheckItemsTable.PRODUCT_DESCRIPTION_COLUMN_INDEX:
			return (rowItem.getProduct() != null) ? rowItem.getProduct().getDescription() : null;
		case BadStockInventoryCheckItemsTable.UNIT_COLUMN_INDEX:
			return rowItem.getUnit();
		case BadStockInventoryCheckItemsTable.QUANTITY_COLUMN_INDEX:
			return rowItem.getQuantity();
		case BadStockInventoryCheckItemsTable.QUANTITY_CHANGE_COLUMN_INDEX:
			return rowItem.getQuantityChange();
		default:
			return null;
		}
	}
	
	@Override
	public String getColumnName(int columnIndex) {
		return columnNames[columnIndex];
	}

	public List<BadStockInventoryCheckItem> getItems() {
		List<BadStockInventoryCheckItem> items = new ArrayList<>();
		for (BadStockInventoryCheckItemRowItem rowItem : this.rowItems) {
			if (rowItem.isValid()) {
				items.add(rowItem.getItem());
			}
		}
		return items;
	}
	
	public void setBadStockInventoryCheck(BadStockInventoryCheck badStockInventoryCheck) {
		this.badStockInventoryCheck = badStockInventoryCheck;
		setItems(badStockInventoryCheck.getItems());
	}
	
	public void setItems(List<BadStockInventoryCheckItem> items) {
		rowItems.clear();
		for (BadStockInventoryCheckItem item : items) {
			rowItems.add(new BadStockInventoryCheckItemRowItem(item));
		}
		fireTableDataChanged();
	}
	
	public void addItem(BadStockInventoryCheckItem item) {
		rowItems.add(new BadStockInventoryCheckItemRowItem(item));
		fireTableDataChanged();
	}
	
	@Override
	public void setValueAt(Object value, int rowIndex, int columnIndex) {
		BadStockInventoryCheckItemRowItem rowItem = rowItems.get(rowIndex);
		String val = (value instanceof Boolean) ? ((Boolean)value).toString() : (String)value;
		switch (columnIndex) {
		case BadStockInventoryCheckItemsTable.PRODUCT_CODE_COLUMN_INDEX:
			if (rowItem.getProduct() != null && rowItem.getProduct().getCode().equals(val)) {
				return;
			}
			rowItem.setProduct(productService.findProductByCode(val));
			rowItem.setUnit(null);
			break;
		case BadStockInventoryCheckItemsTable.UNIT_COLUMN_INDEX:
			if (!val.equals(rowItem.getUnit())) {
				rowItem.setUnit(val);
			}
			break;
		case BadStockInventoryCheckItemsTable.QUANTITY_COLUMN_INDEX:
			rowItem.setQuantity(Integer.valueOf(val));
			break;
		}
		
		// TODO: Save only when there is a change
		if (rowItem.isValid()) {
			BadStockInventoryCheckItem item = rowItem.getItem();
			item.setProduct(rowItem.getProduct());
			item.setUnit(rowItem.getUnit());
			item.setQuantity(Integer.valueOf(rowItem.getQuantity()));
			
			boolean newItem = (item.getId() == null);
			badStockInventoryCheckService.save(item);
			if (newItem) {
				item.getParent().getItems().add(item);
			}
		}
		fireTableCellUpdated(rowIndex, columnIndex);
	}
	
	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		if (badStockInventoryCheck.isPosted()) {
			return false;
		} else {
			BadStockInventoryCheckItemRowItem rowItem = rowItems.get(rowIndex);
			switch (columnIndex) {
			case BadStockInventoryCheckItemsTable.PRODUCT_CODE_COLUMN_INDEX:
				return true;
			case BadStockInventoryCheckItemsTable.UNIT_COLUMN_INDEX:
				return rowItem.getProduct() != null;
			case BadStockInventoryCheckItemsTable.QUANTITY_COLUMN_INDEX:
				return rowItem.getProduct() != null && !StringUtils.isEmpty(rowItem.getUnit());
			case BadStockInventoryCheckItemsTable.QUANTITY_CHANGE_COLUMN_INDEX:
				return false;
			default:
				return false;
			}
		}
	}
	
	public BadStockInventoryCheckItemRowItem getRowItem(int rowIndex) {
		return rowItems.get(rowIndex);
	}
	
	public void removeItem(int rowIndex) {
		BadStockInventoryCheckItemRowItem rowItem = rowItems.remove(rowIndex);
		badStockInventoryCheckService.delete(rowItem.getItem());
		fireTableDataChanged();
	}
	
	public boolean hasItems() {
		return !rowItems.isEmpty();
	}
	
	public void clearAndAddItem(BadStockInventoryCheckItem item) {
		rowItems.clear();
		addItem(item);
	}

	public boolean hasDuplicate(BadStockInventoryCheckItemRowItem checkItem) {
		for (BadStockInventoryCheckItemRowItem rowItem : rowItems) {
			if (rowItem.equals(checkItem) && rowItem != checkItem) {
				return true;
			}
		}
		return false;
	}

	public boolean isValid(int rowIndex) {
		return rowItems.get(rowIndex).isValid();
	}

	public Class<?> getColumnClass(int columnIndex) {
		switch (columnIndex) {
		case BadStockInventoryCheckItemsTable.QUANTITY_COLUMN_INDEX:
		case BadStockInventoryCheckItemsTable.QUANTITY_CHANGE_COLUMN_INDEX:
			return Long.class;
		default:
			return Object.class;
		}
	}

	public boolean hasDuplicate(String unit, BadStockInventoryCheckItemRowItem checkRowItem) {
		for (BadStockInventoryCheckItemRowItem rowItem : rowItems) {
			if (checkRowItem.getProduct().equals(rowItem.getProduct()) 
					&& unit.equals(rowItem.getUnit()) && rowItem != checkRowItem) {
				return true;
			}
		}
		return false;
	}

	public void reset(int row) {
		rowItems.get(row).reset();
		fireTableRowsUpdated(row, row);
	}

	public boolean hasNonBlankItem() {
		return hasItems() && rowItems.get(0).isValid();
	}
	
}