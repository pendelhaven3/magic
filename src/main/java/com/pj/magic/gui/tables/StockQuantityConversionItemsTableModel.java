package com.pj.magic.gui.tables;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.pj.magic.model.Product;
import com.pj.magic.model.StockQuantityConversionItem;
import com.pj.magic.service.ProductService;
import com.pj.magic.service.StockQuantityConversionService;
import com.pj.magic.util.FormatterUtil;

/*
 * [PJ 8/25/2014] 
 * An item can have a Product instance but an invalid code.
 * Product id is used instead to check for product code validity.
 */

@Component
public class StockQuantityConversionItemsTableModel extends AbstractTableModel {
	
	private static final String[] columnNames = {"Code", "Description", "From Unit", "Qty", "To Unit", "Converted Qty"};
	
	@Autowired private ProductService productService;
	@Autowired private StockQuantityConversionService stockQuantityConversionService;
	
	private List<StockQuantityConversionItem> items = new ArrayList<>();
	
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
		StockQuantityConversionItem item = items.get(rowIndex);
		switch (columnIndex) {
		case StockQuantityConversionItemsTable.PRODUCT_CODE_COLUMN_INDEX:
			return (item.getProduct() != null) ? item.getProduct().getCode() : "";
		case StockQuantityConversionItemsTable.PRODUCT_DESCRIPTION_COLUMN_INDEX:
			return (item.getProduct() != null) ? item.getProduct().getDescription() : "";
		case StockQuantityConversionItemsTable.TO_UNIT_COLUMN_INDEX:
			return StringUtils.defaultString(item.getUnit());
		case StockQuantityConversionItemsTable.QUANTITY_COLUMN_INDEX:
			return (item.getQuantity() != null) ? item.getQuantity() : "";
		case StockQuantityConversionItemsTable.FROM_UNIT_COLUMN_INDEX:
			BigDecimal unitPrice = item.getUnitPrice();
			return (unitPrice != null) ? FormatterUtil.formatAmount(unitPrice) : "";
		case StockQuantityConversionItemsTable.CONVERTED_QUANTITY_COLUMN_INDEX:
			BigDecimal amount = item.getAmount();
			return (amount != null) ? FormatterUtil.formatAmount(amount) : "";
		default:
			throw new RuntimeException("Fetching invalid column index: " + columnIndex);
		}
	}
	
	@Override
	public String getColumnName(int columnIndex) {
		return columnNames[columnIndex];
	}

	public List<StockQuantityConversionItem> getItems() {
		List<StockQuantityConversionItem> items = new ArrayList<>();
		for (StockQuantityConversionItem item : this.items) {
			if (item.isValid()) {
				items.add(item);
			}
		}
		return items;
	}
	
	public void setItems(List<StockQuantityConversionItem> items) {
		this.items.clear();
		this.items.addAll(items);
		fireTableDataChanged();
	}
	
	public void addItem(StockQuantityConversionItem item) {
		items.add(item);
		fireTableDataChanged();
	}
	
	@Override
	public void setValueAt(Object value, int rowIndex, int columnIndex) {
		StockQuantityConversionItem item = items.get(rowIndex);
		String val = (String)value;
		switch (columnIndex) {
		case StockQuantityConversionItemsTable.PRODUCT_CODE_COLUMN_INDEX:
			Product product = productService.findProductByCode(val);
			if (product == null) {
				product = new Product();
				product.setCode(val);
			}
			item.setProduct(product);
			break;
		case StockQuantityConversionItemsTable.TO_UNIT_COLUMN_INDEX:
			item.setUnit(val);
			break;
		case StockQuantityConversionItemsTable.CONVERTED_QUANTITY_COLUMN_INDEX:
			if (!StringUtils.isEmpty(val) && StringUtils.isNumeric(val)) {
				item.setQuantity(Integer.parseInt(val));
			} else {
				item.setQuantity(null);
			}
			break;
		}
		if (item.isValid()) {
			stockQuantityConversionService.save(item);
		}
		fireTableCellUpdated(rowIndex, columnIndex);
	}
	
	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		return columnIndex == StockQuantityConversionItemsTable.PRODUCT_CODE_COLUMN_INDEX
				|| columnIndex == StockQuantityConversionItemsTable.CONVERTED_QUANTITY_COLUMN_INDEX
				|| columnIndex == StockQuantityConversionItemsTable.TO_UNIT_COLUMN_INDEX;
	}
	
	public StockQuantityConversionItem getRowItem(int rowIndex) {
		return items.get(rowIndex);
	}
	
	public void removeItem(int rowIndex) {
		StockQuantityConversionItem item = items.remove(rowIndex);
		stockQuantityConversionService.delete(item);
		fireTableDataChanged();
	}
	
	public boolean hasItems() {
		return !items.isEmpty();
	}
	
	public void clearAndAddItem(StockQuantityConversionItem item) {
		items.clear();
		addItem(item);
	}

	public boolean hasDuplicate(StockQuantityConversionItem checkItem) {
		for (StockQuantityConversionItem item : items) {
			if (item.equals(checkItem) && item != checkItem) {
				return true;
			}
		}
		return false;
	}
	
}