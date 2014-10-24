package com.pj.magic.gui.tables.models;

import javax.swing.table.AbstractTableModel;

import com.pj.magic.model.Product;
import com.pj.magic.model.Unit;
import com.pj.magic.util.FormatterUtil;

public class UnitPricesAndQuantitiesTableModel extends AbstractTableModel {

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
		return 5;
	}

	@Override
	public int getColumnCount() {
		return columnNames.length;
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		String unit = null;
		switch (rowIndex) {
		case 0:
			unit = Unit.CASE;
			break;
		case 1:
			unit = Unit.TIE;
			break;
		case 2:
			unit = Unit.CARTON;
			break;
		case 3:
			unit = Unit.DOZEN;
			break;
		case 4:
			unit = Unit.PIECES;
			break;
		}

		switch (columnIndex) {
		case UNIT_COLUMN_INDEX:
			return unit;
		case QUANTITY_COLUMN_INDEX:
			return (product != null) ? product.getUnitQuantity(unit) : "0";
		case UNIT_PRICE_COLUMN_INDEX:
			if (product != null && product.hasUnit(unit)) {
				return FormatterUtil.formatAmount(product.getUnitPrice(unit));
			} else {
				return "0.00";
			}
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
	
	@Override
	public Class<?> getColumnClass(int columnIndex) {
		if (columnIndex == UNIT_PRICE_COLUMN_INDEX) {
			return Number.class;
		} else {
			return Object.class;
		}
	}
	
}
