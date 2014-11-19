package com.pj.magic.gui.tables.models;

import java.util.ArrayList;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import org.springframework.stereotype.Component;

import com.pj.magic.model.AccountsReceivableSummary;

@Component
public class AccountsReceivableSummariesTableModel extends AbstractTableModel {

	private static final String[] columnNames = {"AR Summary No.", "Customer", "Total Amount"};
	
	private static final int ACCOUNTS_RECEIVABLE_SUMMARY_NUMBER_COLUMN_INDEX = 0;
	private static final int CUSTOMER_COLUMN_INDEX = 1;
	private static final int TOTAL_AMOUNT_COLUMN_INDEX = 2;
	
	private List<AccountsReceivableSummary> summaries = new ArrayList<>();
	
	@Override
	public int getRowCount() {
		return summaries.size();
	}

	@Override
	public int getColumnCount() {
		return columnNames.length;
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		AccountsReceivableSummary summary = summaries.get(rowIndex);
		switch (columnIndex) {
		case ACCOUNTS_RECEIVABLE_SUMMARY_NUMBER_COLUMN_INDEX:
			return summary.getAccountsReceivableSummaryNumber();
		case CUSTOMER_COLUMN_INDEX:
			return summary.getCustomer().getName();
		case TOTAL_AMOUNT_COLUMN_INDEX:
			return summary.getTotalAmount();
		default:
			throw new RuntimeException("Fetching invalid column index: " + columnIndex);
		}
	}

	@Override
	public String getColumnName(int column) {
		return columnNames[column];
	}
	
	public void setSummaries(List<AccountsReceivableSummary> summaries) {
		this.summaries = summaries;
		fireTableDataChanged();
	}
	
	public AccountsReceivableSummary getSummary(int rowIndex) {
		return summaries.get(rowIndex);
	}
	
	@Override
	public Class<?> getColumnClass(int columnIndex) {
		if (columnIndex == TOTAL_AMOUNT_COLUMN_INDEX) {
			return Number.class;
		} else {
			return Object.class;
		}
	}
	
}
