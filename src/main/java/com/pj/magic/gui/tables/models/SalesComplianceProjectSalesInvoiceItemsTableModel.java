package com.pj.magic.gui.tables.models;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.pj.magic.gui.tables.SalesComplianceProjectSalesInvoiceItemsTable;
import com.pj.magic.gui.tables.rowitems.SalesComplianceProjectSalesInvoiceItemRowItem;
import com.pj.magic.model.SalesComplianceProjectSalesInvoice;
import com.pj.magic.model.SalesComplianceProjectSalesInvoiceItem;
import com.pj.magic.service.SalesComplianceService;
import com.pj.magic.util.FormatterUtil;

@Component
public class SalesComplianceProjectSalesInvoiceItemsTableModel extends AbstractTableModel {
	
	private static final String[] columnNames = 
		{"Code", "Description", "Unit", "Orig. Qty", "Quantity", "Cost w/ VAT", "Amount", "Disc. 1", "Disc. 2", "Disc. 3",
				"Flat Rate", "Disc. Amount", "Net Amount"};
	
	@Autowired private SalesComplianceService salesComplianceService;
	
	private List<SalesComplianceProjectSalesInvoiceItemRowItem> rowItems = new ArrayList<>();
	
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
		SalesComplianceProjectSalesInvoiceItemRowItem rowItem = rowItems.get(rowIndex);
		SalesComplianceProjectSalesInvoiceItem item = rowItem.getItem();
		switch (columnIndex) {
		case SalesComplianceProjectSalesInvoiceItemsTable.PRODUCT_CODE_COLUMN_INDEX:
			return item.getProduct().getCode();
		case SalesComplianceProjectSalesInvoiceItemsTable.PRODUCT_DESCRIPTION_COLUMN_INDEX:
			return item.getProduct().getDescription();
		case SalesComplianceProjectSalesInvoiceItemsTable.UNIT_COLUMN_INDEX:
			return item.getUnit();
		case SalesComplianceProjectSalesInvoiceItemsTable.ORIGINAL_QUANTITY_COLUMN_INDEX:
			return item.getOriginalQuantity().toString();
		case SalesComplianceProjectSalesInvoiceItemsTable.QUANTITY_COLUMN_INDEX:
			return item.getQuantity().toString();
		case SalesComplianceProjectSalesInvoiceItemsTable.COST_WITH_VAT_COLUMN_INDEX:
			return FormatterUtil.formatAmount(item.getCost().multiply(new BigDecimal("1.12")));
		case SalesComplianceProjectSalesInvoiceItemsTable.AMOUNT_COLUMN_INDEX:
			return FormatterUtil.formatAmount(item.getAmount());
		case SalesComplianceProjectSalesInvoiceItemsTable.DISCOUNT_1_COLUMN_INDEX:
			return item.getDiscount1() != null ? FormatterUtil.formatAmount(item.getDiscount1()) : null;
		case SalesComplianceProjectSalesInvoiceItemsTable.DISCOUNT_2_COLUMN_INDEX:
			return item.getDiscount2() != null ? FormatterUtil.formatAmount(item.getDiscount2()) : null;
		case SalesComplianceProjectSalesInvoiceItemsTable.DISCOUNT_3_COLUMN_INDEX:
			return item.getDiscount3() != null ? FormatterUtil.formatAmount(item.getDiscount3()) : null;
		case SalesComplianceProjectSalesInvoiceItemsTable.FLAT_RATE_COLUMN_INDEX:
			return item.getFlatRateDiscount() != null ? FormatterUtil.formatAmount(item.getFlatRateDiscount()) : null;
		case SalesComplianceProjectSalesInvoiceItemsTable.DISCOUNTED_AMOUNT_COLUMN_INDEX:
			if (rowItem.isValid()) {
				return FormatterUtil.formatAmount(item.getDiscountedAmount());
			} else {
				return "";
			}
		case SalesComplianceProjectSalesInvoiceItemsTable.NET_AMOUNT_COLUMN_INDEX:
			if (rowItem.isValid()) {
				return FormatterUtil.formatAmount(item.getNetAmount());
			} else {
				return "";
			}
		default:
			throw new RuntimeException("Fetching invalid column index: " + columnIndex);
		}
	}
	
	@Override
	public String getColumnName(int columnIndex) {
		return columnNames[columnIndex];
	}

	public List<SalesComplianceProjectSalesInvoiceItem> getItems() {
		List<SalesComplianceProjectSalesInvoiceItem> items = new ArrayList<>();
		for (SalesComplianceProjectSalesInvoiceItemRowItem rowItem : this.rowItems) {
			if (rowItem.isValid()) {
				items.add(rowItem.getItem());
			}
		}
		return items;
	}
	
	public void setItems(List<SalesComplianceProjectSalesInvoiceItem> items) {
		this.rowItems.clear();
		for (SalesComplianceProjectSalesInvoiceItem item : items) {
			this.rowItems.add(new SalesComplianceProjectSalesInvoiceItemRowItem(item));
		}
		fireTableDataChanged();
	}
	
	@Override
	public void setValueAt(Object value, int rowIndex, int columnIndex) {
		SalesComplianceProjectSalesInvoiceItemRowItem rowItem = rowItems.get(rowIndex);
		String val = (String)value;
		switch (columnIndex) {
		case SalesComplianceProjectSalesInvoiceItemsTable.QUANTITY_COLUMN_INDEX:
			rowItem.setQuantity(val);
			break;
		}
		
		if (rowItem.isValid()) {
			SalesComplianceProjectSalesInvoiceItem item = rowItem.getItem();
			item.setQuantity(Integer.valueOf(rowItem.getQuantity()));
			salesComplianceService.save(item);
		}
		fireTableCellUpdated(rowIndex, columnIndex);
	}
	
	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		return columnIndex == SalesComplianceProjectSalesInvoiceItemsTable.QUANTITY_COLUMN_INDEX;
	}
	
	public SalesComplianceProjectSalesInvoiceItemRowItem getRowItem(int rowIndex) {
		return rowItems.get(rowIndex);
	}
	
	public boolean hasItems() {
		return !rowItems.isEmpty();
	}
	
	public boolean isValid(int rowIndex) {
		return rowItems.get(rowIndex).isValid();
	}
	
	public void setSalesInvoice(SalesComplianceProjectSalesInvoice salesInvoice) {
		setItems(salesInvoice.getItems());
	}

}