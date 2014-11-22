package com.pj.magic.gui.tables;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.pj.magic.gui.component.MagicCellEditor;
import com.pj.magic.gui.component.MagicTextField;
import com.pj.magic.gui.tables.models.PaymentCheckPaymentsTableModel;
import com.pj.magic.gui.tables.rowitems.PaymentCheckPaymentRowItem;
import com.pj.magic.model.Payment;
import com.pj.magic.model.PaymentCheckPayment;
import com.pj.magic.util.NumberUtil;

@Component
public class PaymentCheckPaymentsTable extends MagicTable {
	
	public static final int BANK_COLUMN_INDEX = 0;
	public static final int CHECK_NUMBER_COLUMN_INDEX = 1;
	public static final int AMOUNT_COLUMN_INDEX = 2;
	private static final String CANCEL_ACTION_NAME = "cancelAddMode";
	private static final String DELETE_ITEM_ACTION_NAME = "deleteItem";
	private static final String F10_ACTION_NAME = "F10";

	@Autowired private PaymentCheckPaymentsTableModel tableModel;
	
	private Payment payment;
	
	@Autowired
	public PaymentCheckPaymentsTable(PaymentCheckPaymentsTableModel tableModel) {
		super(tableModel);
		initializeColumns();
		initializeModelListener();
		registerKeyBindings();
	}
	
	private void initializeColumns() {
		MagicTextField bankTextField = new MagicTextField();
		bankTextField.setMaximumLength(30);
		columnModel.getColumn(BANK_COLUMN_INDEX).setCellEditor(
				new RequiredFieldCellEditor(bankTextField, "Bank"));

		MagicTextField checkNumberTextField = new MagicTextField();
		checkNumberTextField.setMaximumLength(50);
		columnModel.getColumn(CHECK_NUMBER_COLUMN_INDEX).setCellEditor(
				new RequiredFieldCellEditor(checkNumberTextField, "Check Number"));
		
		MagicTextField amountTextField = new MagicTextField();
		amountTextField.setMaximumLength(12);
		columnModel.getColumn(AMOUNT_COLUMN_INDEX).setCellEditor(new AmountCellEditor(amountTextField));
	}
	
	public void addNewRow() {
		if (getRowCount() > 0 && !tableModel.getRowItem(getRowCount() - 1).isUpdating()) {
			return;
		}
		
		PaymentCheckPayment check = new PaymentCheckPayment();
		check.setParent(payment);
		tableModel.addItem(check);
		selectAndEditCellAt(getRowCount() - 1, 0);
	}
	
	public boolean isLastRowSelected() {
		return getSelectedRow() + 1 == tableModel.getRowCount();
	}

	public void doDeleteCurrentlySelectedItem() {
		/*
		int selectedRowIndex = getSelectedRow();
		AdjustmentInItem item = getCurrentlySelectedRowItem().getItem();
		clearSelection(); // clear row selection so model listeners will not cause exceptions while model items are being updated
		adjustmentIn.getItems().remove(item);
		tableModel.removeItem(selectedRowIndex);
		
		if (tableModel.hasItems()) {
			if (selectedRowIndex == getModel().getRowCount()) {
				changeSelection(selectedRowIndex - 1, 0, false, false);
			} else {
				changeSelection(selectedRowIndex, 0, false, false);
			}
		}
		*/
	}
	
//	public AdjustmentInItemRowItem getCurrentlySelectedRowItem() {
//		return tableModel.getRowItem(getSelectedRow());
//	}
	
	public void setPayment(Payment payment) {
		clearSelection();
		this.payment = payment;
		tableModel.setChecks(payment.getChecks());
	}
	
	protected void registerKeyBindings() {
		InputMap inputMap = getInputMap(JTable.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
		inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), CANCEL_ACTION_NAME);
		inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_F10, 0), F10_ACTION_NAME);
		/*
		inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0), DELETE_ITEM_ACTION_NAME);
		
		*/
		ActionMap actionMap = getActionMap();
		actionMap.put(CANCEL_ACTION_NAME, new AbstractAction() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				cancelEditing();
			}
		});
		actionMap.put(F10_ACTION_NAME, new AbstractAction() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				addNewRow();
			}
		});
		/*
		actionMap.put(DELETE_ITEM_ACTION_NAME, new AbstractAction() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				removeCurrentlySelectedItem();
			}
		});
		*/
		
	}
	
	private void cancelEditing() {
		if (isEditing()) {
			getCellEditor().cancelCellEditing();
			if (getCurrentlySelectedRowItem().isUpdating()) {
				tableModel.reset(getSelectedRow());
			}
		} else if (isLastRowSelected() && !getCurrentlySelectedRowItem().isUpdating()) {
			int selectedRow = getSelectedRow();
			clearSelection();
			tableModel.removeItem(selectedRow);
			if (getRowCount() > 0) {
				changeSelection(getRowCount() - 1, 0, false, false);
			}
		}
	}

	public PaymentCheckPaymentRowItem getCurrentlySelectedRowItem() {
		return tableModel.getRowItem(getSelectedRow());
	}
	
	public void removeCurrentlySelectedItem() {
//		if (getSelectedRow() != -1) {
//			if (getCurrentlySelectedRowItem().isValid()) { // check valid row to prevent deleting the blank row
//				if (confirm("Do you wish to delete the selected item?")) {
//					doDeleteCurrentlySelectedItem();
//				}
//			}
//		}
	}

	public void highlight() {
//		if (!adjustmentIn.hasItems()) {
//			switchToAddMode();
//		} else {
//			changeSelection(0, 0, false, false);
//			requestFocusInWindow();
//		}
	}
	
	private void initializeModelListener() {
		getModel().addTableModelListener(new TableModelListener() {
			
			@Override
			public void tableChanged(TableModelEvent e) {
				final int row = e.getFirstRow();
				final int column = e.getColumn();
				
				SwingUtilities.invokeLater(new Runnable() {
					
					@Override
					public void run() {
						switch (column) {
						case BANK_COLUMN_INDEX:
							selectAndEditCellAt(row, CHECK_NUMBER_COLUMN_INDEX);
							break;
						case CHECK_NUMBER_COLUMN_INDEX:
							selectAndEditCellAt(row, AMOUNT_COLUMN_INDEX);
							break;
						case AMOUNT_COLUMN_INDEX:
							if (isLastRowSelected()) {
								addNewRow();
							}
							break;
						}
					}
				});
			}
		});
	}

	private class RequiredFieldCellEditor extends MagicCellEditor {
		
		private String fieldName;
		
		public RequiredFieldCellEditor(JTextField textField, String fieldName) {
			super(textField);
			this.fieldName = fieldName;
		}
		
		@Override
		public boolean stopCellEditing() {
			String amount = ((JTextField)getComponent()).getText();
			boolean valid = false;
			if (StringUtils.isEmpty(amount)) {
				showErrorMessage(fieldName + " must be specified");
			} else {
				valid = true;
			}
			return (valid) ? super.stopCellEditing() : false;
		}
		
	}

	private class AmountCellEditor extends MagicCellEditor {
		
		public AmountCellEditor(JTextField textField) {
			super(textField);
		}
		
		@Override
		public boolean stopCellEditing() {
			String amount = ((JTextField)getComponent()).getText();
			boolean valid = false;
			if (StringUtils.isEmpty(amount)) {
				showErrorMessage("Amount must be specified");
			} else if (!NumberUtil.isAmount(amount)) {
				showErrorMessage("Amount must be a valid amount");
			} else {
				valid = true;
			}
			return (valid) ? super.stopCellEditing() : false;
		}
		
	}
	
}