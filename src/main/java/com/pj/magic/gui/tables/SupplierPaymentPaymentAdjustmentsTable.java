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
import javax.swing.table.AbstractTableModel;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.pj.magic.gui.component.AmountCellEditor;
import com.pj.magic.gui.component.MagicCellEditor;
import com.pj.magic.gui.component.MagicTextField;
import com.pj.magic.gui.dialog.SelectAdjustmentTypeDialog;
import com.pj.magic.gui.tables.models.SupplierPaymentPaymentAdjustmentsTableModel;
import com.pj.magic.gui.tables.rowitems.SupplierPaymentAdjustmentRowItem;
import com.pj.magic.model.AdjustmentType;
import com.pj.magic.model.SupplierPayment;
import com.pj.magic.model.SupplierPaymentAdjustment;
import com.pj.magic.model.SupplierPaymentPaymentAdjustment;
import com.pj.magic.service.AdjustmentTypeService;
import com.pj.magic.service.SupplierPaymentAdjustmentService;

@Component
public class SupplierPaymentPaymentAdjustmentsTable extends MagicTable {
	
	public static final int ADJUSTMENT_TYPE_COLUMN_INDEX = 0;
	public static final int REFERENCE_NUMBER_COLUMN_INDEX = 1;
	public static final int AMOUNT_COLUMN_INDEX = 2;
	private static final String CANCEL_ACTION_NAME = "cancelAddMode";
	private static final String DELETE_ITEM_ACTION_NAME = "deleteItem";
	private static final String F10_ACTION_NAME = "F10";
	private static final String F5_ACTION_NAME = "F5";

	@Autowired private SupplierPaymentPaymentAdjustmentsTableModel tableModel;
	@Autowired private AdjustmentTypeService adjustmentTypeService;
	@Autowired private SupplierPaymentAdjustmentService supplierPaymentAdjustmentService;
	@Autowired private SelectAdjustmentTypeDialog selectAdjustmentTypeDialog;
	
	private SupplierPayment payment;
	
	@Autowired
	public SupplierPaymentPaymentAdjustmentsTable(SupplierPaymentPaymentAdjustmentsTableModel tableModel) {
		super(tableModel);
		initializeColumns();
		initializeModelListener();
		registerKeyBindings();
	}
	
	private void initializeColumns() {
		MagicTextField adjustmentTypeTextField = new MagicTextField();
		adjustmentTypeTextField.setMaximumLength(20);
		columnModel.getColumn(ADJUSTMENT_TYPE_COLUMN_INDEX)
			.setCellEditor(new AdjustmentTypeCellEditor(adjustmentTypeTextField));

		MagicTextField referenceNumberTextField = new MagicTextField();
		referenceNumberTextField.setMaximumLength(20);
		referenceNumberTextField.setNumbersOnly(true);
		columnModel.getColumn(REFERENCE_NUMBER_COLUMN_INDEX)
			.setCellEditor(new ReferenceNumberCellEditor(referenceNumberTextField));
		
		MagicTextField amountTextField = new MagicTextField();
		amountTextField.setMaximumLength(12);
		columnModel.getColumn(AMOUNT_COLUMN_INDEX).setCellEditor(new AmountCellEditor(amountTextField));

		MagicTextField remarksTextField = new MagicTextField();
		remarksTextField.setMaximumLength(100);
	}
	
	public void addNewRow() {
		if (getRowCount() > 0 && !tableModel.getRowItem(getRowCount() - 1).isUpdating()) {
			return;
		}
		
		SupplierPaymentPaymentAdjustment adjustment = new SupplierPaymentPaymentAdjustment();
		adjustment.setParent(payment);
		tableModel.addItem(adjustment);
		selectAndEditCellAt(getRowCount() - 1, 0);
	}
	
	public boolean isLastRowSelected() {
		return getSelectedRow() + 1 == tableModel.getRowCount();
	}

	public void doDeleteCurrentlySelectedItem() {
		int selectedRowIndex = getSelectedRow();
		SupplierPaymentPaymentAdjustment paymentAdjustment = getCurrentlySelectedRowItem().getPaymentAdjustment();
		clearSelection(); // clear row selection so model listeners will not cause exceptions while model items are being updated
		payment.getPaymentAdjustments().remove(paymentAdjustment);
		tableModel.removeItem(selectedRowIndex);
		
		if (tableModel.hasItems()) {
			if (selectedRowIndex == getModel().getRowCount()) {
				changeSelection(selectedRowIndex - 1, 0, false, false);
			} else {
				changeSelection(selectedRowIndex, 0, false, false);
			}
		}
	}
	
	public void setSupplierPayment(SupplierPayment supplierPayment) {
		clearSelection();
		this.payment = supplierPayment;
		tableModel.setSupplierPayment(supplierPayment);
	}
	
	protected void registerKeyBindings() {
		InputMap inputMap = getInputMap(JTable.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
		inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), CANCEL_ACTION_NAME);
		inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_F10, 0), F10_ACTION_NAME);
		inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_F5, 0), F5_ACTION_NAME);
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
		actionMap.put(F5_ACTION_NAME, new AbstractAction() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				openSelectAdjustmentTypeDialog();
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
	
	private void openSelectAdjustmentTypeDialog() {
		if (!payment.isNew()) {
			return;
		}
		
		if (getSelectedColumn() == ADJUSTMENT_TYPE_COLUMN_INDEX) {
			if (!isEditing()) {
				editCellAtCurrentLocation();
			}
			
			selectAdjustmentTypeDialog.updateDisplay();
			selectAdjustmentTypeDialog.setVisible(true);
			
			AdjustmentType adjustmentType = selectAdjustmentTypeDialog.getSelectedAdjustmentType();
			if (adjustmentType != null) {
				((JTextField)getEditorComponent()).setText(adjustmentType.getCode());
				getCellEditor().stopCellEditing();
			}
		}
	}

	private void cancelEditing() {
		if (getSelectedRow() == -1) {
			return;
		}
		
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

	public SupplierPaymentAdjustmentRowItem getCurrentlySelectedRowItem() {
		return tableModel.getRowItem(getSelectedRow());
	}
	
	public void removeCurrentlySelectedItem() {
		if (getSelectedRow() != -1) {
			if (getCurrentlySelectedRowItem().isValid()) { // check valid row to prevent deleting the blank row
				if (confirm("Do you wish to delete the selected item?")) {
					doDeleteCurrentlySelectedItem();
				}
			}
		}
	}

	private void initializeModelListener() {
		getModel().addTableModelListener(new TableModelListener() {
			
			@Override
			public void tableChanged(TableModelEvent e) {
				final AbstractTableModel model = (AbstractTableModel)e.getSource();
				final int row = e.getFirstRow();
				final int column = e.getColumn();
				
				SwingUtilities.invokeLater(new Runnable() {
					
					@Override
					public void run() {
						switch (column) {
						case ADJUSTMENT_TYPE_COLUMN_INDEX:
							selectAndEditCellAt(row, REFERENCE_NUMBER_COLUMN_INDEX);
							model.fireTableRowsUpdated(row, row);
							break;
						case REFERENCE_NUMBER_COLUMN_INDEX:
							selectAndEditCellAt(row, AMOUNT_COLUMN_INDEX);
							model.fireTableRowsUpdated(row, row);
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

	public void clearDisplay() {
		payment = null;
		tableModel.setSupplierPayment(null);
	}
	
	public class ReferenceNumberCellEditor extends MagicCellEditor {

		public ReferenceNumberCellEditor(JTextField textField) {
			super(textField);
		}
		
		@Override
		public boolean stopCellEditing() {
			SupplierPaymentAdjustmentRowItem rowItem = getCurrentlySelectedRowItem();
			String referenceNumberString = ((JTextField)getComponent()).getText();
			boolean valid = false;
			if (StringUtils.isEmpty(referenceNumberString)) {
				showErrorMessage("Reference number must be specified");
			} else {
				long referenceNumber = Long.parseLong(referenceNumberString);
				valid = validatePaymentAdjustment(rowItem.getAdjustmentType(), referenceNumber);
			}
			return (valid) ? super.stopCellEditing() : false;
		}

	}

	private boolean validatePaymentAdjustment(AdjustmentType adjustmentType, long paymentAdjustmentNumber) {
		boolean valid = false;
		SupplierPaymentAdjustment paymentAdjustment = supplierPaymentAdjustmentService
				.findSupplierPaymentAdjustmentBySupplierPaymentAdjustmentNumber(paymentAdjustmentNumber);
		if (paymentAdjustment == null || !paymentAdjustment.getAdjustmentType().equals(adjustmentType)) {
			showErrorMessage("Supplier Payment Adjustment does not exist");
		} else if (!paymentAdjustment.isPosted()) {
			showErrorMessage("Supplier Payment Adjustment is not yet posted");
		} else {
			valid = true;
		}
		return valid;
	}

	private class AdjustmentTypeCellEditor extends MagicCellEditor {

		public AdjustmentTypeCellEditor(JTextField textField) {
			super(textField);
		}
		
		@Override
		public boolean stopCellEditing() {
			String code = ((JTextField)getComponent()).getText();
			boolean valid = false;
			if (StringUtils.isEmpty(code)) {
				showErrorMessage("Adjustment Type must be specified");
			} else if (adjustmentTypeService.findAdjustmentTypeByCode(code) == null) {
				showErrorMessage("No Adjustment Type matching code specified");
			} else {
				valid = true;
			}
			return (valid) ? super.stopCellEditing() : false;
		}

	}
	
}