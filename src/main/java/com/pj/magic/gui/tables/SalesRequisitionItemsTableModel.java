package com.pj.magic.gui.tables;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.pj.magic.model.Product;
import com.pj.magic.model.SalesRequisitionItem;
import com.pj.magic.service.ProductService;
import com.pj.magic.util.LabelUtil;

/*
 * [PJ 7/10/2014] 
 * An item can have a Product instance but an invalid code.
 * Product id is used instead to check for product code validity.
 */

@Component
public class SalesRequisitionItemsTableModel extends AbstractTableModel {
	
	@Autowired
	private ProductService productService;
	
	private String[] columnNames = {"Code", "Description", "Unit", "Qty", "Unit Price", "Amount"};
	private List<SalesRequisitionItem> items = new ArrayList<>();
	
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
		SalesRequisitionItem item = items.get(rowIndex);
		switch (columnIndex) {
		case SalesRequisitionItemsTable.PRODUCT_CODE_COLUMN_INDEX:
			return (item.getProduct() != null) ? item.getProduct().getCode() : "";
		case SalesRequisitionItemsTable.PRODUCT_DESCRIPTION_COLUMN_INDEX:
			return (item.getProduct() != null) ? item.getProduct().getDescription() : "";
		case SalesRequisitionItemsTable.UNIT_COLUMN_INDEX:
			return StringUtils.defaultString(item.getUnit());
		case SalesRequisitionItemsTable.QUANTITY_COLUMN_INDEX:
			return (item.getQuantity() != null) ? item.getQuantity() : "";
		case SalesRequisitionItemsTable.UNIT_PRICE_COLUMN_INDEX:
			BigDecimal unitPrice = item.getUnitPrice();
			return (unitPrice != null) ? LabelUtil.formatAmount(unitPrice) : "";
		case SalesRequisitionItemsTable.AMOUNT_COLUMN_INDEX:
			BigDecimal amount = item.getAmount();
			return (amount != null) ? LabelUtil.formatAmount(amount) : "";
		default:
			throw new RuntimeException("Fetching invalid column index: " + columnIndex);
		}
	}
	
	@Override
	public String getColumnName(int columnIndex) {
		return columnNames[columnIndex];
	}

	public List<SalesRequisitionItem> getItems() {
		List<SalesRequisitionItem> items = new ArrayList<>();
		for (SalesRequisitionItem item : this.items) {
			if (item.isValid()) {
				items.add(item);
			}
		}
		return items;
	}
	
	public void setItems(List<SalesRequisitionItem> items) {
		this.items.clear();
		this.items.addAll(items);
		fireTableDataChanged();
	}
	
	public void addNewRow() {
		items.add(new SalesRequisitionItem());
		fireTableDataChanged();
	}
	
	@Override
	public void setValueAt(Object value, int rowIndex, int columnIndex) {
		SalesRequisitionItem item = items.get(rowIndex);
		String val = (String)value;
		switch (columnIndex) {
		case SalesRequisitionItemsTable.PRODUCT_CODE_COLUMN_INDEX:
			Product product = productService.findProductByCode(val);
			if (product == null) {
				product = new Product();
				product.setCode(val);
			}
			item.setProduct(product);
			break;
		case SalesRequisitionItemsTable.UNIT_COLUMN_INDEX:
			item.setUnit(val);
			break;
		case SalesRequisitionItemsTable.QUANTITY_COLUMN_INDEX:
			if (!StringUtils.isEmpty(val) && StringUtils.isNumeric(val)) {
				item.setQuantity(Integer.parseInt(val));
			}
			break;
		default:
			throw new RuntimeException("Setting invalid column index: " + columnIndex);
		}
		fireTableRowsUpdated(rowIndex, rowIndex);
//		fireTableCellUpdated(rowIndex, columnIndex);
	}
	
	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		return columnIndex == SalesRequisitionItemsTable.PRODUCT_CODE_COLUMN_INDEX
				|| columnIndex == SalesRequisitionItemsTable.QUANTITY_COLUMN_INDEX
				|| columnIndex == SalesRequisitionItemsTable.UNIT_COLUMN_INDEX;
	}
	
	public SalesRequisitionItem getRowItem(int rowIndex) {
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
		items.add(new SalesRequisitionItem());
		fireTableDataChanged();
	}

	public boolean hasDuplicate(SalesRequisitionItem checkItem) {
		for (SalesRequisitionItem item : items) {
			if (item.equals(checkItem) && item != checkItem) {
				return true;
			}
		}
		return false;
	}
	
}