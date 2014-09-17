package com.pj.magic.gui.tables.models;

import java.util.ArrayList;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import org.springframework.stereotype.Component;

import com.pj.magic.gui.tables.AdjustmentOutsTable;
import com.pj.magic.model.AdjustmentOut;

@Component
public class AdjustmentOutsTableModel extends AbstractTableModel {

	private static final String[] COLUMN_NAMES = {"Adj. Out No."};
	
	private List<AdjustmentOut> adjustmentOuts = new ArrayList<>();
	
	@Override
	public int getRowCount() {
		return adjustmentOuts.size();
	}

	@Override
	public int getColumnCount() {
		return COLUMN_NAMES.length;
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		AdjustmentOut adjustmentOut = adjustmentOuts.get(rowIndex);
		switch (columnIndex) {
		case AdjustmentOutsTable.ADJUSTMENT_OUT_NUMBER_COLUMN_INDEX:
			return adjustmentOut.getAdjustmentOutNumber().toString();
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
	
	public void setAdjustmentOuts(List<AdjustmentOut> adjustmentOuts) {
		this.adjustmentOuts = adjustmentOuts;
		fireTableDataChanged();
	}
	
	public AdjustmentOut getAdjustmentOut(int rowIndex) {
		return adjustmentOuts.get(rowIndex);
	}

	public void remove(AdjustmentOut adjustmentOut) {
		adjustmentOuts.remove(adjustmentOut);
		fireTableDataChanged();
	}
	
}