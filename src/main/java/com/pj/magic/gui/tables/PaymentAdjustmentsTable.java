package com.pj.magic.gui.tables;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.Arrays;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JComboBox;
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
import com.pj.magic.gui.component.RequiredFieldCellEditor;
import com.pj.magic.gui.tables.models.PaymentAdjustmentsTableModel;
import com.pj.magic.gui.tables.rowitems.PaymentAdjustmentRowItem;
import com.pj.magic.model.BadStockReturn;
import com.pj.magic.model.Payment;
import com.pj.magic.model.PaymentAdjustment;
import com.pj.magic.model.SalesReturn;
import com.pj.magic.service.BadStockReturnService;
import com.pj.magic.service.SalesReturnService;

@Component
public class PaymentAdjustmentsTable extends MagicTable {
	
	public static final int ADJUSTMENT_TYPE_COLUMN_INDEX = 0;
	public static final int REFERENCE_NUMBER_COLUMN_INDEX = 1;
	public static final int AMOUNT_COLUMN_INDEX = 2;
	private static final String CANCEL_ACTION_NAME = "cancelAddMode";
	private static final String DELETE_ITEM_ACTION_NAME = "deleteItem";
	private static final String F10_ACTION_NAME = "F10";

	@Autowired private PaymentAdjustmentsTableModel tableModel;
	@Autowired private SalesReturnService salesReturnService;
	@Autowired private BadStockReturnService badStockReturnService;
	
	private Payment payment;
	private JComboBox<String> adjustmentTypeComboBox;
	
	@Autowired
	public PaymentAdjustmentsTable(PaymentAdjustmentsTableModel tableModel) {
		super(tableModel);
		initializeColumns();
		initializeModelListener();
		registerKeyBindings();
	}
	
	private void initializeColumns() {
		MagicTextField adjustmentTypeTextField = new MagicTextField();
		adjustmentTypeTextField.setMaximumLength(20);
		columnModel.getColumn(ADJUSTMENT_TYPE_COLUMN_INDEX)
			.setCellEditor(new RequiredFieldCellEditor(adjustmentTypeTextField, "Adjustment Type"));

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
		
		PaymentAdjustment adjustment = new PaymentAdjustment();
		adjustment.setParent(payment);
		tableModel.addItem(adjustment);
		selectAndEditCellAt(getRowCount() - 1, 0);
	}
	
	public boolean isLastRowSelected() {
		return getSelectedRow() + 1 == tableModel.getRowCount();
	}

	public void doDeleteCurrentlySelectedItem() {
		int selectedRowIndex = getSelectedRow();
		PaymentAdjustment item = getCurrentlySelectedRowItem().getAdjustment();
		clearSelection(); // clear row selection so model listeners will not cause exceptions while model items are being updated
		payment.getAdjustments().remove(item);
		tableModel.removeItem(selectedRowIndex);
		
		if (tableModel.hasItems()) {
			if (selectedRowIndex == getModel().getRowCount()) {
				changeSelection(selectedRowIndex - 1, 0, false, false);
			} else {
				changeSelection(selectedRowIndex, 0, false, false);
			}
		}
	}
	
	public void setPayment(Payment payment) {
		clearSelection();
		this.payment = payment;
		tableModel.setPayment(payment);
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

	public PaymentAdjustmentRowItem getCurrentlySelectedRowItem() {
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
		tableModel.setPayment(null);
	}
	
	public class ReferenceNumberCellEditor extends MagicCellEditor {

		private final List<String> specialLogicAdjustmentTypes = 
				Arrays.asList(PaymentAdjustment.SALES_RETURN_TYPE, PaymentAdjustment.BAD_STOCK_RETURN_TYPE);
		
		public ReferenceNumberCellEditor(JTextField textField) {
			super(textField);
		}
		
		@Override
		public boolean stopCellEditing() {
			PaymentAdjustmentRowItem rowItem = getCurrentlySelectedRowItem();
			if (!specialLogicAdjustmentTypes.contains(rowItem.getAdjustmentType())) {
				return super.stopCellEditing();
			}
			
			String referenceNumberString = ((JTextField)getComponent()).getText();
			boolean valid = false;
			if (StringUtils.isEmpty(referenceNumberString)) {
				showErrorMessage("Reference number must be specified");
			} else {
				long referenceNumber = Long.parseLong(referenceNumberString);
				switch (rowItem.getAdjustmentType()) {
				case PaymentAdjustment.SALES_RETURN_TYPE:
					valid = validateSalesReturn(referenceNumber);
					break;
				case PaymentAdjustment.BAD_STOCK_RETURN_TYPE:
					valid = validateBadStockReturn(referenceNumber);
					break;
				}
			}
			return (valid) ? super.stopCellEditing() : false;
		}

	}

	private boolean validateBadStockReturn(long badStockReturnNumber) {
		boolean valid = false;
		BadStockReturn badStockReturn = badStockReturnService
				.findBadStockReturnByBadStockReturnNumber(badStockReturnNumber);
		if (badStockReturn == null) {
			showErrorMessage("Bad Stock Return does not exist");
		} else if (badStockReturn.isPaid()) {
			showErrorMessage("Bad Stock Return is already paid");
		} else if (!badStockReturn.isPosted()) {
			showErrorMessage("Bad Stock Return is not yet posted");
		} else {
			valid = true;
		}
		return valid;
	}

	private boolean validateSalesReturn(long salesReturnNumber) {
		boolean valid = false;
		SalesReturn salesReturn = salesReturnService
				.findSalesReturnBySalesReturnNumber(salesReturnNumber);
		if (salesReturn == null) {
			showErrorMessage("Sales Return does not exist");
		} else if (salesReturn.isPaid()) {
			showErrorMessage("Sales Return is already paid");
		} else if (!salesReturn.isPosted()) {
			showErrorMessage("Sales Return is not yet posted");
		} else {
			valid = true;
		}
		return valid;
	}
	
}