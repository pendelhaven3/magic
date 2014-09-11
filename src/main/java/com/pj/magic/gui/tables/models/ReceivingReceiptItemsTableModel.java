package com.pj.magic.gui.tables.models;

import java.util.ArrayList;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.pj.magic.gui.tables.ReceivingReceiptItemsTable;
import com.pj.magic.gui.tables.rowitems.ReceivingReceiptItemRowItem;
import com.pj.magic.model.ReceivingReceipt;
import com.pj.magic.model.ReceivingReceiptItem;
import com.pj.magic.service.ProductService;
import com.pj.magic.service.ReceivingReceiptService;
import com.pj.magic.util.FormatterUtil;

@Component
public class ReceivingReceiptItemsTableModel extends AbstractTableModel {
	
	private static final String[] columnNames = 
		{"Code", "Description", "Unit", "Quantity", "Cost", "Amount"};
	
	@Autowired private ProductService productService;
	@Autowired private ReceivingReceiptService receivingReceiptService;
	
	private List<ReceivingReceiptItemRowItem> rowItems = new ArrayList<>();
	
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
		ReceivingReceiptItemRowItem rowItem = rowItems.get(rowIndex);
		switch (columnIndex) {
		case ReceivingReceiptItemsTable.PRODUCT_CODE_COLUMN_INDEX:
			return rowItem.getProductCode();
		case ReceivingReceiptItemsTable.PRODUCT_DESCRIPTION_COLUMN_INDEX:
			if (rowItem.getProduct() != null) {
				return rowItem.getProduct().getDescription();
			} else {
				return "";
			}
		case ReceivingReceiptItemsTable.UNIT_COLUMN_INDEX:
			return StringUtils.defaultString(rowItem.getUnit());
		case ReceivingReceiptItemsTable.QUANTITY_COLUMN_INDEX:
			return StringUtils.defaultString(rowItem.getQuantity());
		default:
			throw new RuntimeException("Fetching invalid column index: " + columnIndex);
		}
	}
	
	@Override
	public String getColumnName(int columnIndex) {
		return columnNames[columnIndex];
	}

	public List<ReceivingReceiptItem> getItems() {
		List<ReceivingReceiptItem> items = new ArrayList<>();
		for (ReceivingReceiptItemRowItem rowItem : this.rowItems) {
			if (rowItem.isValid()) {
				items.add(rowItem.getItem());
			}
		}
		return items;
	}
	
	public void setItems(List<ReceivingReceiptItem> items) {
		setItems(items, true);
	}
	
	public void setItems(List<ReceivingReceiptItem> items, boolean update) {
		this.rowItems.clear();
		for (ReceivingReceiptItem item : items) {
			this.rowItems.add(new ReceivingReceiptItemRowItem(item));
		}
		if (update) {
			fireTableDataChanged();
		}
	}
	
	@Override
	public void setValueAt(Object value, int rowIndex, int columnIndex) {
		ReceivingReceiptItemRowItem rowItem = rowItems.get(rowIndex);
		String val = (String)value;
		switch (columnIndex) {
		case ReceivingReceiptItemsTable.PRODUCT_CODE_COLUMN_INDEX:
			rowItem.setProductCode(val);
			rowItem.setProduct(productService.findProductByCode(val));
			break;
		case ReceivingReceiptItemsTable.UNIT_COLUMN_INDEX:
			rowItem.setUnit(val);
			break;
		case ReceivingReceiptItemsTable.QUANTITY_COLUMN_INDEX:
			rowItem.setQuantity(val);
			break;
		}
		
		// TODO: Save only when there is a change
		if (isCellEditable(rowIndex, columnIndex) && rowItem.isValid()) {
			ReceivingReceiptItem item = rowItem.getItem();
			item.setProduct(rowItem.getProduct());
			item.setUnit(rowItem.getUnit());
			item.setQuantity(Integer.valueOf(rowItem.getQuantity()));
			item.setCost(rowItem.getCostAsBigDecimal());
			receivingReceiptService.save(item);
			rowItem.setCost(item.getCost().toString());
		}
		fireTableCellUpdated(rowIndex, columnIndex);
	}
	
	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		return false;
	}
	
	public ReceivingReceiptItemRowItem getRowItem(int rowIndex) {
		return rowItems.get(rowIndex);
	}
	
	public boolean hasItems() {
		return !rowItems.isEmpty();
	}
	
	public boolean isValid(int rowIndex) {
		return rowItems.get(rowIndex).isValid();
	}
	
	public void setReceivingReceipt(ReceivingReceipt receivingReceipt) {
		setItems(receivingReceipt.getItems());
	}

}