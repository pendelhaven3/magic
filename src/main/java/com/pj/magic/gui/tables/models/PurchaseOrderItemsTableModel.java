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
import com.pj.magic.model.PurchaseOrder;
import com.pj.magic.model.PurchaseOrderItem;
import com.pj.magic.service.ProductService;
import com.pj.magic.service.PurchaseOrderService;
import com.pj.magic.util.FormatterUtil;

@Component
public class PurchaseOrderItemsTableModel extends AbstractTableModel {
	
	private static final String[] columnNames = 
		{"Code", "Description", "Unit", "Sugg. Order", "Quantity", "Cost", "Amount"};
	private static final String[] orderedColumnNames = 
		{"Code", "Description", "Unit", "Sugg. Order", "Quantity", "Ordered", "Actual Qty", "Cost", "Amount"};
	
	@Autowired private ProductService productService;
	@Autowired private PurchaseOrderService purchaseOrderService;
	
	private List<PurchaseOrderItemRowItem> rowItems = new ArrayList<>();
	private PurchaseOrderItemsTable table;
	private boolean ordered;
	private boolean posted;
	
	@Override
	public int getColumnCount() {
		return ordered ? orderedColumnNames.length : columnNames.length;
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
		case PurchaseOrderItemsTable.SUGGESTED_ORDER_COLUMN_INDEX:
			return rowItem.getSuggestedOrder();
		case PurchaseOrderItemsTable.QUANTITY_COLUMN_INDEX:
			return StringUtils.defaultString(rowItem.getQuantity());
		default:
			if (columnIndex == table.getCostColumnIndex()) {
				if (rowItem.isValid()) {
					return FormatterUtil.formatAmount(rowItem.getItem().getCost());
				} else {
					return StringUtils.defaultString(rowItem.getCost());
				}
			} else if (columnIndex == table.getAmountColumnIndex()) {
				if (rowItem.isValid()) {
					return FormatterUtil.formatAmount(rowItem.getItem().getAmount());
				} else {
					return "";
				}
			} else if (columnIndex == table.getOrderedColumnIndex()) {
				return rowItem.getItem().isOrdered() ? "Yes" : "No";
			} else if (columnIndex == table.getActualQuantityColumnIndex()) {
				return StringUtils.defaultString(rowItem.getActualQuantity());
			} else {
				throw new RuntimeException("Fetching invalid column index: " + columnIndex);
			}
		}
	}
	
	@Override
	public String getColumnName(int columnIndex) {
		return ordered ? orderedColumnNames[columnIndex] : columnNames[columnIndex];
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
		setItems(items, true);
	}
	
	public void setItems(List<PurchaseOrderItem> items, boolean update) {
		this.rowItems.clear();
		for (PurchaseOrderItem item : items) {
			this.rowItems.add(new PurchaseOrderItemRowItem(item));
		}
		if (update) {
			fireTableDataChanged();
		}
	}
	
	public void addItem(PurchaseOrderItem item) {
		if (ordered) {
			item.setQuantity(0);
		}
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
			} else if (columnIndex == table.getActualQuantityColumnIndex()) {
				rowItem.setActualQuantity(val);
			}
		}
		// TODO: Save only when there is a change
		if (isCellEditable(rowIndex, columnIndex) && rowItem.isValid()) {
			PurchaseOrderItem item = rowItem.getItem();
			item.setProduct(rowItem.getProduct());
			item.setUnit(rowItem.getUnit());
			item.setQuantity(Integer.valueOf(rowItem.getQuantity()));
			if (!StringUtils.isEmpty(rowItem.getCost())) {
				item.setCost(rowItem.getCostAsBigDecimal());
			} else {
				BigDecimal originalCost = rowItem.getProduct().getUnitCost(rowItem.getUnit());
				item.setCost(originalCost);
				rowItem.setCost(FormatterUtil.formatAmount(originalCost));
			}
			if (!StringUtils.isEmpty(rowItem.getActualQuantity())) {
				item.setActualQuantity(Integer.valueOf(rowItem.getActualQuantity()));
			}
			purchaseOrderService.save(item);
			rowItem.setCost(item.getCost().toString());
		}
		fireTableCellUpdated(rowIndex, columnIndex);
	}
	
	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		if (posted) {
			return false;
		} else if (ordered) {
			if (getRowItem(rowIndex).getItem().isOrdered()) {
				return columnIndex == table.getActualQuantityColumnIndex()
						|| columnIndex == table.getCostColumnIndex();
			} else {
				return columnIndex == PurchaseOrderItemsTable.PRODUCT_CODE_COLUMN_INDEX
						|| columnIndex == PurchaseOrderItemsTable.UNIT_COLUMN_INDEX
						|| columnIndex == table.getActualQuantityColumnIndex()
						|| columnIndex == table.getCostColumnIndex();
			}
		} else {
			return columnIndex == PurchaseOrderItemsTable.PRODUCT_CODE_COLUMN_INDEX
					|| columnIndex == PurchaseOrderItemsTable.UNIT_COLUMN_INDEX
					|| columnIndex == PurchaseOrderItemsTable.QUANTITY_COLUMN_INDEX
					|| columnIndex == table.getCostColumnIndex();
		}
	}
	
	public PurchaseOrderItemRowItem getRowItem(int rowIndex) {
		return rowItems.get(rowIndex);
	}
	
	public void removeItem(int rowIndex) {
		PurchaseOrderItemRowItem rowItem = rowItems.remove(rowIndex);
		purchaseOrderService.delete(rowItem.getItem());
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

	public void setPurchaseOrder(PurchaseOrder purchaseOrder) {
		ordered = purchaseOrder.isOrdered();
		posted = purchaseOrder.isPosted();
		setItems(purchaseOrder.getItems(), false);
		fireTableStructureChanged();
	}
	
	public List<PurchaseOrderItemRowItem> getRowItems() {
		return rowItems;
	}

}