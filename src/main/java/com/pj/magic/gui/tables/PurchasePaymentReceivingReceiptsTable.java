package com.pj.magic.gui.tables;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.pj.magic.gui.tables.models.PurchasePaymentReceivingReceiptsTableModel;
import com.pj.magic.model.PurchasePayment;
import com.pj.magic.model.PurchasePaymentReceivingReceipt;


@Component
public class PurchasePaymentReceivingReceiptsTable extends MagicTable {

	public static final int RECEIVED_DATE_COLUMN_INDEX = 0;
	public static final int RECEIVING_RECEIPT_NUMBER_COLUMN_INDEX = 1;
	public static final int NET_AMOUNT_COLUMN_INDEX = 2;
	
	@Autowired private PurchasePaymentReceivingReceiptsTableModel tableModel;
	
	private PurchasePayment purchasePayment;
	
	@Autowired
	public PurchasePaymentReceivingReceiptsTable(PurchasePaymentReceivingReceiptsTableModel tableModel) {
		super(tableModel);
	}
	
	public void setPurchasePayment(PurchasePayment purchasePayment) {
		this.purchasePayment = purchasePayment;
		tableModel.setPurchasePayment(purchasePayment);
	}

	public void clearDisplay() {
		tableModel.setPurchasePayment(null);
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
		PurchasePaymentReceivingReceipt paymentReceivingReceipt = tableModel.getPaymentReceivingReceipt(selectedRowIndex);
		clearSelection();
		purchasePayment.getReceivingReceipts().remove(paymentReceivingReceipt);
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