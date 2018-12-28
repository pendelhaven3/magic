package com.pj.magic.gui.tables.models;

import java.util.ArrayList;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.pj.magic.gui.tables.AdjustmentInItemsTable;
import com.pj.magic.gui.tables.BadStockAdjustmentInItemsTable;
import com.pj.magic.gui.tables.rowitems.BadStockAdjustmentInItemRowItem;
import com.pj.magic.model.BadStockAdjustmentIn;
import com.pj.magic.model.BadStockAdjustmentInItem;
import com.pj.magic.service.BadStockAdjustmentInService;
import com.pj.magic.service.ProductService;

@Component
public class BadStockAdjustmentInItemsTableModel extends AbstractTableModel {
	
	private static final String[] columnNames = {"Code", "Description", "Unit", "Qty"};
	
	@Autowired
	private ProductService productService;
	
	@Autowired
	private BadStockAdjustmentInService badStockAdjustmentInService;
	
	private List<BadStockAdjustmentInItemRowItem> rowItems = new ArrayList<>();
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
	    BadStockAdjustmentInItemRowItem rowItem = rowItems.get(rowIndex);
		switch (columnIndex) {
		case AdjustmentInItemsTable.PRODUCT_CODE_COLUMN_INDEX:
			return rowItem.getProductCode();
		case AdjustmentInItemsTable.PRODUCT_DESCRIPTION_COLUMN_INDEX:
			return (rowItem.getProduct() != null) ? rowItem.getProduct().getDescription() : null;
		case AdjustmentInItemsTable.UNIT_COLUMN_INDEX:
			return rowItem.getUnit();
		case AdjustmentInItemsTable.QUANTITY_COLUMN_INDEX:
			return rowItem.getQuantity();
		default:
		    return null;
		}
	}
	
	@Override
	public String getColumnName(int columnIndex) {
		return columnNames[columnIndex];
	}

	public List<BadStockAdjustmentInItem> getItems() {
		List<BadStockAdjustmentInItem> items = new ArrayList<>();
		for (BadStockAdjustmentInItemRowItem rowItem : this.rowItems) {
			if (rowItem.isValid()) {
				items.add(rowItem.getItem());
			}
		}
		return items;
	}
	
	public void setItems(List<BadStockAdjustmentInItem> items) {
		rowItems.clear();
		for (BadStockAdjustmentInItem item : items) {
			rowItems.add(new BadStockAdjustmentInItemRowItem(item));
		}
		fireTableDataChanged();
	}
	
	public void addItem(BadStockAdjustmentInItem item) {
		rowItems.add(new BadStockAdjustmentInItemRowItem(item));
		fireTableDataChanged();
	}
	
	@Override
	public void setValueAt(Object value, int rowIndex, int columnIndex) {
	    BadStockAdjustmentInItemRowItem rowItem = rowItems.get(rowIndex);
		String val = (String)value;
		switch (columnIndex) {
		case AdjustmentInItemsTable.PRODUCT_CODE_COLUMN_INDEX:
			if (rowItem.getProduct() != null && rowItem.getProduct().getCode().equals(val)) {
				return;
			}
			rowItem.setProduct(productService.findProductByCode(val));
			rowItem.setUnit(null);
			break;
		case AdjustmentInItemsTable.UNIT_COLUMN_INDEX:
			rowItem.setUnit(val);
			break;
		case AdjustmentInItemsTable.QUANTITY_COLUMN_INDEX:
			rowItem.setQuantity(Integer.valueOf(val));
			break;
		}
		// TODO: Save only when there is a change
		if (rowItem.isValid()) {
			BadStockAdjustmentInItem item = rowItem.getItem();
			item.setProduct(rowItem.getProduct());
			item.setUnit(rowItem.getUnit());
			item.setQuantity(Integer.valueOf(rowItem.getQuantity()));
			
			boolean newItem = (item.getId() == null);
			badStockAdjustmentInService.save(item);
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
			BadStockAdjustmentInItemRowItem rowItem = rowItems.get(rowIndex);
			switch (columnIndex) {
			case BadStockAdjustmentInItemsTable.PRODUCT_CODE_COLUMN_INDEX:
				return true;
			case BadStockAdjustmentInItemsTable.UNIT_COLUMN_INDEX:
				return rowItem.hasValidProduct();
			case BadStockAdjustmentInItemsTable.QUANTITY_COLUMN_INDEX:
				return rowItem.hasValidProduct() && rowItem.hasValidUnit();
			default:
				return false;
			}
		}
	}
	
	public BadStockAdjustmentInItemRowItem getRowItem(int rowIndex) {
		return rowItems.get(rowIndex);
	}
	
	public void removeItem(int rowIndex) {
	    BadStockAdjustmentInItemRowItem rowItem = rowItems.remove(rowIndex);
		badStockAdjustmentInService.delete(rowItem.getItem());
		fireTableDataChanged();
	}
	
	public boolean hasItems() {
		return !rowItems.isEmpty();
	}
	
	public void clearAndAddItem(BadStockAdjustmentInItem item) {
		rowItems.clear();
		addItem(item);
	}

	public boolean hasDuplicate(BadStockAdjustmentInItemRowItem checkItem) {
		for (BadStockAdjustmentInItemRowItem rowItem : rowItems) {
			if (rowItem.equals(checkItem) && rowItem != checkItem) {
				return true;
			}
		}
		return false;
	}

	public boolean isValid(int rowIndex) {
		return rowItems.get(rowIndex).isValid();
	}

	public boolean hasDuplicate(String unit, BadStockAdjustmentInItemRowItem checkRowItem) {
		for (BadStockAdjustmentInItemRowItem rowItem : rowItems) {
			if (checkRowItem.getProduct().equals(rowItem.getProduct()) 
					&& unit.equals(rowItem.getUnit()) && rowItem != checkRowItem) {
				return true;
			}
		}
		return false;
	}

	public void setAdjustmentIn(BadStockAdjustmentIn adjustmentIn) {
		setItems(adjustmentIn.getItems());
		editable = !adjustmentIn.isPosted();
	}

	public void reset(int row) {
		rowItems.get(row).reset();
		fireTableRowsUpdated(row, row);
	}

	public boolean hasNonBlankItem() {
		return hasItems() && rowItems.get(0).isValid();
	}
	
}