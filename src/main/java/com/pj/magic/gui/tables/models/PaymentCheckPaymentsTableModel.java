package com.pj.magic.gui.tables.models;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.pj.magic.gui.tables.PaymentCheckPaymentsTable;
import com.pj.magic.model.PaymentCheckPayment;
import com.pj.magic.service.PaymentService;
import com.pj.magic.util.FormatterUtil;
import com.pj.magic.util.NumberUtil;

@Component
public class PaymentCheckPaymentsTableModel extends AbstractTableModel {
	
	private static final String[] columnNames = {"Bank", "Check No.", "Amount"};
	
	@Autowired private PaymentService paymentService;
	
	private List<PaymentCheckPayment> checks = new ArrayList<>();
	
	@Override
	public int getColumnCount() {
		return columnNames.length;
	}
	
	@Override
	public int getRowCount() {
		return checks.size();
	}
	
	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		PaymentCheckPayment check = checks.get(rowIndex);
		switch (columnIndex) {
		case PaymentCheckPaymentsTable.BANK_COLUMN_INDEX:
			return check.getBank();
		case PaymentCheckPaymentsTable.CHECK_NUMBER_COLUMN_INDEX:
			return check.getCheckNumber();
		case PaymentCheckPaymentsTable.AMOUNT_COLUMN_INDEX:
			BigDecimal amount = check.getAmount();
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
		this.checks = checks;
		fireTableDataChanged();
	}
	
//	public void addItem(AdjustmentInItem item) {
//		rowItems.add(new AdjustmentInItemRowItem(item));
//		fireTableDataChanged();
//	}
	
	@Override
	public void setValueAt(Object value, int rowIndex, int columnIndex) {
		PaymentCheckPayment check = checks.get(rowIndex);
		String val = (String)value;
		switch (columnIndex) {
		case PaymentCheckPaymentsTable.BANK_COLUMN_INDEX:
			if (val.equals(check.getBank())) {
				return;
			}
			check.setBank(val);
			break;
		case PaymentCheckPaymentsTable.CHECK_NUMBER_COLUMN_INDEX:
			if (val.equals(check.getCheckNumber())) {
				return;
			}
			check.setCheckNumber(val);
			break;
		case PaymentCheckPaymentsTable.AMOUNT_COLUMN_INDEX:
			if (NumberUtil.toBigDecimal(val).equals(check.getAmount())) {
				return;
			}
			check.setAmount(NumberUtil.toBigDecimal(val));
			break;
		}
		
		if (isValid(check)) {
			paymentService.save(check);
		}
		fireTableCellUpdated(rowIndex, columnIndex);
	}
	
	private boolean isValid(PaymentCheckPayment check) {
		return StringUtils.isEmpty(check.getBank()) && StringUtils.isEmpty(check.getCheckNumber())
				&& check.getAmount() != null;
	}

	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) {
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
		checks.add(check);
		fireTableDataChanged();
	}

}