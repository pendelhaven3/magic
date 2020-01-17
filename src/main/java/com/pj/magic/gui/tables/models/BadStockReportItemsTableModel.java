package com.pj.magic.gui.tables.models;

import java.util.ArrayList;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.pj.magic.gui.tables.BadStockReportItemsTable;
import com.pj.magic.gui.tables.rowitems.BadStockReportItemRowItem;
import com.pj.magic.model.BadStockReport;
import com.pj.magic.model.BadStockReportItem;
import com.pj.magic.service.BadStockReportService;
import com.pj.magic.service.ProductService;

@Component
public class BadStockReportItemsTableModel extends AbstractTableModel {
	
	private static final String[] columnNames = {"Code", "Description", "Unit", "Qty", "Force Conversion"};
	
	@Autowired private ProductService productService;
	@Autowired private BadStockReportService badStockReportService;
	
	private List<BadStockReportItemRowItem> rowItems = new ArrayList<>();
	private BadStockReport badStockReport;
	
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
		BadStockReportItemRowItem rowItem = rowItems.get(rowIndex);
		switch (columnIndex) {
		case BadStockReportItemsTable.PRODUCT_CODE_COLUMN_INDEX:
			return rowItem.getProductCode();
		case BadStockReportItemsTable.PRODUCT_DESCRIPTION_COLUMN_INDEX:
			return (rowItem.getProduct() != null) ? rowItem.getProduct().getDescription() : null;
		case BadStockReportItemsTable.UNIT_COLUMN_INDEX:
			return rowItem.getUnit();
		case BadStockReportItemsTable.QUANTITY_COLUMN_INDEX:
			return rowItem.getQuantity();
		case BadStockReportItemsTable.FORCE_CONVERSION_COLUMN_INDEX:
			return rowItem.isForceConversion();
		default:
			throw new RuntimeException("Fetching invalid column index: " + columnIndex);
		}
	}
	
	@Override
	public String getColumnName(int columnIndex) {
		return columnNames[columnIndex];
	}

	public List<BadStockReportItem> getItems() {
		List<BadStockReportItem> items = new ArrayList<>();
		for (BadStockReportItemRowItem rowItem : this.rowItems) {
			if (rowItem.isValid()) {
				items.add(rowItem.getItem());
			}
		}
		return items;
	}
	
	public void setBadStockReport(BadStockReport badStockReport) {
		this.badStockReport = badStockReport;
		setItems(badStockReport.getItems());
	}
	
	public void setItems(List<BadStockReportItem> items) {
		rowItems.clear();
		for (BadStockReportItem item : items) {
			rowItems.add(new BadStockReportItemRowItem(item));
		}
		fireTableDataChanged();
	}
	
	public void addItem(BadStockReportItem item) {
		rowItems.add(new BadStockReportItemRowItem(item));
		fireTableDataChanged();
	}
	
	@Override
	public void setValueAt(Object value, int rowIndex, int columnIndex) {
		BadStockReportItemRowItem rowItem = rowItems.get(rowIndex);
		String val = (value instanceof Boolean) ? ((Boolean)value).toString() : (String)value;
		switch (columnIndex) {
		case BadStockReportItemsTable.PRODUCT_CODE_COLUMN_INDEX:
			if (rowItem.getProduct() != null && rowItem.getProduct().getCode().equals(val)) {
				return;
			}
			rowItem.setProduct(productService.findProductByCode(val));
			rowItem.setUnit(null);
			break;
		case BadStockReportItemsTable.UNIT_COLUMN_INDEX:
			if (!val.equals(rowItem.getUnit())) {
				rowItem.setUnit(val);
			}
			break;
		case BadStockReportItemsTable.QUANTITY_COLUMN_INDEX:
			rowItem.setQuantity(Integer.valueOf(val));
			break;
		case BadStockReportItemsTable.FORCE_CONVERSION_COLUMN_INDEX:
			rowItem.setForceConversion(Boolean.valueOf(val));
			break;
		}
		// TODO: Save only when there is a change
		if (rowItem.isValid()) {
			BadStockReportItem item = rowItem.getItem();
			item.setProduct(rowItem.getProduct());
			item.setUnit(rowItem.getUnit());
			item.setQuantity(Integer.valueOf(rowItem.getQuantity()));
			item.setForceConversion(rowItem.isForceConversion());
			
			boolean newItem = (item.getId() == null);
			badStockReportService.save(item);
			if (newItem) {
				item.getParent().getItems().add(item);
			}
		}
		fireTableCellUpdated(rowIndex, columnIndex);
	}
	
	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		if (badStockReport.isPosted()) {
			return false;
		} else {
			BadStockReportItemRowItem rowItem = rowItems.get(rowIndex);
			switch (columnIndex) {
			case BadStockReportItemsTable.PRODUCT_CODE_COLUMN_INDEX:
			case BadStockReportItemsTable.FORCE_CONVERSION_COLUMN_INDEX:
				return true;
			case BadStockReportItemsTable.UNIT_COLUMN_INDEX:
				return rowItem.getProduct() != null;
			case BadStockReportItemsTable.QUANTITY_COLUMN_INDEX:
				return rowItem.getProduct() != null && !StringUtils.isEmpty(rowItem.getUnit());
			default:
				return false;
			}
		}
	}
	
	public BadStockReportItemRowItem getRowItem(int rowIndex) {
		return rowItems.get(rowIndex);
	}
	
	public void removeItem(int rowIndex) {
		BadStockReportItemRowItem rowItem = rowItems.remove(rowIndex);
		badStockReportService.delete(rowItem.getItem());
		fireTableDataChanged();
	}
	
	public boolean hasItems() {
		return !rowItems.isEmpty();
	}
	
	public void clearAndAddItem(BadStockReportItem item) {
		rowItems.clear();
		addItem(item);
	}

	public boolean hasDuplicate(BadStockReportItemRowItem checkItem) {
		for (BadStockReportItemRowItem rowItem : rowItems) {
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
		case BadStockReportItemsTable.QUANTITY_COLUMN_INDEX:
			return Long.class;
		case BadStockReportItemsTable.FORCE_CONVERSION_COLUMN_INDEX:
			return Boolean.class;
		default:
			return Object.class;
		}
	}

	public boolean hasDuplicate(String unit, BadStockReportItemRowItem checkRowItem) {
		for (BadStockReportItemRowItem rowItem : rowItems) {
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