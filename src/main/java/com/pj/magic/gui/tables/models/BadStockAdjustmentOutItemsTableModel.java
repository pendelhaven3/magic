package com.pj.magic.gui.tables.models;

import java.util.ArrayList;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.pj.magic.gui.tables.BadStockAdjustmentOutItemsTable;
import com.pj.magic.gui.tables.rowitems.BadStockAdjustmentOutItemRowItem;
import com.pj.magic.model.BadStockAdjustmentOut;
import com.pj.magic.model.BadStockAdjustmentOutItem;
import com.pj.magic.service.BadStockAdjustmentOutService;
import com.pj.magic.service.ProductService;

@Component
public class BadStockAdjustmentOutItemsTableModel extends AbstractTableModel {
	
	private static final String[] columnNames = {"Code", "Description", "Unit", "Qty"};
	
	@Autowired
	private ProductService productService;
	
	@Autowired
	private BadStockAdjustmentOutService badStockAdjustmentOutService;
	
	private List<BadStockAdjustmentOutItemRowItem> rowItems = new ArrayList<>();
	private boolean editable;
	
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
	    BadStockAdjustmentOutItemRowItem rowItem = rowItems.get(rowIndex);
		switch (columnIndex) {
		case BadStockAdjustmentOutItemsTable.PRODUCT_CODE_COLUMN_INDEX:
			return rowItem.getProductCode();
		case BadStockAdjustmentOutItemsTable.PRODUCT_DESCRIPTION_COLUMN_INDEX:
			return (rowItem.getProduct() != null) ? rowItem.getProduct().getDescription() : null;
		case BadStockAdjustmentOutItemsTable.UNIT_COLUMN_INDEX:
			return rowItem.getUnit();
		case BadStockAdjustmentOutItemsTable.QUANTITY_COLUMN_INDEX:
			return rowItem.getQuantity();
		default:
		    return null;
		}
	}
	
	@Override
	public String getColumnName(int columnIndex) {
		return columnNames[columnIndex];
	}

	public List<BadStockAdjustmentOutItem> getItems() {
		List<BadStockAdjustmentOutItem> items = new ArrayList<>();
		for (BadStockAdjustmentOutItemRowItem rowItem : this.rowItems) {
			if (rowItem.isValid()) {
				items.add(rowItem.getItem());
			}
		}
		return items;
	}
	
	public void setItems(List<BadStockAdjustmentOutItem> items) {
		rowItems.clear();
		for (BadStockAdjustmentOutItem item : items) {
			rowItems.add(new BadStockAdjustmentOutItemRowItem(item));
		}
		fireTableDataChanged();
	}
	
	public void addItem(BadStockAdjustmentOutItem item) {
		rowItems.add(new BadStockAdjustmentOutItemRowItem(item));
		fireTableDataChanged();
	}
	
	@Override
	public void setValueAt(Object value, int rowIndex, int columnIndex) {
	    BadStockAdjustmentOutItemRowItem rowItem = rowItems.get(rowIndex);
		String val = (String)value;
		switch (columnIndex) {
		case BadStockAdjustmentOutItemsTable.PRODUCT_CODE_COLUMN_INDEX:
			if (rowItem.getProduct() != null && rowItem.getProduct().getCode().equals(val)) {
				return;
			}
			rowItem.setProduct(productService.findProductByCode(val));
			rowItem.setUnit(null);
			break;
		case BadStockAdjustmentOutItemsTable.UNIT_COLUMN_INDEX:
			rowItem.setUnit(val);
			break;
		case BadStockAdjustmentOutItemsTable.QUANTITY_COLUMN_INDEX:
			rowItem.setQuantity(Integer.valueOf(val));
			break;
		}
		// TODO: Save only when there is a change
		if (rowItem.isValid()) {
			BadStockAdjustmentOutItem item = rowItem.getItem();
			item.setProduct(rowItem.getProduct());
			item.setUnit(rowItem.getUnit());
			item.setQuantity(Integer.valueOf(rowItem.getQuantity()));
			
			boolean newItem = (item.getId() == null);
			badStockAdjustmentOutService.save(item);
			if (newItem) {
				item.getParent().getItems().add(item);
			}
		}
		fireTableCellUpdated(rowIndex, columnIndex);
	}
	
	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		if (!editable) {
			return false;
		} else {
			BadStockAdjustmentOutItemRowItem rowItem = rowItems.get(rowIndex);
			switch (columnIndex) {
			case BadStockAdjustmentOutItemsTable.PRODUCT_CODE_COLUMN_INDEX:
				return true;
			case BadStockAdjustmentOutItemsTable.UNIT_COLUMN_INDEX:
				return rowItem.hasValidProduct();
			case BadStockAdjustmentOutItemsTable.QUANTITY_COLUMN_INDEX:
				return rowItem.hasValidProduct() && rowItem.hasValidUnit();
			default:
				return false;
			}
		}
	}
	
	public BadStockAdjustmentOutItemRowItem getRowItem(int rowIndex) {
		return rowItems.get(rowIndex);
	}
	
	public void removeItem(int rowIndex) {
	    BadStockAdjustmentOutItemRowItem rowItem = rowItems.remove(rowIndex);
		badStockAdjustmentOutService.delete(rowItem.getItem());
		fireTableDataChanged();
	}
	
	public boolean hasItems() {
		return !rowItems.isEmpty();
	}
	
	public void clearAndAddItem(BadStockAdjustmentOutItem item) {
		rowItems.clear();
		addItem(item);
	}

	public boolean hasDuplicate(BadStockAdjustmentOutItemRowItem checkItem) {
		for (BadStockAdjustmentOutItemRowItem rowItem : rowItems) {
			if (rowItem.equals(checkItem) && rowItem != checkItem) {
				return true;
			}
		}
		return false;
	}

	public boolean isValid(int rowIndex) {
		return rowItems.get(rowIndex).isValid();
	}

	public boolean hasDuplicate(String unit, BadStockAdjustmentOutItemRowItem checkRowItem) {
		for (BadStockAdjustmentOutItemRowItem rowItem : rowItems) {
			if (checkRowItem.getProduct().equals(rowItem.getProduct()) 
					&& unit.equals(rowItem.getUnit()) && rowItem != checkRowItem) {
				return true;
			}
		}
		return false;
	}

	public void setAdjustmentOut(BadStockAdjustmentOut adjustmentOut) {
		setItems(adjustmentOut.getItems());
		editable = !adjustmentOut.isPosted();
	}

	public void reset(int row) {
		rowItems.get(row).reset();
		fireTableRowsUpdated(row, row);
	}

	public boolean hasNonBlankItem() {
		return hasItems() && rowItems.get(0).isValid();
	}
	
}