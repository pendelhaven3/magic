package com.pj.magic.gui.tables.models;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.pj.magic.gui.tables.AdjustmentInItemsTable;
import com.pj.magic.gui.tables.rowitems.AdjustmentInItemRowItem;
import com.pj.magic.model.AdjustmentInItem;
import com.pj.magic.service.AdjustmentInService;
import com.pj.magic.service.ProductService;
import com.pj.magic.util.FormatterUtil;

@Component
public class AdjustmentInItemsTableModel extends AbstractTableModel {
	
	private static final String[] columnNames = {"Code", "Description", "Unit", "Qty", "Unit Price", "Amount"};
	
	@Autowired private ProductService productService;
	@Autowired private AdjustmentInService adjustmentInService;
	
	private List<AdjustmentInItemRowItem> rowItems = new ArrayList<>();
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
		AdjustmentInItemRowItem rowItem = rowItems.get(rowIndex);
		switch (columnIndex) {
		case AdjustmentInItemsTable.PRODUCT_CODE_COLUMN_INDEX:
			return rowItem.getProductCode();
		case AdjustmentInItemsTable.PRODUCT_DESCRIPTION_COLUMN_INDEX:
			return (rowItem.getProduct() != null) ? rowItem.getProduct().getDescription() : null;
		case AdjustmentInItemsTable.UNIT_COLUMN_INDEX:
			return rowItem.getUnit();
		case AdjustmentInItemsTable.QUANTITY_COLUMN_INDEX:
			return rowItem.getQuantity();
		case AdjustmentInItemsTable.UNIT_PRICE_COLUMN_INDEX:
			BigDecimal unitPrice = rowItem.getUnitPrice();
			return (unitPrice != null) ? FormatterUtil.formatAmount(unitPrice) : "";
		case AdjustmentInItemsTable.AMOUNT_COLUMN_INDEX:
			BigDecimal amount = rowItem.getAmount();
			return (amount != null) ? FormatterUtil.formatAmount(amount) : "";
		default:
			throw new RuntimeException("Fetching invalid column index: " + columnIndex);
		}
	}
	
	@Override
	public String getColumnName(int columnIndex) {
		return columnNames[columnIndex];
	}

	public List<AdjustmentInItem> getItems() {
		List<AdjustmentInItem> items = new ArrayList<>();
		for (AdjustmentInItemRowItem rowItem : this.rowItems) {
			if (rowItem.isValid()) {
				items.add(rowItem.getItem());
			}
		}
		return items;
	}
	
	public void setItems(List<AdjustmentInItem> items) {
		rowItems.clear();
		for (AdjustmentInItem item : items) {
			rowItems.add(new AdjustmentInItemRowItem(item));
		}
		fireTableDataChanged();
	}
	
	public void addItem(AdjustmentInItem item) {
		rowItems.add(new AdjustmentInItemRowItem(item));
		fireTableDataChanged();
	}
	
	@Override
	public void setValueAt(Object value, int rowIndex, int columnIndex) {
		AdjustmentInItemRowItem rowItem = rowItems.get(rowIndex);
		String val = (String)value;
		switch (columnIndex) {
		case AdjustmentInItemsTable.PRODUCT_CODE_COLUMN_INDEX:
			rowItem.setProductCode(val);
			rowItem.setProduct(productService.findProductByCode(val));
			break;
		case AdjustmentInItemsTable.UNIT_COLUMN_INDEX:
			rowItem.setUnit(val);
			break;
		case AdjustmentInItemsTable.QUANTITY_COLUMN_INDEX:
			rowItem.setQuantity(val);
			break;
		}
		// TODO: Save only when there is a change
		if (rowItem.isValid()) {
			AdjustmentInItem item = rowItem.getItem();
			item.setProduct(rowItem.getProduct());
			item.setUnit(rowItem.getUnit());
			item.setQuantity(Integer.valueOf(rowItem.getQuantity()));
			adjustmentInService.save(item);
		}
		fireTableCellUpdated(rowIndex, columnIndex);
	}
	
	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		if (!editable) {
			return false;
		} else {
			return columnIndex == AdjustmentInItemsTable.PRODUCT_CODE_COLUMN_INDEX
					|| columnIndex == AdjustmentInItemsTable.QUANTITY_COLUMN_INDEX
					|| columnIndex == AdjustmentInItemsTable.UNIT_COLUMN_INDEX;
		}
	}
	
	public AdjustmentInItemRowItem getRowItem(int rowIndex) {
		return rowItems.get(rowIndex);
	}
	
	public void removeItem(int rowIndex) {
		AdjustmentInItemRowItem rowItem = rowItems.remove(rowIndex);
		adjustmentInService.delete(rowItem.getItem());
		fireTableDataChanged();
	}
	
	public boolean hasItems() {
		return !rowItems.isEmpty();
	}
	
	public void clearAndAddItem(AdjustmentInItem item) {
		rowItems.clear();
		addItem(item);
	}

	public boolean hasDuplicate(AdjustmentInItemRowItem checkItem) {
		for (AdjustmentInItemRowItem rowItem : rowItems) {
			if (rowItem.equals(checkItem) && rowItem != checkItem) {
				return true;
			}
		}
		return false;
	}

	public boolean isValid(int rowIndex) {
		return rowItems.get(rowIndex).isValid();
	}
	
	public void setEditable(boolean editable) {
		this.editable = editable;
	}
	
}