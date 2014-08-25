package com.pj.magic.gui.tables;

import java.util.ArrayList;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

import com.pj.magic.model.StockQuantityConversion;

@Component
public class StockQuantityConversionsTableModel extends AbstractTableModel {

	private static final String[] COLUMN_NAMES = {"SQC No.", "Remarks"};
	
	private List<StockQuantityConversion> stockQuantityConversions = new ArrayList<>();
	
	@Override
	public int getRowCount() {
		return stockQuantityConversions.size();
	}

	@Override
	public int getColumnCount() {
		return COLUMN_NAMES.length;
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		StockQuantityConversion stockQuantityConversion = stockQuantityConversions.get(rowIndex);
		switch (columnIndex) {
		case StockQuantityConversionsTable.STOCK_QUANTITY_CONVERSION_NUMBER_COLUMN_INDEX:
			return stockQuantityConversion.getStockQuantityConversionNumber().toString();
		case StockQuantityConversionsTable.REMARKS_COLUMN_INDEX:
			return StringUtils.defaultString(stockQuantityConversion.getRemarks());
		default:
			throw new RuntimeException("Fetch invalid column index: " + columnIndex);
		}
	}
	
	@Override
	public String getColumnName(int column) {
		return COLUMN_NAMES[column];
	}
	
	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		return false;
	}
	
	public void setStockQuantityConversions(List<StockQuantityConversion> stockQuantityConversions) {
		this.stockQuantityConversions = stockQuantityConversions;
		fireTableDataChanged();
	}
	
	public StockQuantityConversion getStockQuantityConversion(int rowIndex) {
		return stockQuantityConversions.get(rowIndex);
	}

	public void remove(StockQuantityConversion stockQuantityConversion) {
		stockQuantityConversions.remove(stockQuantityConversion);
		fireTableDataChanged();
	}
	
}
