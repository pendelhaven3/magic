package com.pj.magic.gui.tables;

import javax.swing.table.AbstractTableModel;

import org.springframework.stereotype.Component;

import com.pj.magic.model.Product;
import com.pj.magic.model.UnitPrice;
import com.pj.magic.util.LabelUtil;

@Component
public class ProductUnitPricesTableModel extends AbstractTableModel {

	private static final int UNIT_COLUMN_INDEX = 0;
	private static final int QUANTITY_COLUMN_INDEX = 1;
	private static final int UNIT_PRICE_COLUMN_INDEX = 2;
	private static final String[] columnNames = new String[] {"Unit", "Quantity", "Price"};
	
	private Product product;
	
	public void setProduct(Product product) {
		this.product = product;
		fireTableDataChanged();
	}
	
	@Override
	public int getRowCount() {
		return (product != null) ? product.getUnits().size() : 0;
	}

	@Override
	public int getColumnCount() {
		return columnNames.length;
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		if (product == null) {
			return "";
		}
		
		UnitPrice unitPrice = product.getUnitPrices().get(rowIndex);
		
		switch (columnIndex) {
		case UNIT_COLUMN_INDEX:
			return unitPrice.getUnit();
		case QUANTITY_COLUMN_INDEX:
			return "999"; // TODO: to be implemented in the future
		case UNIT_PRICE_COLUMN_INDEX:
			return LabelUtil.formatAmount(unitPrice.getPrice());
		default:
			throw new RuntimeException("Fetching invalid column index: " + columnIndex);
		}
	}

	@Override
	public String getColumnName(int column) {
		return columnNames[column];
	}
	
	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		return false;
	}
	
}
