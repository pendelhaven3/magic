package com.pj.magic.gui.tables;

import javax.swing.JTable;
import javax.swing.table.TableColumnModel;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.pj.magic.gui.tables.models.SalesInvoiceItemsTableModel;
import com.pj.magic.model.SalesInvoice;
import com.pj.magic.model.SalesInvoiceItem;
import com.pj.magic.service.ProductService;

@Component
public class SalesInvoiceItemsTable extends JTable {
	
	public static final int PRODUCT_CODE_COLUMN_INDEX = 0;
	public static final int PRODUCT_DESCRIPTION_COLUMN_INDEX = 1;
	public static final int UNIT_COLUMN_INDEX = 2;
	public static final int QUANTITY_COLUMN_INDEX = 3;
	public static final int UNIT_PRICE_COLUMN_INDEX = 4;
	public static final int AMOUNT_COLUMN_INDEX = 5;

	@Autowired private ProductService productService;
	
	private SalesInvoice salesInvoice;
	
	@Autowired
	public SalesInvoiceItemsTable(SalesInvoiceItemsTableModel tableModel) {
		super(tableModel);
		initializeColumns();
	}
	
	private void initializeColumns() {
		TableColumnModel columnModel = getColumnModel();
		columnModel.getColumn(PRODUCT_CODE_COLUMN_INDEX).setPreferredWidth(120);
		columnModel.getColumn(PRODUCT_DESCRIPTION_COLUMN_INDEX).setPreferredWidth(300);
		columnModel.getColumn(UNIT_COLUMN_INDEX).setPreferredWidth(70);
		columnModel.getColumn(QUANTITY_COLUMN_INDEX).setPreferredWidth(70);
		columnModel.getColumn(UNIT_PRICE_COLUMN_INDEX).setPreferredWidth(100);
		columnModel.getColumn(AMOUNT_COLUMN_INDEX).setPreferredWidth(100);
	}
	
	public SalesInvoiceItemsTableModel getItemsTableModel() {
		return (SalesInvoiceItemsTableModel)super.getModel();
	}
	
	public SalesInvoiceItem getCurrentlySelectedRowItem() {
		return getItemsTableModel().getRowItem(getSelectedRow());
	}
	
	public void setSalesInvoice(SalesInvoice salesInvoice) {
		this.salesInvoice = salesInvoice;
		getItemsTableModel().setItems(salesInvoice.getItems());
	}
	
}