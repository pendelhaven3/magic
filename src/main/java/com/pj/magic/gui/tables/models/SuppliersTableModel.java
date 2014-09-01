package com.pj.magic.gui.tables.models;

import java.util.ArrayList;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import org.springframework.stereotype.Component;

import com.pj.magic.model.Supplier;

@Component
public class SuppliersTableModel extends AbstractTableModel {

	private static final String[] columnNames = {"Name"};
	
	private List<Supplier> suppliers = new ArrayList<>();
	
	@Override
	public int getRowCount() {
		return suppliers.size();
	}

	@Override
	public int getColumnCount() {
		return columnNames.length;
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		Supplier supplier = suppliers.get(rowIndex);
		return supplier.getName();
	}

	@Override
	public String getColumnName(int column) {
		return columnNames[column];
	}
	
	public void setSuppliers(List<Supplier> suppliers) {
		this.suppliers = suppliers;
		fireTableDataChanged();
	}
	
	public Supplier getSupplier(int rowIndex) {
		return suppliers.get(rowIndex);
	}
	
}
