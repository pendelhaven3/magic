package com.pj.magic.gui.tables;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.pj.magic.gui.tables.models.PaymentSalesInvoicesTableModel;
import com.pj.magic.model.PaymentSalesInvoice;


@Component
public class PaymentSalesInvoicesTable extends MagicTable {

	public static final int SALES_INVOICE_NUMBER_COLUMN_INDEX = 0;
	public static final int ADJUSTMENT_AMOUNT_COLUMN_INDEX = 1;
	public static final int AMOUNT_COLUMN_INDEX = 2;
	
	@Autowired private PaymentSalesInvoicesTableModel tableModel;
	
	@Autowired
	public PaymentSalesInvoicesTable(PaymentSalesInvoicesTableModel tableModel) {
		super(tableModel);
	}
	
	public void setPaymentSalesInvoices(List<PaymentSalesInvoice> paymentSalesInvoices) {
		tableModel.setPaymentSalesInvoices(paymentSalesInvoices);
	}

	public void clearDisplay() {
		tableModel.setPaymentSalesInvoices(new ArrayList<PaymentSalesInvoice>());
	}
	
}
