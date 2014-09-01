package com.pj.magic.gui.tables.models;

import java.util.ArrayList;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.pj.magic.gui.tables.SalesInvoiceItemsTable;
import com.pj.magic.model.SalesInvoiceItem;
import com.pj.magic.service.ProductService;
import com.pj.magic.util.FormatterUtil;

@Component
public class SalesInvoiceItemsTableModel extends AbstractTableModel {
	
	private static final String[] columnNames = {"Code", "Description", "Unit", "Qty", "Unit Price", "Amount"};
	
	@Autowired private ProductService productService;
	
	private List<SalesInvoiceItem> items = new ArrayList<>();
	
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
		SalesInvoiceItem item = items.get(rowIndex);
		switch (columnIndex) {
		case SalesInvoiceItemsTable.PRODUCT_CODE_COLUMN_INDEX:
			return item.getProduct().getCode();
		case SalesInvoiceItemsTable.PRODUCT_DESCRIPTION_COLUMN_INDEX:
			return item.getProduct().getDescription();
		case SalesInvoiceItemsTable.UNIT_COLUMN_INDEX:
			return item.getUnit();
		case SalesInvoiceItemsTable.QUANTITY_COLUMN_INDEX:
			return item.getQuantity();
		case SalesInvoiceItemsTable.UNIT_PRICE_COLUMN_INDEX:
			return FormatterUtil.formatAmount(item.getUnitPrice());
		case SalesInvoiceItemsTable.AMOUNT_COLUMN_INDEX:
			return FormatterUtil.formatAmount(item.getAmount());
		default:
			throw new RuntimeException("Fetching invalid column index: " + columnIndex);
		}
	}
	
	@Override
	public String getColumnName(int columnIndex) {
		return columnNames[columnIndex];
	}

	public void setItems(List<SalesInvoiceItem> items) {
		this.items.clear();
		this.items.addAll(items);
		fireTableDataChanged();
	}
	
	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		return false;
	}
	
	public SalesInvoiceItem getRowItem(int rowIndex) {
		return items.get(rowIndex);
	}
	
}