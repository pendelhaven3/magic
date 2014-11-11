package com.pj.magic.gui.tables.models;

import java.util.ArrayList;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import org.springframework.stereotype.Component;

import com.pj.magic.gui.tables.StockCardInventoryReportTable;
import com.pj.magic.model.StockCardInventoryReportItem;
import com.pj.magic.util.FormatterUtil;

@Component
public class StockCardInventoryReportTableModel extends AbstractTableModel {

	private static final String[] columnNames = 
		{"Trans. Date", "Trans. No.", "Supplier/Customer", "Trans. Type", "Add Qty", "Less Qty", "Cost / Price", "Amount", "Ref. No."};
	
	private List<StockCardInventoryReportItem> items = new ArrayList<>();
	
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
		StockCardInventoryReportItem item = items.get(rowIndex);
		switch (columnIndex) {
		case StockCardInventoryReportTable.TRANSACTION_DATE_COLUMN_INDEX:
			return FormatterUtil.formatDate(item.getTransactionDate());
		case StockCardInventoryReportTable.TRANSACTION_NUMBER_COLUMN_INDEX:
			return item.getTransactionNumber();
		case StockCardInventoryReportTable.SUPPLIER_OR_CUSTOMER_NAME_COLUMN_INDEX:
			return item.getSupplierOrCustomerName();
		case StockCardInventoryReportTable.TRANSACTION_TYPE_COLUMN_INDEX:
			return item.getTransactionType();
		case StockCardInventoryReportTable.ADD_QUANTITY_COLUMN_INDEX:
			return item.getAddQuantity();
		case StockCardInventoryReportTable.LESS_QUANTITY_COLUMN_INDEX:
			return item.getLessQuantity();
		case StockCardInventoryReportTable.CURRENT_COST_COLUMN_INDEX:
			return FormatterUtil.formatAmount(item.getCurrentCostOrSellingPrice());
		case StockCardInventoryReportTable.AMOUNT_COLUMN_INDEX:
			return FormatterUtil.formatAmount(item.getAmount());
		case StockCardInventoryReportTable.REFERENCE_NUMBER_COLUMN_INDEX:
			return item.getReferenceNumber();
		default:
			throw new RuntimeException("Fetch invalid column index: " + columnIndex);
		}
	}

	public void setItems(List<StockCardInventoryReportItem> items) {
		this.items.clear();
		this.items.addAll(items);
		fireTableDataChanged();
	}

	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		return false;
	}
	
	@Override
	public Class<?> getColumnClass(int columnIndex) {
		switch (columnIndex) {
		case StockCardInventoryReportTable.CURRENT_COST_COLUMN_INDEX:
		case StockCardInventoryReportTable.AMOUNT_COLUMN_INDEX:
			return Number.class;
		default:
			return Object.class;
		}
	}
	
	@Override
	public String getColumnName(int column) {
		return columnNames[column];
	}
	
}
