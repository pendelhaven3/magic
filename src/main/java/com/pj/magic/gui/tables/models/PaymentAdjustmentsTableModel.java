package com.pj.magic.gui.tables.models;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.pj.magic.gui.tables.PaymentAdjustmentsTable;
import com.pj.magic.gui.tables.rowitems.PaymentAdjustmentRowItem;
import com.pj.magic.model.AdjustmentType;
import com.pj.magic.model.BadStockReturn;
import com.pj.magic.model.Payment;
import com.pj.magic.model.PaymentAdjustment;
import com.pj.magic.model.SalesReturn;
import com.pj.magic.service.AdjustmentTypeService;
import com.pj.magic.service.BadStockReturnService;
import com.pj.magic.service.PaymentService;
import com.pj.magic.service.SalesReturnService;
import com.pj.magic.util.FormatterUtil;
import com.pj.magic.util.NumberUtil;

@Component
public class PaymentAdjustmentsTableModel extends AbstractTableModel {
	
	private static final String[] columnNames = {"Adjustment Type", "Reference No.", "Amount"};
	
	@Autowired private PaymentService paymentService;
	@Autowired private SalesReturnService salesReturnService;
	@Autowired private BadStockReturnService badStockReturnService;
	@Autowired private AdjustmentTypeService adjustmentTypeService;
	
	private List<PaymentAdjustmentRowItem> rowItems = new ArrayList<>();
	private Payment payment;
	
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
		PaymentAdjustmentRowItem rowItem = rowItems.get(rowIndex);
		switch (columnIndex) {
		case PaymentAdjustmentsTable.ADJUSTMENT_TYPE_COLUMN_INDEX:
			return rowItem.getAdjustmentType();
		case PaymentAdjustmentsTable.REFERENCE_NUMBER_COLUMN_INDEX:
			return rowItem.getReferenceNumber();
		case PaymentAdjustmentsTable.AMOUNT_COLUMN_INDEX:
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

	public void setPayment(Payment payment) {
		this.payment = payment;
		rowItems.clear();
		if (payment != null) {
			for (PaymentAdjustment adjustment : payment.getAdjustments()) {
				rowItems.add(new PaymentAdjustmentRowItem(adjustment));
			}
		}
		fireTableDataChanged();
	}
	
	@Override
	public void setValueAt(Object value, int rowIndex, int columnIndex) {
		PaymentAdjustmentRowItem rowItem = rowItems.get(rowIndex);
		
		String val = (String)value;
		switch (columnIndex) {
		case PaymentAdjustmentsTable.ADJUSTMENT_TYPE_COLUMN_INDEX:
			AdjustmentType adjustmentType = adjustmentTypeService.findAdjustmentTypeByCode(val);
			if (val != null && val.equals(rowItem.getAdjustmentType())) {
				fireTableCellUpdated(rowIndex, columnIndex);
				return;
			}
			rowItem.setAdjustmentType(adjustmentType);
			rowItem.setReferenceNumber(null);
			rowItem.setAmount(null);
			break;
		case PaymentAdjustmentsTable.REFERENCE_NUMBER_COLUMN_INDEX:
			if (val.equals(rowItem.getReferenceNumber())) {
				return;
			}
			
			if (!StringUtils.isEmpty(val)) {
				rowItem.setReferenceNumber(val);
				
				if (AdjustmentType.SALES_RETURN.equals(rowItem.getAdjustmentType())) {
					SalesReturn salesReturn = salesReturnService
							.findSalesReturnBySalesReturnNumber(Long.parseLong(val));
					rowItem.setAmount(salesReturn.getTotalAmount());
					columnIndex = PaymentAdjustmentsTable.AMOUNT_COLUMN_INDEX;
				} else if (AdjustmentType.BAD_STOCK_RETURN.equals(rowItem.getAdjustmentType())) {
					BadStockReturn badStockReturn = badStockReturnService
							.findBadStockReturnByBadStockReturnNumber(Long.parseLong(val));
					rowItem.setAmount(badStockReturn.getTotalAmount());
					columnIndex = PaymentAdjustmentsTable.AMOUNT_COLUMN_INDEX;
				}
			} else {
				rowItem.setReferenceNumber(null);
			}
			
			break;
		case PaymentAdjustmentsTable.AMOUNT_COLUMN_INDEX:
			String amount = (String)value;
			if (NumberUtil.toBigDecimal(amount).equals(rowItem.getAmount())) {
				return;
			}
			rowItem.setAmount(NumberUtil.toBigDecimal(amount));
			break;
		}
		
		if (rowItem.isValid()) {
			PaymentAdjustment adjustment = rowItem.getAdjustment();
			adjustment.setAdjustmentType(rowItem.getAdjustmentType());
			adjustment.setReferenceNumber(rowItem.getReferenceNumber());
			adjustment.setAmount(rowItem.getAmount());
			
			boolean newAdjustment = (adjustment.getId() == null);
			paymentService.save(adjustment);
			if (newAdjustment) {
				adjustment.getParent().getAdjustments().add(adjustment);
			}
		}
		fireTableCellUpdated(rowIndex, columnIndex);
	}
	
	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		if (payment.isPosted()) {
			return false;
		}
		
		PaymentAdjustmentRowItem rowItem = rowItems.get(rowIndex);
		boolean editable = true;
		switch (columnIndex) {
		case PaymentAdjustmentsTable.REFERENCE_NUMBER_COLUMN_INDEX:
		case PaymentAdjustmentsTable.AMOUNT_COLUMN_INDEX:
			editable = (rowItem.getAdjustmentType() != null);
			break;
		}
		return editable;
	}
	
	@Override
	public Class<?> getColumnClass(int columnIndex) {
		if (columnIndex == PaymentAdjustmentsTable.AMOUNT_COLUMN_INDEX) {
			return Number.class;
		} else {
			return Object.class;
		}
	}

	public void addItem(PaymentAdjustment adjustment) {
		rowItems.add(new PaymentAdjustmentRowItem(adjustment));
		fireTableDataChanged();
	}

	public void reset(int row) {
		rowItems.get(row).reset();
	}

	public PaymentAdjustmentRowItem getRowItem(int row) {
		return rowItems.get(row);
	}

	public void removeItem(int row) {
		PaymentAdjustmentRowItem rowItem = rowItems.remove(row);
		paymentService.delete(rowItem.getAdjustment());
		fireTableDataChanged();
	}

	public boolean hasItems() {
		return !rowItems.isEmpty();
	}
	
}