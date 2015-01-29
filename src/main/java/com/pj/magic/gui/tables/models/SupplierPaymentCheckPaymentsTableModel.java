package com.pj.magic.gui.tables.models;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.pj.magic.gui.tables.PaymentCheckPaymentsTable;
import com.pj.magic.gui.tables.SupplierPaymentCheckPaymentsTable;
import com.pj.magic.gui.tables.rowitems.SupplierPaymentCheckPaymentRowItem;
import com.pj.magic.model.SupplierPayment;
import com.pj.magic.model.SupplierPaymentCheckPayment;
import com.pj.magic.service.SupplierPaymentService;
import com.pj.magic.util.DateUtil;
import com.pj.magic.util.FormatterUtil;
import com.pj.magic.util.NumberUtil;

@Component
public class SupplierPaymentCheckPaymentsTableModel extends AbstractTableModel {
	
	private static final String[] columnNames = {"Bank", "Check Date", "Check No.", "Amount"};
	
	@Autowired private SupplierPaymentService supplierPaymentService;
	
	private List<SupplierPaymentCheckPaymentRowItem> rowItems = new ArrayList<>();
	private SupplierPayment supplierPayment;
	
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
		SupplierPaymentCheckPaymentRowItem rowItem = rowItems.get(rowIndex);
		switch (columnIndex) {
		case PaymentCheckPaymentsTable.BANK_COLUMN_INDEX:
			return rowItem.getBank();
		case PaymentCheckPaymentsTable.CHECK_DATE_COLUMN_INDEX:
			Date checkDate = rowItem.getCheckDate();
			return (checkDate != null) ? FormatterUtil.formatDate(checkDate) : null;
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

	public void setPayment(SupplierPayment supplierPayment) {
		this.supplierPayment = supplierPayment;
		rowItems.clear();
		if (supplierPayment != null) {
			for (SupplierPaymentCheckPayment checkPayment : supplierPayment.getCheckPayments()) {
				rowItems.add(new SupplierPaymentCheckPaymentRowItem(checkPayment));
			}
		}
		fireTableDataChanged();
	}
	
	@Override
	public void setValueAt(Object value, int rowIndex, int columnIndex) {
		SupplierPaymentCheckPaymentRowItem rowItem = rowItems.get(rowIndex);
		String val = (String)value;
		switch (columnIndex) {
		case SupplierPaymentCheckPaymentsTable.BANK_COLUMN_INDEX:
			if (val.equals(rowItem.getBank())) {
				return;
			}
			rowItem.setBank(val);
			break;
		case SupplierPaymentCheckPaymentsTable.CHECK_DATE_COLUMN_INDEX:
			if (DateUtil.toDate(val).equals(rowItem.getCheckDate())) {
				return;
			}
			rowItem.setCheckDate(DateUtil.toDate(val));
			break;
		case SupplierPaymentCheckPaymentsTable.CHECK_NUMBER_COLUMN_INDEX:
			if (val.equals(rowItem.getCheckNumber())) {
				return;
			}
			rowItem.setCheckNumber(val);
			break;
		case SupplierPaymentCheckPaymentsTable.AMOUNT_COLUMN_INDEX:
			if (NumberUtil.toBigDecimal(val).equals(rowItem.getAmount())) {
				return;
			}
			rowItem.setAmount(NumberUtil.toBigDecimal(val));
			break;
		}
		
		if (rowItem.isValid()) {
			SupplierPaymentCheckPayment checkPayment = rowItem.getCheckPayment();
			checkPayment.setBank(rowItem.getBank());
			checkPayment.setCheckDate(rowItem.getCheckDate());
			checkPayment.setCheckNumber(rowItem.getCheckNumber());
			checkPayment.setAmount(rowItem.getAmount());
			
			boolean newCheckPayment = (checkPayment.getId() == null);
			supplierPaymentService.save(checkPayment);
			if (newCheckPayment) {
				checkPayment.getParent().getCheckPayments().add(checkPayment);
			}
		}
		fireTableCellUpdated(rowIndex, columnIndex);
	}
	
	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		if (supplierPayment.isPosted()) {
			return false;
		}
		
		SupplierPaymentCheckPaymentRowItem rowItem = rowItems.get(rowIndex);
		boolean editable = true;
		switch (columnIndex) {
		case SupplierPaymentCheckPaymentsTable.AMOUNT_COLUMN_INDEX:
			editable = !StringUtils.isEmpty(rowItem.getCheckNumber());
		case SupplierPaymentCheckPaymentsTable.CHECK_NUMBER_COLUMN_INDEX:
			editable = editable && rowItem.getCheckDate() != null;
		case SupplierPaymentCheckPaymentsTable.CHECK_DATE_COLUMN_INDEX:
			editable = editable && !StringUtils.isEmpty(rowItem.getBank());
		case SupplierPaymentCheckPaymentsTable.BANK_COLUMN_INDEX:
			break;
		}
		return editable;
	}
	
	@Override
	public Class<?> getColumnClass(int columnIndex) {
		if (columnIndex == PaymentCheckPaymentsTable.AMOUNT_COLUMN_INDEX) {
			return Number.class;
		} else {
			return Object.class;
		}
	}

	public void addItem(SupplierPaymentCheckPayment checkPayment) {
		rowItems.add(new SupplierPaymentCheckPaymentRowItem(checkPayment));
		fireTableDataChanged();
	}

	public void reset(int row) {
		rowItems.get(row).reset();
	}

	public SupplierPaymentCheckPaymentRowItem getRowItem(int row) {
		return rowItems.get(row);
	}

	public void removeItem(int row) {
		SupplierPaymentCheckPaymentRowItem rowItem = rowItems.remove(row);
		supplierPaymentService.delete(rowItem.getCheckPayment());
		fireTableDataChanged();
	}

	public boolean hasItems() {
		return !rowItems.isEmpty();
	}
	
}