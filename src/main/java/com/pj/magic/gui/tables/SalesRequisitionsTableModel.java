package com.pj.magic.gui.tables;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import org.apache.commons.lang.StringUtils;

import com.pj.magic.model.SalesRequisition;
import com.pj.magic.util.LabelUtil;

public class SalesRequisitionsTableModel extends AbstractTableModel {

	private static final String[] COLUMN_NAMES = {"SR No.", "Customer Name", "Create Date", "Encoder"};
	
	private List<SalesRequisition> salesRequisitions = new ArrayList<>();
	
	@Override
	public int getRowCount() {
		return salesRequisitions.size();
	}

	@Override
	public int getColumnCount() {
		return COLUMN_NAMES.length;
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		SalesRequisition salesRequisition = salesRequisitions.get(rowIndex);
		switch (columnIndex) {
		case SalesRequisitionsTable.SALES_REQUISITION_NUMBER_COLUMN_INDEX:
			return salesRequisition.getSalesRequisitionNumber().toString();
		case SalesRequisitionsTable.CUSTOMER_NAME_COLUMN_INDEX:
			return salesRequisition.getCustomerName();
		case SalesRequisitionsTable.CREATE_DATE_COLUMN_INDEX:
			Date date = salesRequisition.getCreateDate();
			return (date != null) ? LabelUtil.formatDate(date) : "";
		case SalesRequisitionsTable.ENCODER_COLUMN_INDEX:
			return StringUtils.defaultString(salesRequisition.getEncoder());
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
	
	public void setSalesRequisitions(List<SalesRequisition> salesRequisitions) {
		this.salesRequisitions = salesRequisitions;
		fireTableDataChanged();
	}
	
	public SalesRequisition getSalesRequisition(int rowIndex) {
		return salesRequisitions.get(rowIndex);
	}
	
}
