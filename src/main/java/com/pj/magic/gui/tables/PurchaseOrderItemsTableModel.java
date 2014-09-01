package com.pj.magic.gui.tables;

import java.util.ArrayList;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.pj.magic.model.Product;
import com.pj.magic.model.PurchaseOrderItem;
import com.pj.magic.service.ProductService;
import com.pj.magic.service.PurchaseOrderService;

/*
 * [PJ 8/25/2014] 
 * An item can have a Product instance but an invalid code.
 * Product id is used instead to check for product code validity.
 */

@Component
public class PurchaseOrderItemsTableModel extends AbstractTableModel {
	
	private static final String[] columnNames = {"Code", "Description", "From Unit", "Qty", "To Unit", "Converted Qty"};
	
	@Autowired private ProductService productService;
	@Autowired private PurchaseOrderService purchaseOrderService;
	
	private List<PurchaseOrderItem> items = new ArrayList<>();
	
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
		PurchaseOrderItem item = items.get(rowIndex);
		switch (columnIndex) {
		case PurchaseOrderItemsTable.PRODUCT_CODE_COLUMN_INDEX:
			return (item.getProduct() != null) ? item.getProduct().getCode() : "";
		case PurchaseOrderItemsTable.PRODUCT_DESCRIPTION_COLUMN_INDEX:
			return (item.getProduct() != null) ? item.getProduct().getDescription() : "";
		case PurchaseOrderItemsTable.UNIT_COLUMN_INDEX:
			return StringUtils.defaultString(item.getUnit());
		case PurchaseOrderItemsTable.QUANTITY_COLUMN_INDEX:
			return (item.getQuantity() != null) ? item.getQuantity() : "";
		case PurchaseOrderItemsTable.COST_COLUMN_INDEX:
			return "";
		case PurchaseOrderItemsTable.ACTUAL_QUANTITY_COLUMN_INDEX:
			return "";
		default:
			throw new RuntimeException("Fetching invalid column index: " + columnIndex);
		}
	}
	
	@Override
	public String getColumnName(int columnIndex) {
		return columnNames[columnIndex];
	}

	public List<PurchaseOrderItem> getItems() {
		List<PurchaseOrderItem> items = new ArrayList<>();
		for (PurchaseOrderItem item : this.items) {
			if (item.isValid()) {
				items.add(item);
			}
		}
		return items;
	}
	
	public void setItems(List<PurchaseOrderItem> items) {
		this.items.clear();
		this.items.addAll(items);
		fireTableDataChanged();
	}
	
	public void addItem(PurchaseOrderItem item) {
		items.add(item);
		fireTableDataChanged();
	}
	
	@Override
	public void setValueAt(Object value, int rowIndex, int columnIndex) {
		PurchaseOrderItem item = items.get(rowIndex);
		String val = (String)value;
		switch (columnIndex) {
		case PurchaseOrderItemsTable.PRODUCT_CODE_COLUMN_INDEX:
			Product product = productService.findProductByCode(val);
			if (product == null) {
				product = new Product();
				product.setCode(val);
			}
			item.setProduct(product);
			break;
		case PurchaseOrderItemsTable.UNIT_COLUMN_INDEX:
			item.setUnit(val);
			break;
		case PurchaseOrderItemsTable.QUANTITY_COLUMN_INDEX:
			if (!StringUtils.isEmpty(val) && StringUtils.isNumeric(val)) {
				item.setQuantity(Integer.parseInt(val));
			} else {
				item.setQuantity(null);
			}
			break;
		}
		if (item.isValid()) {
			purchaseOrderService.save(item);
		}
		fireTableCellUpdated(rowIndex, columnIndex);
	}
	
	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		return columnIndex == PurchaseOrderItemsTable.PRODUCT_CODE_COLUMN_INDEX
				|| columnIndex == PurchaseOrderItemsTable.UNIT_COLUMN_INDEX
				|| columnIndex == PurchaseOrderItemsTable.QUANTITY_COLUMN_INDEX
				|| columnIndex == PurchaseOrderItemsTable.COST_COLUMN_INDEX;
	}
	
	public PurchaseOrderItem getRowItem(int rowIndex) {
		return items.get(rowIndex);
	}
	
	public void removeItem(int rowIndex) {
		PurchaseOrderItem item = items.remove(rowIndex);
		purchaseOrderService.delete(item);
		fireTableDataChanged();
	}
	
	public boolean hasItems() {
		return !items.isEmpty();
	}
	
	public void clearAndAddItem(PurchaseOrderItem item) {
		items.clear();
		addItem(item);
	}

	public boolean hasDuplicate(PurchaseOrderItem checkItem) {
		for (PurchaseOrderItem item : items) {
			if (item.equals(checkItem) && item != checkItem) {
				return true;
			}
		}
		return false;
	}
	
}