package com.pj.magic.gui.tables.models;

import java.util.ArrayList;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import org.springframework.stereotype.Component;

import com.pj.magic.gui.tables.AreaInventoryReportsTable;
import com.pj.magic.model.AreaInventoryReport;
import com.pj.magic.util.FormatterUtil;

@Component
public class AreaInventoryReportsTableModel extends AbstractTableModel {

	private static final String[] COLUMN_NAMES = {"Inventory Date", "Area"};
	
	private List<AreaInventoryReport> areaInventoryReports = new ArrayList<>();
	
	@Override
	public int getRowCount() {
		return areaInventoryReports.size();
	}

	@Override
	public int getColumnCount() {
		return COLUMN_NAMES.length;
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		AreaInventoryReport areaInventoryReport = areaInventoryReports.get(rowIndex);
		switch (columnIndex) {
		case AreaInventoryReportsTable.INVENTORY_DATE_COLUMN_INDEX:
			return FormatterUtil.formatDate(areaInventoryReport.getParent().getInventoryDate());
		case AreaInventoryReportsTable.AREA_COLUMN_INDEX:
			return areaInventoryReport.getArea();
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
	
	public void setAreaInventoryReports(List<AreaInventoryReport> inventoryChecks) {
		this.areaInventoryReports = inventoryChecks;
		fireTableDataChanged();
	}
	
	public AreaInventoryReport getAreaInventoryReport(int rowIndex) {
		return areaInventoryReports.get(rowIndex);
	}

	public void remove(AreaInventoryReport adjustmentIn) {
		areaInventoryReports.remove(adjustmentIn);
		fireTableDataChanged();
	}
	
}
