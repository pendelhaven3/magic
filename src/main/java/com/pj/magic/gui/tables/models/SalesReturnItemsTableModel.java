package com.pj.magic.gui.tables.models;

import java.util.ArrayList;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import org.springframework.stereotype.Component;

import com.pj.magic.gui.tables.SalesReturnItemsTable;
import com.pj.magic.model.SalesReturn;
import com.pj.magic.model.SalesReturnItem;
import com.pj.magic.util.FormatterUtil;

@Component
public class SalesReturnItemsTableModel extends AbstractTableModel {

	private static final String[] columnNames = {"Code", "Description", "Unit", "Quantity", 
		"Unit Price", "Amount", "To Return", "Return Amount"};
	
	private List<SalesReturnItem> items = new ArrayList<>();
	private boolean posted;
	
	@Override
	public int getRowCount() {
		return items.size();
	}

	@Override
	public int getColumnCount() {
		return columnNames.length;
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		SalesReturnItem item = items.get(rowIndex);
		switch (columnIndex) {
		case SalesReturnItemsTable.CODE_COLUMN_INDEX:
			return item.getItem().getProduct().getCode();
		case SalesReturnItemsTable.DESCRIPTION_COLUMN_INDEX:
			return item.getItem().getProduct().getDescription();
		case SalesReturnItemsTable.UNIT_COLUMN_INDEX:
			return item.getItem().getUnit();
		case SalesReturnItemsTable.QUANTITY_COLUMN_INDEX:
			return item.getItem().getQuantity();
		case SalesReturnItemsTable.UNIT_PRICE_COLUMN_INDEX:
			return FormatterUtil.formatAmount(item.getItem().getUnitPrice());
		case SalesReturnItemsTable.AMOUNT_COLUMN_INDEX:
			return item.getItem().getProduct().getCode();
		case SalesReturnItemsTable.RETURN_QUANTITY_COLUMN_INDEX:
			return item.getQuantity();
		case SalesReturnItemsTable.RETURN_AMOUNT_COLUMN_INDEX:
			return null;
		default:
			throw new RuntimeException("Fetching invalid column index: " + columnIndex);
		}
	}

	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		return !posted && columnIndex == SalesReturnItemsTable.RETURN_QUANTITY_COLUMN_INDEX;
	}
	
	public void setSalesReturn(SalesReturn salesReturn) {
		items = salesReturn.getItemsForEditing();
		posted = salesReturn.isPosted();
		fireTableDataChanged();
	}
	
	@Override
	public String getColumnName(int column) {
		return columnNames[column];
	}
	
}
