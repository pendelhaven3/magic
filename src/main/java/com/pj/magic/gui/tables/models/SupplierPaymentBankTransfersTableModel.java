package com.pj.magic.gui.tables.models;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.pj.magic.gui.tables.SupplierPaymentBankTransfersTable;
import com.pj.magic.gui.tables.rowitems.SupplierPaymentBankTransferRowItem;
import com.pj.magic.model.SupplierPayment;
import com.pj.magic.model.SupplierPaymentBankTransfer;
import com.pj.magic.service.SupplierPaymentService;
import com.pj.magic.util.DateUtil;
import com.pj.magic.util.FormatterUtil;
import com.pj.magic.util.NumberUtil;

@Component
public class SupplierPaymentBankTransfersTableModel extends AbstractTableModel {
	
	private static final String[] columnNames = {"Bank", "Amount", "Reference Number", "Transfer Date"};
	
	@Autowired private SupplierPaymentService supplierPaymentService;
	
	private List<SupplierPaymentBankTransferRowItem> rowItems = new ArrayList<>();
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
		SupplierPaymentBankTransferRowItem rowItem = rowItems.get(rowIndex);
		switch (columnIndex) {
		case SupplierPaymentBankTransfersTable.BANK_COLUMN_INDEX:
			return rowItem.getBank();
		case SupplierPaymentBankTransfersTable.AMOUNT_COLUMN_INDEX:
			BigDecimal amount = rowItem.getAmount();
			return (amount != null) ? FormatterUtil.formatAmount(amount) : null;
		case SupplierPaymentBankTransfersTable.REFERENCE_NUMBER_COLUMN_INDEX:
			return rowItem.getReferenceNumber();
		case SupplierPaymentBankTransfersTable.TRANSFER_DATE_COLUMN_INDEX:
			Date transferDate = rowItem.getTransferDate();
			return (transferDate != null) ? FormatterUtil.formatDate(transferDate) : null;
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
			for (SupplierPaymentBankTransfer bankTransfer : supplierPayment.getBankTransfers()) {
				rowItems.add(new SupplierPaymentBankTransferRowItem(bankTransfer));
			}
		}
		fireTableDataChanged();
	}
	
	@Override
	public void setValueAt(Object value, int rowIndex, int columnIndex) {
		SupplierPaymentBankTransferRowItem rowItem = rowItems.get(rowIndex);
		switch (columnIndex) {
		case SupplierPaymentBankTransfersTable.BANK_COLUMN_INDEX:
			String bank = (String)value;
			if (bank.equals(rowItem.getBank())) {
				return;
			}
			rowItem.setBank(bank);
			break;
		case SupplierPaymentBankTransfersTable.AMOUNT_COLUMN_INDEX:
			String amount = (String)value;
			if (NumberUtil.toBigDecimal(amount).equals(rowItem.getAmount())) {
				return;
			}
			rowItem.setAmount(NumberUtil.toBigDecimal(amount));
			break;
		case SupplierPaymentBankTransfersTable.REFERENCE_NUMBER_COLUMN_INDEX:
			String referenceNumber = (String)value;
			if (referenceNumber.equals(rowItem.getReferenceNumber())) {
				return;
			}
			rowItem.setReferenceNumber(referenceNumber);
			break;
		case SupplierPaymentBankTransfersTable.TRANSFER_DATE_COLUMN_INDEX:
			String transferDateString = (String)value;
			if (DateUtil.toDate(transferDateString).equals(rowItem.getTransferDate())) {
				return;
			}
			rowItem.setTransferDate(DateUtil.toDate(transferDateString));
			break;
		}
		
		if (rowItem.isValid()) {
			SupplierPaymentBankTransfer bankTransfer = rowItem.getBankTransfer();
			bankTransfer.setBank(rowItem.getBank());
			bankTransfer.setAmount(rowItem.getAmount());
			bankTransfer.setReferenceNumber(rowItem.getReferenceNumber());
			bankTransfer.setTransferDate(rowItem.getTransferDate());
			
			boolean newBankTransfer = (bankTransfer.getId() == null);
			supplierPaymentService.save(bankTransfer);
			if (newBankTransfer) {
				bankTransfer.getParent().getBankTransfers().add(bankTransfer);
			}
		}
		fireTableCellUpdated(rowIndex, columnIndex);
	}
	
	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		if (supplierPayment.isPosted()) {
			return false;
		}
		
		SupplierPaymentBankTransferRowItem rowItem = rowItems.get(rowIndex);
		boolean editable = true;
		switch (columnIndex) {
		case SupplierPaymentBankTransfersTable.TRANSFER_DATE_COLUMN_INDEX:
			editable = !StringUtils.isEmpty(rowItem.getReferenceNumber());
		case SupplierPaymentBankTransfersTable.REFERENCE_NUMBER_COLUMN_INDEX:
			editable = (rowItem.getAmount() != null);
		case SupplierPaymentBankTransfersTable.AMOUNT_COLUMN_INDEX:
			editable = !StringUtils.isEmpty(rowItem.getBank());
		case SupplierPaymentBankTransfersTable.BANK_COLUMN_INDEX:
			break;
		}
		return editable;
	}
	
	@Override
	public Class<?> getColumnClass(int columnIndex) {
		if (columnIndex == SupplierPaymentBankTransfersTable.AMOUNT_COLUMN_INDEX) {
			return Number.class;
		} else {
			return Object.class;
		}
	}

	public void addItem(SupplierPaymentBankTransfer bankTransfer) {
		rowItems.add(new SupplierPaymentBankTransferRowItem(bankTransfer));
		fireTableDataChanged();
	}

	public void reset(int row) {
		rowItems.get(row).reset();
	}

	public SupplierPaymentBankTransferRowItem getRowItem(int row) {
		return rowItems.get(row);
	}

	public void removeItem(int row) {
		SupplierPaymentBankTransferRowItem rowItem = rowItems.remove(row);
		supplierPaymentService.delete(rowItem.getBankTransfer());
		fireTableDataChanged();
	}

	public boolean hasItems() {
		return !rowItems.isEmpty();
	}
	
}