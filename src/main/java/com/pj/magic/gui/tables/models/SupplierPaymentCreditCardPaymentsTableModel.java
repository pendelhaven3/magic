package com.pj.magic.gui.tables.models;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.pj.magic.gui.tables.SupplierPaymentCreditCardPaymentsTable;
import com.pj.magic.gui.tables.rowitems.SupplierPaymentCreditCardPaymentRowItem;
import com.pj.magic.model.SupplierPayment;
import com.pj.magic.model.SupplierPaymentCreditCardPayment;
import com.pj.magic.model.User;
import com.pj.magic.service.SupplierPaymentService;
import com.pj.magic.util.DateUtil;
import com.pj.magic.util.FormatterUtil;
import com.pj.magic.util.NumberUtil;

@Component
public class SupplierPaymentCreditCardPaymentsTableModel extends AbstractTableModel {
	
	private static final String[] columnNames = {"Amount", "Bank", "Paid Date", "Paid By"};
	
	@Autowired private SupplierPaymentService supplierPaymentService;
	
	private List<SupplierPaymentCreditCardPaymentRowItem> rowItems = new ArrayList<>();
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
		SupplierPaymentCreditCardPaymentRowItem rowItem = rowItems.get(rowIndex);
		switch (columnIndex) {
		case SupplierPaymentCreditCardPaymentsTable.AMOUNT_COLUMN_INDEX:
			BigDecimal amount = rowItem.getAmount();
			return (amount != null) ? FormatterUtil.formatAmount(amount) : null;
		case SupplierPaymentCreditCardPaymentsTable.BANK_COLUMN_INDEX:
			return rowItem.getBank();
		case SupplierPaymentCreditCardPaymentsTable.PAID_DATE_COLUMN_INDEX:
			Date paidDate = rowItem.getPaidDate();
			return (paidDate != null) ? FormatterUtil.formatDate(paidDate) : null;
		case SupplierPaymentCreditCardPaymentsTable.PAID_BY_COLUMN_INDEX:
			return rowItem.getPaidBy();
		default:
			throw new RuntimeException("Fetching invalid column index: " + columnIndex);
		}
	}
	
	@Override
	public String getColumnName(int columnIndex) {
		return columnNames[columnIndex];
	}

	public void setSupplierPayment(SupplierPayment supplierPayment) {
		this.supplierPayment = supplierPayment;
		rowItems.clear();
		if (supplierPayment != null) {
			for (SupplierPaymentCreditCardPayment creditCardPayment : supplierPayment.getCreditCardPayments()) {
				rowItems.add(new SupplierPaymentCreditCardPaymentRowItem(creditCardPayment));
			}
		}
		fireTableDataChanged();
	}
	
	@Override
	public void setValueAt(Object value, int rowIndex, int columnIndex) {
		SupplierPaymentCreditCardPaymentRowItem rowItem = rowItems.get(rowIndex);
		switch (columnIndex) {
		case SupplierPaymentCreditCardPaymentsTable.AMOUNT_COLUMN_INDEX:
			String amount = (String)value;
			if (NumberUtil.toBigDecimal(amount).equals(rowItem.getAmount())) {
				return;
			}
			rowItem.setAmount(NumberUtil.toBigDecimal(amount));
			break;
		case SupplierPaymentCreditCardPaymentsTable.BANK_COLUMN_INDEX:
			String bank = (String)value;
			if (bank.equals(rowItem.getBank())) {
				return;
			}
			rowItem.setBank(bank);
			break;
		case SupplierPaymentCreditCardPaymentsTable.PAID_DATE_COLUMN_INDEX:
			String dateString = (String)value;
			if (DateUtil.toDate(dateString).equals(rowItem.getPaidDate())) {
				return;
			}
			rowItem.setPaidDate(DateUtil.toDate(dateString));
			break;
		case SupplierPaymentCreditCardPaymentsTable.PAID_BY_COLUMN_INDEX:
			User paidBy = (User)value;
			if (paidBy == null || paidBy.equals(rowItem.getPaidBy())) {
				return;
			}
			rowItem.setPaidBy(paidBy);
			break;
		}
		
		if (rowItem.isValid()) {
			SupplierPaymentCreditCardPayment creditCardPayment = rowItem.getCreditCardPayment();
			creditCardPayment.setAmount(rowItem.getAmount());
			creditCardPayment.setBank(rowItem.getBank());
			creditCardPayment.setPaidDate(rowItem.getPaidDate());
			creditCardPayment.setPaidBy(rowItem.getPaidBy());
			
			boolean newCreditCardPayment = (creditCardPayment.getId() == null);
			supplierPaymentService.save(creditCardPayment);
			if (newCreditCardPayment) {
				creditCardPayment.getParent().getCreditCardPayments().add(creditCardPayment);
			}
		}
		fireTableCellUpdated(rowIndex, columnIndex);
	}
	
	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		if (supplierPayment.isPosted()) {
			return false;
		}
		
		SupplierPaymentCreditCardPaymentRowItem rowItem = rowItems.get(rowIndex);
		boolean editable = true;
		switch (columnIndex) {
		case SupplierPaymentCreditCardPaymentsTable.PAID_BY_COLUMN_INDEX:
			editable = (rowItem.getPaidDate() != null);
		case SupplierPaymentCreditCardPaymentsTable.PAID_DATE_COLUMN_INDEX:
			editable = (rowItem.getAmount() != null);
		case SupplierPaymentCreditCardPaymentsTable.AMOUNT_COLUMN_INDEX:
			break;
		}
		return editable;
	}
	
	@Override
	public Class<?> getColumnClass(int columnIndex) {
		if (columnIndex == SupplierPaymentCreditCardPaymentsTable.AMOUNT_COLUMN_INDEX) {
			return Number.class;
		} else {
			return Object.class;
		}
	}

	public void addItem(SupplierPaymentCreditCardPayment creditCardPayment) {
		rowItems.add(new SupplierPaymentCreditCardPaymentRowItem(creditCardPayment));
		fireTableDataChanged();
	}

	public void reset(int row) {
		rowItems.get(row).reset();
	}

	public SupplierPaymentCreditCardPaymentRowItem getRowItem(int row) {
		return rowItems.get(row);
	}

	public void removeItem(int row) {
		SupplierPaymentCreditCardPaymentRowItem rowItem = rowItems.remove(row);
		supplierPaymentService.delete(rowItem.getCreditCardPayment());
		fireTableDataChanged();
	}

	public boolean hasItems() {
		return !rowItems.isEmpty();
	}
	
}