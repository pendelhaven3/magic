package com.pj.magic.gui.tables.models;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import org.apache.commons.lang.StringUtils;

import com.pj.magic.gui.tables.SalesInvoicesTable;
import com.pj.magic.model.SalesInvoice;
import com.pj.magic.util.FormatterUtil;

public class SalesInvoicesTableModel extends AbstractTableModel {

	private static final String[] COLUMN_NAMES = {"SI No.", "Customer Name", "Post Date", "Posted By", "Total Amount"};
	
	private List<SalesInvoice> salesInvoices = new ArrayList<>();
	
	@Override
	public int getRowCount() {
		return salesInvoices.size();
	}

	@Override
	public int getColumnCount() {
		return COLUMN_NAMES.length;
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		SalesInvoice salesInvoice = salesInvoices.get(rowIndex);
		switch (columnIndex) {
		case SalesInvoicesTable.SALES_INVOICE_NUMBER_COLUMN_INDEX:
			return salesInvoice.getSalesInvoiceNumber().toString();
		case SalesInvoicesTable.CUSTOMER_NAME_COLUMN_INDEX:
			return salesInvoice.getCustomer().getName();
		case SalesInvoicesTable.POST_DATE_COLUMN_INDEX:
			Date date = salesInvoice.getPostDate();
			return (date != null) ? FormatterUtil.formatDate(date) : "";
		case SalesInvoicesTable.POSTED_BY_COLUMN_INDEX:
			return StringUtils.defaultString(salesInvoice.getPostedBy());
		case SalesInvoicesTable.TOTAL_AMOUNT_COLUMN_INDEX:
			return FormatterUtil.formatAmount(salesInvoice.getTotalAmount());
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
	
	public void setSalesInvoices(List<SalesInvoice> salesInvoices) {
		this.salesInvoices = salesInvoices;
		fireTableDataChanged();
	}
	
	public SalesInvoice getSalesInvoice(int rowIndex) {
		return salesInvoices.get(rowIndex);
	}

}
