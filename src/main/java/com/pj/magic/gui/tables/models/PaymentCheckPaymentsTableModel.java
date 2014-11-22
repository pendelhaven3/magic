package com.pj.magic.gui.tables.models;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.pj.magic.gui.tables.PaymentCheckPaymentsTable;
import com.pj.magic.gui.tables.rowitems.PaymentCheckPaymentRowItem;
import com.pj.magic.model.PaymentCheckPayment;
import com.pj.magic.service.PaymentService;
import com.pj.magic.util.FormatterUtil;
import com.pj.magic.util.NumberUtil;

@Component
public class PaymentCheckPaymentsTableModel extends AbstractTableModel {
	
	private static final String[] columnNames = {"Bank", "Check No.", "Amount"};
	
	@Autowired private PaymentService paymentService;
	
	private List<PaymentCheckPaymentRowItem> rowItems = new ArrayList<>();
	
	@Override
	public int getColumnCount() {
		return columnNames.length;
	}
	
	@Override
	public int getRowCount() {
		return rowItems.size();
	}
	
	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		PaymentCheckPaymentRowItem rowItem = rowItems.get(rowIndex);
		switch (columnIndex) {
		case PaymentCheckPaymentsTable.BANK_COLUMN_INDEX:
			return rowItem.getBank();
		case PaymentCheckPaymentsTable.CHECK_NUMBER_COLUMN_INDEX:
			return rowItem.getCheckNumber();
		case PaymentCheckPaymentsTable.AMOUNT_COLUMN_INDEX:
			BigDecimal amount = rowItem.getAmount();
			return (amount != null) ? FormatterUtil.formatAmount(amount) : null;
		default:
			throw new RuntimeException("Fetching invalid column index: " + columnIndex);
		}
	}
	
	@Override
	public String getColumnName(int columnIndex) {
		return columnNames[columnIndex];
	}

	public void setChecks(List<PaymentCheckPayment> checks) {
		rowItems.clear();
		for (PaymentCheckPayment check : checks) {
			rowItems.add(new PaymentCheckPaymentRowItem(check));
		}
		fireTableDataChanged();
	}
	
	@Override
	public void setValueAt(Object value, int rowIndex, int columnIndex) {
		PaymentCheckPaymentRowItem rowItem = rowItems.get(rowIndex);
		String val = (String)value;
		switch (columnIndex) {
		case PaymentCheckPaymentsTable.BANK_COLUMN_INDEX:
			if (val.equals(rowItem.getBank())) {
				return;
			}
			rowItem.setBank(val);
			break;
		case PaymentCheckPaymentsTable.CHECK_NUMBER_COLUMN_INDEX:
			if (val.equals(rowItem.getCheckNumber())) {
				return;
			}
			rowItem.setCheckNumber(val);
			break;
		case PaymentCheckPaymentsTable.AMOUNT_COLUMN_INDEX:
			if (NumberUtil.toBigDecimal(val).equals(rowItem.getAmount())) {
				return;
			}
			rowItem.setAmount(NumberUtil.toBigDecimal(val));
			break;
		}
		
		if (rowItem.isValid()) {
			PaymentCheckPayment check = rowItem.getCheck();
			check.setBank(rowItem.getBank());
			check.setCheckNumber(rowItem.getCheckNumber());
			check.setAmount(rowItem.getAmount());
			paymentService.save(check);
		}
		fireTableCellUpdated(rowIndex, columnIndex);
	}
	
	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		PaymentCheckPaymentRowItem check = rowItems.get(rowIndex);
		switch (columnIndex) {
		case PaymentCheckPaymentsTable.BANK_COLUMN_INDEX:
			return true;
		case PaymentCheckPaymentsTable.CHECK_NUMBER_COLUMN_INDEX:
			return !StringUtils.isEmpty(check.getBank());
		case PaymentCheckPaymentsTable.AMOUNT_COLUMN_INDEX:
			return !StringUtils.isEmpty(check.getBank()) && !StringUtils.isEmpty(check.getCheckNumber());
		}
		return true;
	}
	
	/*
	@Override
	public Class<?> getColumnClass(int columnIndex) {
		if (columnIndex == AdjustmentInItemsTable.COST_COLUMN_INDEX
				|| columnIndex == AdjustmentInItemsTable.AMOUNT_COLUMN_INDEX) {
			return Number.class;
		} else {
			return Object.class;
		}
	}
	*/

	public void addItem(PaymentCheckPayment check) {
		rowItems.add(new PaymentCheckPaymentRowItem(check));
		fireTableDataChanged();
	}

	public void reset(int row) {
		rowItems.get(row).reset();
	}

	public PaymentCheckPaymentRowItem getRowItem(int row) {
		return rowItems.get(row);
	}

	public void removeItem(int row) {
		rowItems.remove(row);
		fireTableDataChanged();
	}
	
}