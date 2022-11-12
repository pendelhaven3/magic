package com.pj.magic.gui.tables;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.Date;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.DefaultCellEditor;
import javax.swing.DefaultComboBoxModel;
import javax.swing.InputMap;
import javax.swing.JComboBox;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.pj.magic.gui.component.AmountCellEditor;
import com.pj.magic.gui.component.DateCellEditor;
import com.pj.magic.gui.component.MagicComboBox;
import com.pj.magic.gui.component.MagicTextField;
import com.pj.magic.gui.dialog.SelectDateDialog;
import com.pj.magic.gui.tables.models.PaymentEcashPaymentsTableModel;
import com.pj.magic.gui.tables.rowitems.PaymentEcashPaymentRowItem;
import com.pj.magic.model.EcashReceiver;
import com.pj.magic.model.Payment;
import com.pj.magic.model.PaymentEcashPayment;
import com.pj.magic.model.User;
import com.pj.magic.service.EcashReceiverService;
import com.pj.magic.service.UserService;
import com.pj.magic.util.FormatterUtil;

@Component
public class PaymentEcashPaymentsTable extends MagicTable {
	
	public static final int AMOUNT_COLUMN_INDEX = 0;
	public static final int ECASH_RECEIVER_COLUMN_INDEX = 1;
	public static final int REFERENCE_NUMBER_COLUMN_INDEX = 2;
	public static final int RECEIVED_DATE_COLUMN_INDEX = 3;
	public static final int RECEIVED_BY_COLUMN_INDEX = 4;
	private static final String CANCEL_ACTION_NAME = "cancelAddMode";
	private static final String DELETE_ITEM_ACTION_NAME = "deleteItem";
	private static final String F10_ACTION_NAME = "F10";
	private static final String F5_ACTION_NAME = "F5";

	@Autowired private PaymentEcashPaymentsTableModel tableModel;
	@Autowired private UserService userService;
	@Autowired private EcashReceiverService ecashReceiverService;
	@Autowired private SelectDateDialog selectDateDialog;
	
	private Payment payment;
	private JComboBox<EcashReceiver> ecashReceiverComboBox;
	private JComboBox<User> receivedByComboBox;
	
	@Autowired
	public PaymentEcashPaymentsTable(PaymentEcashPaymentsTableModel tableModel) {
		super(tableModel);
		initializeColumns();
		initializeModelListener();
		registerKeyBindings();
	}
	
	private void initializeColumns() {
		MagicTextField amountTextField = new MagicTextField();
		amountTextField.setMaximumLength(12);
		columnModel.getColumn(AMOUNT_COLUMN_INDEX).setCellEditor(new AmountCellEditor(amountTextField));

		ecashReceiverComboBox = new MagicComboBox<>();
		columnModel.getColumn(ECASH_RECEIVER_COLUMN_INDEX).setCellEditor(new DefaultCellEditor(ecashReceiverComboBox));
		// combobox column always updated in setPayment
		
		MagicTextField referenceNumberField = new MagicTextField();
		referenceNumberField.setMaximumLength(10);
		
		MagicTextField receivedDateField = new MagicTextField();
		receivedDateField.setMaximumLength(10);
		columnModel.getColumn(RECEIVED_DATE_COLUMN_INDEX).setCellEditor(
				new DateCellEditor(receivedDateField, "Received Date"));
		
		receivedByComboBox = new MagicComboBox<>();
		columnModel.getColumn(RECEIVED_BY_COLUMN_INDEX).setCellEditor(new DefaultCellEditor(receivedByComboBox));
		// combobox column always updated in setPayment
		
	}
	
	public void addNewRow() {
		if (getRowCount() > 0 && !tableModel.getRowItem(getRowCount() - 1).isUpdating()) {
			return;
		}
		
		PaymentEcashPayment ecashPayment = new PaymentEcashPayment();
		ecashPayment.setParent(payment);
		tableModel.addItem(ecashPayment);
		selectAndEditCellAt(getRowCount() - 1, 0);
	}
	
	public boolean isLastRowSelected() {
		return getSelectedRow() + 1 == tableModel.getRowCount();
	}

	public void doDeleteCurrentlySelectedItem() {
		int selectedRowIndex = getSelectedRow();
		PaymentEcashPayment item = getCurrentlySelectedRowItem().getEcashPayment();
		clearSelection(); // clear row selection so model listeners will not cause exceptions while model items are being updated
		payment.getEcashPayments().remove(item);
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
		
		List<User> users = userService.getAllUsers();
		receivedByComboBox.setModel(new DefaultComboBoxModel<>(users.toArray(new User[users.size()])));
		
		List<EcashReceiver> ecashReceivers = ecashReceiverService.getAllEcashReceivers();
		ecashReceiverComboBox.setModel(new DefaultComboBoxModel<>(ecashReceivers.toArray(new EcashReceiver[users.size()])));
	}
	
	protected void registerKeyBindings() {
		InputMap inputMap = getInputMap(JTable.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
		inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), CANCEL_ACTION_NAME);
		inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_F10, 0), F10_ACTION_NAME);
		inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_F5, 0), F5_ACTION_NAME);
		inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0), DELETE_ITEM_ACTION_NAME);
		
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
				if (isReceivedDateColumnSelected() && payment.isNew()) {
					openSelectDateDialog();
				}
			}
		});
		actionMap.put(DELETE_ITEM_ACTION_NAME, new AbstractAction() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				removeCurrentlySelectedItem();
			}
		});
	}
	
	private void openSelectDateDialog() {
		if (!isEditing()) {
			editCellAt(getSelectedRow(), RECEIVED_DATE_COLUMN_INDEX);
		}
		
		selectDateDialog.setVisible(true);
		Date selectedDate = selectDateDialog.getSelectedDate();
		if (selectedDate != null) {
			((JTextField)getEditorComponent()).setText(FormatterUtil.formatDate(selectedDate));
			getCellEditor().stopCellEditing();
		}
	}

	private boolean isReceivedDateColumnSelected() {
		return getSelectedColumn() == RECEIVED_DATE_COLUMN_INDEX;
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

	public PaymentEcashPaymentRowItem getCurrentlySelectedRowItem() {
		return tableModel.getRowItem(getSelectedRow());
	}
	
	public void removeCurrentlySelectedItem() {
		if (!payment.isNew()) {
			return;
		}
		
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
				final int row = e.getFirstRow();
				final int column = e.getColumn();
				
				SwingUtilities.invokeLater(new Runnable() {
					
					@Override
					public void run() {
						switch (column) {
						case AMOUNT_COLUMN_INDEX:
							selectAndEditCellAt(row, ECASH_RECEIVER_COLUMN_INDEX);
							break;
						case ECASH_RECEIVER_COLUMN_INDEX:
							selectAndEditCellAt(row, REFERENCE_NUMBER_COLUMN_INDEX);
							break;
						case REFERENCE_NUMBER_COLUMN_INDEX:
							selectAndEditCellAt(row, RECEIVED_DATE_COLUMN_INDEX);
							break;
						case RECEIVED_DATE_COLUMN_INDEX:
							selectAndEditCellAt(row, RECEIVED_BY_COLUMN_INDEX);
							break;
						case RECEIVED_BY_COLUMN_INDEX:
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
	
}