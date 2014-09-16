package com.pj.magic.gui.tables;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumnModel;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.pj.magic.gui.tables.models.ReceivingReceiptItemsTableModel;
import com.pj.magic.gui.tables.rowitems.ReceivingReceiptItemRowItem;
import com.pj.magic.model.ReceivingReceipt;
import com.pj.magic.model.ReceivingReceiptItem;
import com.pj.magic.service.ProductService;
import com.pj.magic.util.NumberUtil;

@Component
public class ReceivingReceiptItemsTable extends ItemsTable {
	
	public static final int PRODUCT_CODE_COLUMN_INDEX = 0;
	public static final int PRODUCT_DESCRIPTION_COLUMN_INDEX = 1;
	public static final int UNIT_COLUMN_INDEX = 2;
	public static final int QUANTITY_COLUMN_INDEX = 3;
	public static final int COST_COLUMN_INDEX = 4;
	public static final int AMOUNT_COLUMN_INDEX = 5;
	public static final int DISCOUNT_1_COLUMN_INDEX = 6;
	public static final int DISCOUNT_2_COLUMN_INDEX = 7;
	public static final int DISCOUNT_3_COLUMN_INDEX = 8;
	public static final int FLAT_RATE_COLUMN_INDEX = 9;
	public static final int DISCOUNTED_AMOUNT_COLUMN_INDEX = 10;
	public static final int NET_AMOUNT_COLUMN_INDEX = 11;
	
	private static final String TAB_ACTION_NAME = "tab";
	private static final String DOWN_ACTION_NAME = "down";

	@Autowired private ProductService productService;
	@Autowired private ReceivingReceiptItemsTableModel tableModel;
	
	private ReceivingReceipt receivingReceipt;
	private Action originalDownAction;
	private Action originalEscapeAction;
	
	@Autowired
	public ReceivingReceiptItemsTable(ReceivingReceiptItemsTableModel tableModel) {
		super(tableModel);
		setSurrendersFocusOnKeystroke(true);
		initializeColumns();
		initializeRowItemValidationBehavior();
		registerKeyBindings();
	}
	
	// TODO: replace tab key simulation with table model listener
	private void initializeColumns() {
		TableColumnModel columnModel = getColumnModel();
		columnModel.getColumn(PRODUCT_CODE_COLUMN_INDEX).setPreferredWidth(80);
		columnModel.getColumn(PRODUCT_DESCRIPTION_COLUMN_INDEX).setPreferredWidth(300);
		columnModel.getColumn(UNIT_COLUMN_INDEX).setPreferredWidth(60);
		columnModel.getColumn(QUANTITY_COLUMN_INDEX).setPreferredWidth(60);
		
		DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
		rightRenderer.setHorizontalAlignment(JLabel.RIGHT);
		getColumnModel().getColumn(COST_COLUMN_INDEX).setCellRenderer(rightRenderer);
		getColumnModel().getColumn(AMOUNT_COLUMN_INDEX).setCellRenderer(rightRenderer);
		getColumnModel().getColumn(DISCOUNT_1_COLUMN_INDEX).setCellRenderer(rightRenderer);
		getColumnModel().getColumn(DISCOUNT_2_COLUMN_INDEX).setCellRenderer(rightRenderer);
		getColumnModel().getColumn(DISCOUNT_3_COLUMN_INDEX).setCellRenderer(rightRenderer);
		getColumnModel().getColumn(FLAT_RATE_COLUMN_INDEX).setCellRenderer(rightRenderer);
		getColumnModel().getColumn(DISCOUNTED_AMOUNT_COLUMN_INDEX).setCellRenderer(rightRenderer);
		getColumnModel().getColumn(NET_AMOUNT_COLUMN_INDEX).setCellRenderer(rightRenderer);
	}
	
	private Action getAction(int keyEvent) {
		String actionName = (String)getInputMap(JTable.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).get(KeyStroke.getKeyStroke(keyEvent, 0));
		return getActionMap().get(actionName);
	}

	public boolean isQuantityFieldSelected() {
		return getSelectedColumn() == QUANTITY_COLUMN_INDEX;
	}
	
	public boolean isProductCodeFieldSelected() {
		return getSelectedColumn() == PRODUCT_CODE_COLUMN_INDEX;
	}

	public boolean isUnitFieldSelected() {
		return getSelectedColumn() == UNIT_COLUMN_INDEX;
	}
	
	public boolean isLastRowSelected() {
		return getSelectedRow() + 1 == tableModel.getRowCount();
	}

	public ReceivingReceiptItemRowItem getCurrentlySelectedRowItem() {
		return tableModel.getRowItem(getSelectedRow());
	}
	
	public void setReceivingReceipt(ReceivingReceipt receivingReceipt) {
		clearSelection();
		this.receivingReceipt = receivingReceipt;
		tableModel.setReceivingReceipt(receivingReceipt);
	}
	
	protected void registerKeyBindings() {
		// TODO: shift + tab
		// TODO: Modify on other columns dont work
		
		originalDownAction = getAction(KeyEvent.VK_DOWN);
		originalEscapeAction = getAction(KeyEvent.VK_ESCAPE);
		
		InputMap inputMap = getInputMap(JTable.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
		inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_TAB, 0), TAB_ACTION_NAME);
		inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0), DOWN_ACTION_NAME);
		
		ActionMap actionMap = getActionMap();
		actionMap.put(TAB_ACTION_NAME, new AbstractAction() {

			@Override
			public void actionPerformed(ActionEvent e) {
				tab();
			}
		});
		actionMap.put(DOWN_ACTION_NAME, new AbstractAction() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				if (isEditing()) {
					getCellEditor().stopCellEditing();
				} else {
					originalDownAction.actionPerformed(e);
				}
			}
		});
	}
	
	protected void tab() {
		if (getSelectedRow() == -1) {
			return;
		}
		
		if (isEditing()) {
			getCellEditor().stopCellEditing();
		}
		
		int selectedColumn = getSelectedColumn();
		int selectedRow = getSelectedRow();
		ReceivingReceiptItemRowItem rowItem = getCurrentlySelectedRowItem();
		
		switch (selectedColumn) {
		
		}
	}

	public int getTotalNumberOfItems() {
		return receivingReceipt.getTotalNumberOfItems();
	}

	public void highlightQuantityColumn(ReceivingReceiptItem item) {
		int row = receivingReceipt.getItems().indexOf(item);
		changeSelection(row, QUANTITY_COLUMN_INDEX, false, false);
		editCellAt(row, QUANTITY_COLUMN_INDEX);
		getEditorComponent().requestFocusInWindow();
	}
	
	public void highlight() {
		changeSelection(0, DISCOUNT_1_COLUMN_INDEX, false, false);
		editCellAt(0, DISCOUNT_1_COLUMN_INDEX);
		getEditorComponent().requestFocusInWindow();
	}
	
	private void initializeRowItemValidationBehavior() {
		getModel().addTableModelListener(new TableModelListener() {
			
			@Override
			public void tableChanged(TableModelEvent e) {
				final int row = getSelectedRow();
				final int column = e.getColumn();
				
				if (column == DISCOUNT_1_COLUMN_INDEX) {
					SwingUtilities.invokeLater(new Runnable() {

						@Override
						public void run() {
							if (validateDiscount1(getCurrentlySelectedRowItem())) {
								tableModel.setValueAt("", row, DISCOUNTED_AMOUNT_COLUMN_INDEX);
								tableModel.setValueAt("", row, NET_AMOUNT_COLUMN_INDEX);
								changeSelection(row, DISCOUNT_2_COLUMN_INDEX, false, false);
								editCellAt(row, DISCOUNT_2_COLUMN_INDEX);
								getEditorComponent().requestFocusInWindow();
							}
						}
					});
				} else if (column == DISCOUNT_2_COLUMN_INDEX) {
					SwingUtilities.invokeLater(new Runnable() {

						@Override
						public void run() {
							if (validateDiscount2(getCurrentlySelectedRowItem())) {
								tableModel.setValueAt("", row, DISCOUNTED_AMOUNT_COLUMN_INDEX);
								tableModel.setValueAt("", row, NET_AMOUNT_COLUMN_INDEX);
								changeSelection(row, DISCOUNT_3_COLUMN_INDEX, false, false);
								editCellAt(row, DISCOUNT_3_COLUMN_INDEX);
								getEditorComponent().requestFocusInWindow();
							}
						}
					});
				} else if (column == DISCOUNT_3_COLUMN_INDEX) {
					SwingUtilities.invokeLater(new Runnable() {

						@Override
						public void run() {
							if (validateDiscount3(getCurrentlySelectedRowItem())) {
								tableModel.setValueAt("", row, DISCOUNTED_AMOUNT_COLUMN_INDEX);
								tableModel.setValueAt("", row, NET_AMOUNT_COLUMN_INDEX);
								changeSelection(row, FLAT_RATE_COLUMN_INDEX, false, false);
								editCellAt(row, FLAT_RATE_COLUMN_INDEX);
								getEditorComponent().requestFocusInWindow();
							}
						}
					});
				} else if (column == FLAT_RATE_COLUMN_INDEX) {
					SwingUtilities.invokeLater(new Runnable() {

						@Override
						public void run() {
							if (validateFlatRate(getCurrentlySelectedRowItem())) {
								tableModel.setValueAt("", row, DISCOUNTED_AMOUNT_COLUMN_INDEX);
								tableModel.setValueAt("", row, NET_AMOUNT_COLUMN_INDEX);
								if (!isLastRowSelected()) {
									changeSelection(row + 1, DISCOUNT_1_COLUMN_INDEX, false, false);
									editCellAtCurrentRow(DISCOUNT_1_COLUMN_INDEX);
								}
							}
						}
					});
				}
			}
		});
	}
	
	private boolean validateDiscount1(ReceivingReceiptItemRowItem rowItem) {
		boolean valid = false;
		if (!StringUtils.isEmpty(rowItem.getDiscount1()) && !NumberUtil.isAmount(rowItem.getDiscount1())) {
			showErrorMessage("Discount 1 must be a valid amount");
		} else {
			valid = true;
		}
		
		if (!valid) {
			editCellAtCurrentRow(DISCOUNT_1_COLUMN_INDEX);
		}
		return valid;
	}
	
	private boolean validateDiscount2(ReceivingReceiptItemRowItem rowItem) {
		boolean valid = false;
		if (!StringUtils.isEmpty(rowItem.getDiscount2()) && !NumberUtil.isAmount(rowItem.getDiscount2())) {
			showErrorMessage("Discount 2 must be a valid amount");
		} else {
			valid = true;
		}
		
		if (!valid) {
			editCellAtCurrentRow(DISCOUNT_2_COLUMN_INDEX);
		}
		return valid;
	}
	
	private boolean validateDiscount3(ReceivingReceiptItemRowItem rowItem) {
		boolean valid = false;
		if (!StringUtils.isEmpty(rowItem.getDiscount3()) && !NumberUtil.isAmount(rowItem.getDiscount3())) {
			showErrorMessage("Discount 3 must be a valid amount");
		} else {
			valid = true;
		}
		
		if (!valid) {
			editCellAtCurrentRow(DISCOUNT_3_COLUMN_INDEX);
		}
		return valid;
	}
	
	private boolean validateFlatRate(ReceivingReceiptItemRowItem rowItem) {
		boolean valid = false;
		if (!StringUtils.isEmpty(rowItem.getFlatRateDiscount()) 
				&& !NumberUtil.isAmount(rowItem.getFlatRateDiscount())) {
			showErrorMessage("Flat Rate Discount must be a valid amount");
		} else {
			valid = true;
		}
		
		if (!valid) {
			editCellAtCurrentRow(FLAT_RATE_COLUMN_INDEX);
		}
		return valid;
	}
	
}