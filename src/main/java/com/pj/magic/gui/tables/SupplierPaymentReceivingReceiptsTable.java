package com.pj.magic.gui.tables;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.pj.magic.gui.tables.models.SupplierPaymentReceivingReceiptsTableModel;
import com.pj.magic.model.SupplierPayment;
import com.pj.magic.model.SupplierPaymentReceivingReceipt;


@Component
public class SupplierPaymentReceivingReceiptsTable extends MagicTable {

	public static final int RECEIVED_DATE_COLUMN_INDEX = 0;
	public static final int RECEIVING_RECEIPT_NUMBER_COLUMN_INDEX = 1;
	public static final int NET_AMOUNT_COLUMN_INDEX = 2;
	
	@Autowired private SupplierPaymentReceivingReceiptsTableModel tableModel;
	
	private SupplierPayment supplierPayment;
	
	@Autowired
	public SupplierPaymentReceivingReceiptsTable(SupplierPaymentReceivingReceiptsTableModel tableModel) {
		super(tableModel);
	}
	
	public void setSupplierPayment(SupplierPayment supplierPayment) {
		this.supplierPayment = supplierPayment;
		tableModel.setSupplierPayment(supplierPayment);
	}

	public void clearDisplay() {
		tableModel.setSupplierPayment(null);
	}
	
	public void removeCurrentlySelectedItem() {
		if (getSelectedRow() != -1) {
			if (confirm("Do you wish to delete the selected item?")) {
				doDeleteCurrentlySelectedItem();
			}
		}
	}

	private void doDeleteCurrentlySelectedItem() {
		int selectedRowIndex = getSelectedRow();
		SupplierPaymentReceivingReceipt paymentReceivingReceipt = tableModel.getPaymentReceivingReceipt(selectedRowIndex);
		clearSelection();
		supplierPayment.getReceivingReceipts().remove(paymentReceivingReceipt);
		tableModel.removeItem(paymentReceivingReceipt);
		
		if (tableModel.hasItems()) {
			if (selectedRowIndex == getModel().getRowCount()) {
				changeSelection(selectedRowIndex - 1, 0, false, false);
			} else {
				changeSelection(selectedRowIndex, 0, false, false);
			}
		}
	}
	
}