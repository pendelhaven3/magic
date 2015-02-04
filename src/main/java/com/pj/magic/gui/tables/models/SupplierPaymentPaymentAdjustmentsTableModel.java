package com.pj.magic.gui.tables.models;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.pj.magic.gui.tables.PaymentPaymentAdjustmentsTable;
import com.pj.magic.gui.tables.SupplierPaymentPaymentAdjustmentsTable;
import com.pj.magic.gui.tables.rowitems.SupplierPaymentAdjustmentRowItem;
import com.pj.magic.model.PurchasePaymentAdjustmentType;
import com.pj.magic.model.PurchaseReturn;
import com.pj.magic.model.SupplierPayment;
import com.pj.magic.model.SupplierPaymentAdjustment;
import com.pj.magic.model.SupplierPaymentPaymentAdjustment;
import com.pj.magic.service.PurchasePaymentAdjustmentTypeService;
import com.pj.magic.service.PurchaseReturnService;
import com.pj.magic.service.SupplierPaymentAdjustmentService;
import com.pj.magic.service.SupplierPaymentService;
import com.pj.magic.util.FormatterUtil;
import com.pj.magic.util.NumberUtil;

@Component
public class SupplierPaymentPaymentAdjustmentsTableModel extends AbstractTableModel {
	
	private static final String[] columnNames = {"Adjustment Type", "Reference No.", "Amount"};
	
	@Autowired private SupplierPaymentService supplierPaymentService;
	@Autowired private SupplierPaymentAdjustmentService supplierPaymentAdjustmentService;
	@Autowired private PurchasePaymentAdjustmentTypeService purchasePaymentAdjustmentTypeService;
	@Autowired private PurchaseReturnService purchaseReturnService;
	
	private List<SupplierPaymentAdjustmentRowItem> rowItems = new ArrayList<>();
	private SupplierPayment payment;
	
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
		SupplierPaymentAdjustmentRowItem rowItem = rowItems.get(rowIndex);
		switch (columnIndex) {
		case PaymentPaymentAdjustmentsTable.ADJUSTMENT_TYPE_COLUMN_INDEX:
			return rowItem.getAdjustmentType();
		case PaymentPaymentAdjustmentsTable.REFERENCE_NUMBER_COLUMN_INDEX:
			return rowItem.getReferenceNumber();
		case PaymentPaymentAdjustmentsTable.AMOUNT_COLUMN_INDEX:
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

	public void setSupplierPayment(SupplierPayment supplierPayment) {
		this.payment = supplierPayment;
		rowItems.clear();
		if (supplierPayment != null) {
			for (SupplierPaymentPaymentAdjustment adjustment : supplierPayment.getPaymentAdjustments()) {
				rowItems.add(new SupplierPaymentAdjustmentRowItem(adjustment));
			}
		}
		fireTableDataChanged();
	}
	
	@Override
	public void setValueAt(Object value, int rowIndex, int columnIndex) {
		SupplierPaymentAdjustmentRowItem rowItem = rowItems.get(rowIndex);
		
		String val = (String)value;
		switch (columnIndex) {
		case SupplierPaymentPaymentAdjustmentsTable.ADJUSTMENT_TYPE_COLUMN_INDEX:
			PurchasePaymentAdjustmentType adjustmentType = purchasePaymentAdjustmentTypeService.findAdjustmentTypeByCode(val);
			if (val != null && val.equals(rowItem.getAdjustmentType())) {
				fireTableCellUpdated(rowIndex, columnIndex);
				return;
			}
			rowItem.setAdjustmentType(adjustmentType);
			rowItem.setReferenceNumber(null);
			rowItem.setAmount(null);
			break;
		case SupplierPaymentPaymentAdjustmentsTable.REFERENCE_NUMBER_COLUMN_INDEX:
			columnIndex = PaymentPaymentAdjustmentsTable.AMOUNT_COLUMN_INDEX;
			
			if (val.equals(rowItem.getReferenceNumber())) {
				return;
			}
			
			rowItem.setReferenceNumber(val);
			
			switch (rowItem.getAdjustmentType().getCode()) {
			case PurchasePaymentAdjustmentType.PURCHASE_RETURN_GOOD_STOCK_CODE:
				PurchaseReturn purchaseReturn = purchaseReturnService
					.findPurchaseReturnByPurchaseReturnNumber(Long.parseLong(val));
				rowItem.setAmount(purchaseReturn.getTotalAmount());
				break;
			default:
				SupplierPaymentAdjustment paymentAdjustment = supplierPaymentAdjustmentService
					.findSupplierPaymentAdjustmentBySupplierPaymentAdjustmentNumber(Long.parseLong(val));
				rowItem.setAmount(paymentAdjustment.getAmount());
				break;
			}
			
			
			break;
		case SupplierPaymentPaymentAdjustmentsTable.AMOUNT_COLUMN_INDEX:
			String amount = (String)value;
			if (NumberUtil.toBigDecimal(amount).equals(rowItem.getAmount())) {
				return;
			}
			rowItem.setAmount(NumberUtil.toBigDecimal(amount));
			break;
		}
		
		if (rowItem.isValid()) {
			SupplierPaymentPaymentAdjustment adjustment = rowItem.getPaymentAdjustment();
			adjustment.setAdjustmentType(rowItem.getAdjustmentType());
			adjustment.setReferenceNumber(rowItem.getReferenceNumber());
			adjustment.setAmount(rowItem.getAmount());
			
			boolean newAdjustment = (adjustment.getId() == null);
			supplierPaymentService.save(adjustment);
			if (newAdjustment) {
				adjustment.getParent().getPaymentAdjustments().add(adjustment);
			}
		}
		fireTableCellUpdated(rowIndex, columnIndex);
	}
	
	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		if (payment.isPosted()) {
			return false;
		}
		
		SupplierPaymentAdjustmentRowItem rowItem = rowItems.get(rowIndex);
		boolean editable = true;
		switch (columnIndex) {
		case PaymentPaymentAdjustmentsTable.AMOUNT_COLUMN_INDEX:
			editable = false;
			break;
		case PaymentPaymentAdjustmentsTable.REFERENCE_NUMBER_COLUMN_INDEX:
			editable = (rowItem.getAdjustmentType() != null);
			break;
		}
		return editable;
	}
	
	@Override
	public Class<?> getColumnClass(int columnIndex) {
		if (columnIndex == PaymentPaymentAdjustmentsTable.AMOUNT_COLUMN_INDEX) {
			return Number.class;
		} else {
			return Object.class;
		}
	}

	public void addItem(SupplierPaymentPaymentAdjustment adjustment) {
		rowItems.add(new SupplierPaymentAdjustmentRowItem(adjustment));
		fireTableDataChanged();
	}

	public void reset(int row) {
		rowItems.get(row).reset();
	}

	public SupplierPaymentAdjustmentRowItem getRowItem(int row) {
		return rowItems.get(row);
	}

	public void removeItem(int row) {
		SupplierPaymentAdjustmentRowItem rowItem = rowItems.remove(row);
		supplierPaymentService.delete(rowItem.getPaymentAdjustment());
		fireTableDataChanged();
	}

	public boolean hasItems() {
		return !rowItems.isEmpty();
	}
	
}