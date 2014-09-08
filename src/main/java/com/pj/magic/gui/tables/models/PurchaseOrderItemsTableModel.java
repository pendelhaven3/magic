package com.pj.magic.gui.tables.models;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.pj.magic.gui.tables.PurchaseOrderItemsTable;
import com.pj.magic.gui.tables.rowitems.PurchaseOrderItemRowItem;
import com.pj.magic.model.PurchaseOrderItem;
import com.pj.magic.service.ProductService;
import com.pj.magic.service.PurchaseOrderService;
import com.pj.magic.util.FormatterUtil;

@Component
public class PurchaseOrderItemsTableModel extends AbstractTableModel {
	
	private static final String[] columnNames = {"Code", "Description", "Unit", "Quantity", "Cost", "Amount"};
	
	@Autowired private ProductService productService;
	@Autowired private PurchaseOrderService purchaseOrderService;
	
	private List<PurchaseOrderItemRowItem> rowItems = new ArrayList<>();
	private PurchaseOrderItemsTable table;
	
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
		PurchaseOrderItemRowItem rowItem = rowItems.get(rowIndex);
		switch (columnIndex) {
		case PurchaseOrderItemsTable.PRODUCT_CODE_COLUMN_INDEX:
			return rowItem.getProductCode();
		case PurchaseOrderItemsTable.PRODUCT_DESCRIPTION_COLUMN_INDEX:
			if (rowItem.getProduct() != null) {
				return rowItem.getProduct().getDescription();
			} else {
				return "";
			}
		case PurchaseOrderItemsTable.UNIT_COLUMN_INDEX:
			return StringUtils.defaultString(rowItem.getUnit());
		case PurchaseOrderItemsTable.QUANTITY_COLUMN_INDEX:
			return StringUtils.defaultString(rowItem.getQuantity());
		default:
			if (columnIndex == table.getCostColumnIndex()) {
				return StringUtils.defaultString(rowItem.getCost());
			} else if (columnIndex == table.getAmountColumnIndex()) {
				if (rowItem.isValid()) {
					return FormatterUtil.formatAmount(rowItem.getItem().getAmount());
				} else {
					return "";
				}
			} else {
				throw new RuntimeException("Fetching invalid column index: " + columnIndex);
			}
		}
	}
	
	@Override
	public String getColumnName(int columnIndex) {
		return columnNames[columnIndex];
	}

	public List<PurchaseOrderItem> getItems() {
		List<PurchaseOrderItem> items = new ArrayList<>();
		for (PurchaseOrderItemRowItem rowItem : this.rowItems) {
			if (rowItem.isValid()) {
				items.add(rowItem.getItem());
			}
		}
		return items;
	}
	
	public void setItems(List<PurchaseOrderItem> items) {
		this.rowItems.clear();
		for (PurchaseOrderItem item : items) {
			this.rowItems.add(new PurchaseOrderItemRowItem(item));
		}
		fireTableDataChanged();
	}
	
	public void addItem(PurchaseOrderItem item) {
		rowItems.add(new PurchaseOrderItemRowItem(item));
		fireTableDataChanged();
	}
	
	@Override
	public void setValueAt(Object value, int rowIndex, int columnIndex) {
		PurchaseOrderItemRowItem rowItem = rowItems.get(rowIndex);
		String val = (String)value;
		switch (columnIndex) {
		case PurchaseOrderItemsTable.PRODUCT_CODE_COLUMN_INDEX:
			rowItem.setProductCode(val);
			rowItem.setProduct(productService.findProductByCode(val));
			break;
		case PurchaseOrderItemsTable.UNIT_COLUMN_INDEX:
			rowItem.setUnit(val);
			break;
		case PurchaseOrderItemsTable.QUANTITY_COLUMN_INDEX:
			rowItem.setQuantity(val);
			break;
		default:
			if (columnIndex == table.getCostColumnIndex()) {
				rowItem.setCost(val);
			}
		}
		if (rowItem.isValid()) {
			PurchaseOrderItem item = rowItem.getItem();
			item.setProduct(rowItem.getProduct());
			item.setUnit(rowItem.getUnit());
			item.setQuantity(Integer.valueOf(rowItem.getQuantity()));
			item.setCost(new BigDecimal(rowItem.getCost()).setScale(2));
			purchaseOrderService.save(item);
			rowItem.setCost(item.getCost().toString());
		}
		fireTableCellUpdated(rowIndex, columnIndex);
	}
	
	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		return columnIndex == PurchaseOrderItemsTable.PRODUCT_CODE_COLUMN_INDEX
				|| columnIndex == PurchaseOrderItemsTable.UNIT_COLUMN_INDEX
				|| columnIndex == PurchaseOrderItemsTable.QUANTITY_COLUMN_INDEX
				|| columnIndex == table.getCostColumnIndex();
	}
	
	public PurchaseOrderItemRowItem getRowItem(int rowIndex) {
		return rowItems.get(rowIndex);
	}
	
	public void removeItem(int rowIndex) {
		PurchaseOrderItemRowItem wrapper = rowItems.remove(rowIndex);
		purchaseOrderService.delete(wrapper.getItem());
		fireTableDataChanged();
	}
	
	public boolean hasItems() {
		return !rowItems.isEmpty();
	}
	
	public void clearAndAddItem(PurchaseOrderItem item) {
		rowItems.clear();
		addItem(item);
	}

	public boolean hasDuplicate(PurchaseOrderItemRowItem checkItem) {
		for (PurchaseOrderItemRowItem rowItem : rowItems) {
			if (rowItem.equals(checkItem) && rowItem != checkItem) {
				return true;
			}
		}
		return false;
	}

	public boolean isValid(int rowIndex) {
		return rowItems.get(rowIndex).isValid();
	}
	
	public void setTable(PurchaseOrderItemsTable table) {
		this.table = table;
	}
	
}