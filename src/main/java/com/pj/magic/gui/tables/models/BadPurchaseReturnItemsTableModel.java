package com.pj.magic.gui.tables.models;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.pj.magic.gui.tables.BadPurchaseReturnItemsTable;
import com.pj.magic.gui.tables.SalesRequisitionItemsTable;
import com.pj.magic.gui.tables.rowitems.BadPurchaseReturnItemRowItem;
import com.pj.magic.model.BadPurchaseReturn;
import com.pj.magic.model.BadPurchaseReturnItem;
import com.pj.magic.service.BadPurchaseReturnService;
import com.pj.magic.service.ProductService;
import com.pj.magic.util.FormatterUtil;
import com.pj.magic.util.NumberUtil;

@Component
public class BadPurchaseReturnItemsTableModel extends AbstractTableModel {
	
	private static final String[] columnNames = {"Code", "Description", "Unit", "Qty", "Unit Price", "Amount"};
	
	@Autowired private ProductService productService;
	@Autowired private BadPurchaseReturnService badStockReturnService;
	
	private List<BadPurchaseReturnItemRowItem> rowItems = new ArrayList<>();
	private BadPurchaseReturn badStockReturn;
	
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
		BadPurchaseReturnItemRowItem rowItem = rowItems.get(rowIndex);
		switch (columnIndex) {
		case BadPurchaseReturnItemsTable.PRODUCT_CODE_COLUMN_INDEX:
			return rowItem.getProductCode();
		case BadPurchaseReturnItemsTable.PRODUCT_DESCRIPTION_COLUMN_INDEX:
			return (rowItem.getProduct() != null) ? rowItem.getProduct().getDescription() : null;
		case BadPurchaseReturnItemsTable.UNIT_COLUMN_INDEX:
			return rowItem.getUnit();
		case BadPurchaseReturnItemsTable.QUANTITY_COLUMN_INDEX:
			return rowItem.getQuantity();
		case BadPurchaseReturnItemsTable.UNIT_COST_COLUMN_INDEX:
			BigDecimal unitCost = rowItem.getUnitCost();
			return (unitCost != null) ? FormatterUtil.formatAmount(unitCost) : "";
		case BadPurchaseReturnItemsTable.AMOUNT_COLUMN_INDEX:
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

	public List<BadPurchaseReturnItem> getItems() {
		List<BadPurchaseReturnItem> items = new ArrayList<>();
		for (BadPurchaseReturnItemRowItem rowItem : this.rowItems) {
			if (rowItem.isValid()) {
				items.add(rowItem.getItem());
			}
		}
		return items;
	}
	
	public void setBadPurchaseReturn(BadPurchaseReturn badStockReturn) {
		this.badStockReturn = badStockReturn;
		setItems(badStockReturn.getItems());
	}
	
	public void setItems(List<BadPurchaseReturnItem> items) {
		rowItems.clear();
		for (BadPurchaseReturnItem item : items) {
			rowItems.add(new BadPurchaseReturnItemRowItem(item));
		}
		fireTableDataChanged();
	}
	
	public void addItem(BadPurchaseReturnItem item) {
		rowItems.add(new BadPurchaseReturnItemRowItem(item));
		fireTableDataChanged();
	}
	
	@Override
	public void setValueAt(Object value, int rowIndex, int columnIndex) {
		BadPurchaseReturnItemRowItem rowItem = rowItems.get(rowIndex);
		String val = (String)value;
		switch (columnIndex) {
		case BadPurchaseReturnItemsTable.PRODUCT_CODE_COLUMN_INDEX:
			if (rowItem.getProduct() != null && rowItem.getProduct().getCode().equals(val)) {
				return;
			}
			rowItem.setProduct(productService.findProductByCode(val));
			rowItem.setUnit(null);
			break;
		case BadPurchaseReturnItemsTable.UNIT_COLUMN_INDEX:
			rowItem.setUnit(val);
			break;
		case BadPurchaseReturnItemsTable.QUANTITY_COLUMN_INDEX:
			rowItem.setQuantity(Integer.valueOf(val));
			break;
		case BadPurchaseReturnItemsTable.UNIT_COST_COLUMN_INDEX:
			rowItem.setUnitCost(NumberUtil.toBigDecimal(val));
			break;
		}
		// TODO: Save only when there is a change
		if (rowItem.isValid()) {
			BadPurchaseReturnItem item = rowItem.getItem();
			item.setProduct(rowItem.getProduct());
			item.setUnit(rowItem.getUnit());
			item.setQuantity(Integer.valueOf(rowItem.getQuantity()));
			if (item.getUnitCost() != null) {
				item.setUnitCost(rowItem.getUnitCost());
			} else {
				// TODO: Use final cost of supplier!
				BigDecimal originalUnitPrice = rowItem.getProduct().getFinalCost(rowItem.getUnit());
				item.setUnitCost(originalUnitPrice);
				rowItem.setUnitCost(originalUnitPrice);
			}
			
			boolean newItem = (item.getId() == null);
			badStockReturnService.save(item);
			if (newItem) {
				item.getParent().getItems().add(item);
			}
		}
		fireTableCellUpdated(rowIndex, columnIndex);
	}
	
	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		if (badStockReturn.isPosted()) {
			return false;
		} else {
			BadPurchaseReturnItemRowItem rowItem = rowItems.get(rowIndex);
			switch (columnIndex) {
			case SalesRequisitionItemsTable.PRODUCT_CODE_COLUMN_INDEX:
				return true;
			case SalesRequisitionItemsTable.UNIT_COLUMN_INDEX:
				return rowItem.getProduct() != null;
			case SalesRequisitionItemsTable.QUANTITY_COLUMN_INDEX:
				return rowItem.getProduct() != null && !StringUtils.isEmpty(rowItem.getUnit());
			case SalesRequisitionItemsTable.UNIT_PRICE_COLUMN_INDEX:
				return rowItem.getProduct() != null && !StringUtils.isEmpty(rowItem.getUnit())
					&& rowItem.getQuantity() != null;
			default:
				return false;
			}
		}
	}
	
	public BadPurchaseReturnItemRowItem getRowItem(int rowIndex) {
		return rowItems.get(rowIndex);
	}
	
	public void removeItem(int rowIndex) {
		BadPurchaseReturnItemRowItem rowItem = rowItems.remove(rowIndex);
		badStockReturnService.delete(rowItem.getItem());
		fireTableDataChanged();
	}
	
	public boolean hasItems() {
		return !rowItems.isEmpty();
	}
	
	public void clearAndAddItem(BadPurchaseReturnItem item) {
		rowItems.clear();
		addItem(item);
	}

	public boolean hasDuplicate(BadPurchaseReturnItemRowItem checkItem) {
		for (BadPurchaseReturnItemRowItem rowItem : rowItems) {
			if (rowItem.equals(checkItem) && rowItem != checkItem) {
				return true;
			}
		}
		return false;
	}

	public boolean isValid(int rowIndex) {
		return rowItems.get(rowIndex).isValid();
	}

	@Override
	public Class<?> getColumnClass(int columnIndex) {
		if (columnIndex == BadPurchaseReturnItemsTable.UNIT_COST_COLUMN_INDEX
				|| columnIndex == BadPurchaseReturnItemsTable.AMOUNT_COLUMN_INDEX) {
			return Number.class;
		} else {
			return Object.class;
		}
	}

	public boolean hasDuplicate(String unit, BadPurchaseReturnItemRowItem checkRowItem) {
		for (BadPurchaseReturnItemRowItem rowItem : rowItems) {
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