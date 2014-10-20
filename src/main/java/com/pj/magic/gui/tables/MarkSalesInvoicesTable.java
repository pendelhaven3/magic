package com.pj.magic.gui.tables;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.swing.JTable;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.pj.magic.gui.tables.models.MarkSalesInvoicesTableModel;
import com.pj.magic.model.SalesInvoice;
import com.pj.magic.service.SalesInvoiceService;

@Component
public class MarkSalesInvoicesTable extends JTable {

	public static final int SALES_INVOICE_NUMBER_COLUMN_INDEX = 0;
	public static final int SALES_REQUISITION_NUMBER_COLUMN_INDEX = 1;
	public static final int TRANSACTION_DATE_COLUMN_INDEX = 2;
	public static final int CUSTOMER_NAME_COLUMN_INDEX = 3;
	public static final int ENCODER_COLUMN_INDEX = 4;
	public static final int NET_AMOUNT_COLUMN_INDEX = 5;
	public static final int MARK_COLUMN_INDEX = 6;
	public static final int CANCEL_COLUMN_INDEX = 7;

	@Autowired private SalesInvoiceService salesInvoiceService;
	
	private MarkSalesInvoicesTableModel tableModel = new MarkSalesInvoicesTableModel();
	
	@PostConstruct
	public void initialize() {
		setModel(tableModel);
		initializeColumns();
		registerKeyBindings();
    }
	
	private void initializeColumns() {
	}

	public void update() {
		List<SalesInvoice> salesInvoices = salesInvoiceService.getNewSalesInvoices();
		tableModel.setSalesInvoices(salesInvoices);
		if (!salesInvoices.isEmpty()) {
			changeSelection(0, 0, false, false);
		}
	}
	
	private void registerKeyBindings() {
		// none
	}

	public List<SalesInvoice> getSalesInvoices() {
		return tableModel.getSalesInvoices();
	}
	
}
