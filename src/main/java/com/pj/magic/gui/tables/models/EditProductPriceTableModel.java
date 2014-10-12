package com.pj.magic.gui.tables.models;

import java.util.Collections;
import java.util.Comparator;

import javax.swing.table.AbstractTableModel;

import org.springframework.stereotype.Component;

import com.pj.magic.gui.tables.EditProductPriceTable;
import com.pj.magic.model.Product;
import com.pj.magic.model.Unit;
import com.pj.magic.util.FormatterUtil;
import com.pj.magic.util.NumberUtil;

@Component
public class EditProductPriceTableModel extends AbstractTableModel {

	private static final String[] columnNames =
		{"Unit", "Final Cost", "Selling Price", "% Profit", "Flat Profit"};

	private Product product;
	private String finalCost;
	private String sellingPrice;
	private String percentProfit;
	private String flatProfit;
	
	public void setProduct(Product product) {
		this.product = product;
		
		Collections.sort(product.getUnits(), new Comparator<String>() {

			@Override
			public int compare(String unit1, String unit2) {
				return Unit.compare(unit1, unit2) * -1;
			}
		});
		
		String maxUnit = product.getUnits().get(0);
		finalCost = FormatterUtil.formatAmount(product.getFinalCost(maxUnit));
		sellingPrice = FormatterUtil.formatAmount(product.getUnitPrice(maxUnit));
		percentProfit = FormatterUtil.formatAmount(product.getPercentProfit(maxUnit));
		flatProfit = FormatterUtil.formatAmount(product.getFlatProfit(maxUnit));
		
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
	public String getColumnName(int column) {
		return columnNames[column];
	}
	
	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		String unit = product.getUnits().get(rowIndex);
		switch (columnIndex) {
		case EditProductPriceTable.UNIT_COLUMN_INDEX:
			return unit;
		}
		
		if (rowIndex == 0) {
			switch (columnIndex) {
			case EditProductPriceTable.FINAL_COST_COLUMN_INDEX:
				return finalCost;
			case EditProductPriceTable.SELLING_PRICE_COLUMN_INDEX:
				return sellingPrice;
			case EditProductPriceTable.PERCENT_PROFIT_COLUMN_INDEX:
				return percentProfit;
			case EditProductPriceTable.FLAT_PROFIT_COLUMN_INDEX:
				return flatProfit;
			default:
				throw new RuntimeException("Fetching invalid column index: " + columnIndex);
			}
		} else {
			switch (columnIndex) {
			case EditProductPriceTable.FINAL_COST_COLUMN_INDEX:
				return FormatterUtil.formatAmount(product.getFinalCost(unit));
			case EditProductPriceTable.SELLING_PRICE_COLUMN_INDEX:
				return FormatterUtil.formatAmount(product.getUnitPrice(unit));
			case EditProductPriceTable.PERCENT_PROFIT_COLUMN_INDEX:
				return FormatterUtil.formatAmount(product.getPercentProfit(unit));
			case EditProductPriceTable.FLAT_PROFIT_COLUMN_INDEX:
				return FormatterUtil.formatAmount(product.getFlatProfit(unit));
			default:
				throw new RuntimeException("Fetching invalid column index: " + columnIndex);
			}
		}
	}
	
	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		return rowIndex == 0 && (columnIndex == EditProductPriceTable.FINAL_COST_COLUMN_INDEX
				|| columnIndex == EditProductPriceTable.SELLING_PRICE_COLUMN_INDEX
				|| columnIndex == EditProductPriceTable.PERCENT_PROFIT_COLUMN_INDEX
				|| columnIndex == EditProductPriceTable.FLAT_PROFIT_COLUMN_INDEX);
	}

	@Override
	public void setValueAt(Object value, int rowIndex, int columnIndex) {
		if (rowIndex != 0) {
			throw new RuntimeException("Cannot directly update costs/prices for smaller units of product");
		}
		
		String val = (String)value;
		switch (columnIndex) {
		case EditProductPriceTable.FINAL_COST_COLUMN_INDEX:
			finalCost = val;
			break;
		case EditProductPriceTable.SELLING_PRICE_COLUMN_INDEX:
			sellingPrice = val;
			break;
		case EditProductPriceTable.PERCENT_PROFIT_COLUMN_INDEX:
			percentProfit = val;
			break;
		case EditProductPriceTable.FLAT_PROFIT_COLUMN_INDEX:
			flatProfit = val;
			break;
		}
		
		if (NumberUtil.isAmount(val)) {
			String unit = product.getUnits().get(rowIndex);
			switch (columnIndex) {
			case EditProductPriceTable.FINAL_COST_COLUMN_INDEX:
				product.setFinalCost(unit, NumberUtil.toBigDecimal(val));
				break;
			case EditProductPriceTable.SELLING_PRICE_COLUMN_INDEX:
				product.setUnitPrice(unit, NumberUtil.toBigDecimal(val));
				break;
			case EditProductPriceTable.PERCENT_PROFIT_COLUMN_INDEX:
				product.setPercentProfit(unit, NumberUtil.toBigDecimal(val));
				break;
			case EditProductPriceTable.FLAT_PROFIT_COLUMN_INDEX:
				product.setFlatProfit(unit, NumberUtil.toBigDecimal(val));
				break;
			}
			finalCost = FormatterUtil.formatAmount(product.getFinalCost(unit));
			sellingPrice = FormatterUtil.formatAmount(product.getUnitPrice(unit));
			percentProfit = FormatterUtil.formatAmount(product.getPercentProfit(unit));
			flatProfit = FormatterUtil.formatAmount(product.getFlatProfit(unit));
			fireTableCellUpdated(rowIndex, EditProductPriceTable.FINAL_COST_COLUMN_INDEX);
			fireTableCellUpdated(rowIndex, EditProductPriceTable.SELLING_PRICE_COLUMN_INDEX);
			fireTableCellUpdated(rowIndex, EditProductPriceTable.PERCENT_PROFIT_COLUMN_INDEX);
			fireTableCellUpdated(rowIndex, EditProductPriceTable.FLAT_PROFIT_COLUMN_INDEX);
			
			if (product.getUnits().size() > 1) {
				product.autoCalculateCostsOfSmallerUnits();
				product.autoCalculatePricesOfSmallerUnits();
				for (int i = 1; i < product.getUnits().size(); i++) {
					fireTableCellUpdated(i, EditProductPriceTable.FINAL_COST_COLUMN_INDEX);
					fireTableCellUpdated(i, EditProductPriceTable.SELLING_PRICE_COLUMN_INDEX);
				}
			}
		} else {
			fireTableCellUpdated(rowIndex, columnIndex);
		}
		
	}
	
}
