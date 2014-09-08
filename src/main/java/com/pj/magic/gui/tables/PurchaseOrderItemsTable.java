package com.pj.magic.gui.tables;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.DefaultCellEditor;
import javax.swing.InputMap;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableColumnModel;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.pj.magic.Constants;
import com.pj.magic.gui.component.AbstractKeyListener;
import com.pj.magic.gui.component.MagicTextField;
import com.pj.magic.gui.dialog.SelectActionDialog;
import com.pj.magic.gui.dialog.SelectProductDialog;
import com.pj.magic.gui.dialog.SelectUnitDialog;
import com.pj.magic.gui.tables.models.ActionsTableModel;
import com.pj.magic.gui.tables.models.PurchaseOrderItemsTableModel;
import com.pj.magic.gui.tables.rowitems.PurchaseOrderItemRowItem;
import com.pj.magic.model.PurchaseOrder;
import com.pj.magic.model.PurchaseOrderItem;
import com.pj.magic.service.ProductService;
import com.pj.magic.util.KeyUtil;
import com.pj.magic.util.NumberUtil;

/*
 * PurchaseOrderItemsTable has 3 modes: New, Ordered, and Posted
 */

@Component
public class PurchaseOrderItemsTable extends ItemsTable {
	
	public static final int PRODUCT_CODE_COLUMN_INDEX = 0;
	public static final int PRODUCT_DESCRIPTION_COLUMN_INDEX = 1;
	public static final int UNIT_COLUMN_INDEX = 2;
	public static final int QUANTITY_COLUMN_INDEX = 3;
	
	private static final int QUANTITY_MAXIMUM_LENGTH = 3;
	private static final String TAB_ACTION_NAME = "tab";
	private static final String DOWN_ACTION_NAME = "down";
	private static final String SHOW_SELECTION_DIALOG_ACTION_NAME = "showSelectionDialog";
	private static final String SHOW_SELECT_ACTION_DIALOG_ACTION_NAME = "showSelectActionDialog";
	private static final String CANCEL_ACTION_NAME = "cancelAddMode";
	private static final String DELETE_ITEM_ACTION_NAME = "deleteItem";

	@Autowired private SelectActionDialog selectActionDialog;
	@Autowired private SelectProductDialog selectProductDialog;
	@Autowired private SelectUnitDialog selectUnitDialog;
	@Autowired private ProductService productService;
	@Autowired private PurchaseOrderItemsTableModel tableModel;
	
	// dynamic columns
	private int orderedColumnIndex = 4;
	private int actualQuantityColumnIndex = 5;
	private int costColumnIndex;
	private int amountColumnIndex;
	
	private PurchaseOrder purchaseOrder;
	private Action originalDownAction;
	private Action originalEscapeAction;
	
	@Autowired
	public PurchaseOrderItemsTable(PurchaseOrderItemsTableModel tableModel) {
		super(tableModel);
		tableModel.setTable(this);
		setSurrendersFocusOnKeystroke(true);
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
		columnModel.getColumn(costColumnIndex).setPreferredWidth(60);
		columnModel.getColumn(amountColumnIndex).setPreferredWidth(60);
		if (purchaseOrder.isOrdered()) {
			columnModel.getColumn(orderedColumnIndex).setPreferredWidth(60);
			columnModel.getColumn(actualQuantityColumnIndex).setPreferredWidth(60);
		}
		
		MagicTextField productCodeTextField = new MagicTextField();
		productCodeTextField.setMaximumLength(Constants.PRODUCT_CODE_MAXIMUM_LENGTH);
		productCodeTextField.addKeyListener(new AbstractKeyListener() {
			
			@Override
			public void keyReleased(KeyEvent event) {
				if (KeyUtil.isAlphaNumericKeyCode(event.getKeyCode())) {
					JTextField textField = (JTextField)event.getComponent();
					if (textField.getText().length() == Constants.PRODUCT_CODE_MAXIMUM_LENGTH) {
						KeyUtil.simulateTabKey();
					};
				}
			}
			
			@Override
			public void keyPressed(KeyEvent event) {
				if (event.getKeyCode() == KeyEvent.VK_ENTER) {
					getCellEditor().stopCellEditing();
					KeyUtil.simulateTabKey();
				}
			}
		});
		getColumnModel().getColumn(PRODUCT_CODE_COLUMN_INDEX).setCellEditor(new DefaultCellEditor(productCodeTextField));
		
		MagicTextField unitTextField = new MagicTextField();
		unitTextField.setMaximumLength(Constants.UNIT_MAXIMUM_LENGTH);
		unitTextField.addKeyListener(new AbstractKeyListener() {
			
			@Override
			public void keyReleased(KeyEvent event) {
				if (!KeyUtil.isAlphaNumericKeyCode(event.getKeyCode())) {
					return;
				}
				JTextField textField = (JTextField)event.getComponent();
				if (textField.getText().length() == Constants.UNIT_MAXIMUM_LENGTH) {
					KeyUtil.simulateTabKey();
				}
			}
			
			@Override
			public void keyPressed(KeyEvent event) {
				if (event.getKeyCode() == KeyEvent.VK_ENTER) {
					getCellEditor().stopCellEditing();
					KeyUtil.simulateTabKey();
				}
			}
		});
		TableCellEditor unitCellEditor = new DefaultCellEditor(unitTextField);
		getColumnModel().getColumn(UNIT_COLUMN_INDEX).setCellEditor(unitCellEditor);
		
		MagicTextField quantityTextField = new MagicTextField();
		quantityTextField.setMaximumLength(QUANTITY_MAXIMUM_LENGTH);
		quantityTextField.setNumbersOnly(true);
		
		DefaultCellEditor quantityCellEditor = new DefaultCellEditor(quantityTextField);
		getColumnModel().getColumn(QUANTITY_COLUMN_INDEX).setCellEditor(quantityCellEditor);
		getColumnModel().getColumn(actualQuantityColumnIndex).setCellEditor(quantityCellEditor);
		
		DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
		rightRenderer.setHorizontalAlignment(JLabel.RIGHT);
		getColumnModel().getColumn(costColumnIndex).setCellRenderer(rightRenderer);
		getColumnModel().getColumn(amountColumnIndex).setCellRenderer(rightRenderer);
	}
	
	public void switchToAddMode() {
		clearSelection();
		if (isEditing()) {
			getCellEditor().cancelCellEditing();
		}
		
		if (addMode) {
			purchaseOrder.getItems().addAll(tableModel.getItems());
		}
		
		addMode = true;
		tableModel.clearAndAddItem(createBlankItem());
		changeSelection(0, 0, false, false);
		editCellAt(0, 0);
		getEditorComponent().requestFocusInWindow();
	}
	
	private Action getAction(int keyEvent) {
		String actionName = (String)getInputMap(JTable.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).get(KeyStroke.getKeyStroke(keyEvent, 0));
		return getActionMap().get(actionName);
	}

	public void addNewRow() {
		int newRowIndex = getSelectedRow() + 1;
		tableModel.addItem(createBlankItem());
		changeSelection(newRowIndex, 0, false, false);
		editCellAt(newRowIndex, 0);
		getEditorComponent().requestFocusInWindow();
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

	public void switchToEditMode() {
		clearSelection();
		if (isEditing()) {
			getCellEditor().cancelCellEditing();
		}
		
		addMode = false;
		List<PurchaseOrderItem> items = purchaseOrder.getItems();
		items.addAll(tableModel.getItems());
		tableModel.setItems(items);
		
		if (items.size() > 0) {
			changeSelection(0, 0, false, false);
		}
	}
	
	public void removeCurrentlySelectedRow() {
		int selectedRowIndex = getSelectedRow();
		PurchaseOrderItem item = getCurrentlySelectedRowItem().getItem();
		clearSelection(); // clear row selection so model listeners will not cause exceptions while model items are being updated
		purchaseOrder.getItems().remove(item);
		tableModel.removeItem(selectedRowIndex);
		
		if (tableModel.hasItems()) {
			if (selectedRowIndex == getModel().getRowCount()) {
				changeSelection(selectedRowIndex - 1, 0, false, false);
			} else {
				changeSelection(selectedRowIndex, 0, false, false);
			}
		}
	}
	
	public PurchaseOrderItemRowItem getCurrentlySelectedRowItem() {
		return tableModel.getRowItem(getSelectedRow());
	}
	
	private boolean hasDuplicate(PurchaseOrderItemRowItem rowItem) {
		PurchaseOrderItem checkItem = new PurchaseOrderItem();
		checkItem.setProduct(rowItem.getProduct());
		checkItem.setUnit(rowItem.getUnit());
		
		for (PurchaseOrderItem item : purchaseOrder.getItems()) {
			if (item.equals(checkItem) && item != checkItem) {
				return true;
			}
		}
		return false;
	}
	
	public void setPurchaseOrder(PurchaseOrder purchaseOrder) {
		clearSelection();
		addMode = false;
		this.purchaseOrder = purchaseOrder;
		
		if (purchaseOrder.isOrdered()) {
			costColumnIndex = 6;
			amountColumnIndex = 7;
		} else {
			costColumnIndex = 4;
			amountColumnIndex = 5;
		}
		tableModel.setPurchaseOrder(purchaseOrder);
		initializeColumns();
	}
	
	private PurchaseOrderItem createBlankItem() {
		PurchaseOrderItem item = new PurchaseOrderItem();
		item.setParent(purchaseOrder);
		return item;
	}
	
	protected void registerKeyBindings() {
		// TODO: shift + tab
		// TODO: Modify on other columns dont work
		
		originalDownAction = getAction(KeyEvent.VK_DOWN);
		originalEscapeAction = getAction(KeyEvent.VK_ESCAPE);
		
		InputMap inputMap = getInputMap(JTable.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
		inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_TAB, 0), TAB_ACTION_NAME);
		inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0), DOWN_ACTION_NAME);
		inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_F3, 0), DELETE_ITEM_ACTION_NAME);
		inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_F5, 0), SHOW_SELECTION_DIALOG_ACTION_NAME);
		inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_F10, 0), SHOW_SELECT_ACTION_DIALOG_ACTION_NAME);
		inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), CANCEL_ACTION_NAME);
		
		ActionMap actionMap = getActionMap();
		actionMap.put(SHOW_SELECT_ACTION_DIALOG_ACTION_NAME, new AbstractAction() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				selectActionDialog.setVisible(true);
				
				String action = selectActionDialog.getSelectedAction();
				if (ActionsTableModel.CREATE_ACTION.equals(action)) {
					switchToAddMode();
				}
			}
		});
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
		actionMap.put(SHOW_SELECTION_DIALOG_ACTION_NAME, new AbstractAction() {

			@Override
			public void actionPerformed(ActionEvent e) {
				showSelectionDialog();
			}
		});
		actionMap.put(CANCEL_ACTION_NAME, new AbstractAction() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				if (isAdding()) {
					switchToEditMode();
				} else {
					originalEscapeAction.actionPerformed(e);
				}
			}
		});
		actionMap.put(DELETE_ITEM_ACTION_NAME, new AbstractAction() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				delete();
			}
		});
	}
	
	protected void delete() {
		if (tableModel.hasItems()) {
			if (tableModel.isValid(getSelectedRow())) { // check valid row to prevent deleting the blank row
				if (confirm("Do you wish to delete the selected item?")) {
					removeCurrentlySelectedRow();
				}
			}
		}
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
		PurchaseOrderItemRowItem rowItem = getCurrentlySelectedRowItem();
		
		switch (selectedColumn) {
		case PRODUCT_CODE_COLUMN_INDEX:
			String code = (String)getValueAt(selectedRow, PRODUCT_CODE_COLUMN_INDEX);
			if (StringUtils.isEmpty(code)) {
				showErrorMessage("Product code must be specified");
				editCellAt(selectedRow, PRODUCT_CODE_COLUMN_INDEX);
				getEditorComponent().requestFocusInWindow();
			} else if (productService.findProductByCode(code) == null) {
				showErrorMessage("No product matchng code specified");
				editCellAt(selectedRow, PRODUCT_CODE_COLUMN_INDEX);
				getEditorComponent().requestFocusInWindow();
			} else {
				changeSelection(selectedRow, UNIT_COLUMN_INDEX, false, false);
				editCellAt(selectedRow, UNIT_COLUMN_INDEX);
				getEditorComponent().requestFocusInWindow();
			}
			break;
		case PurchaseOrderItemsTable.UNIT_COLUMN_INDEX:
			String fromUnit = (String)getValueAt(selectedRow, UNIT_COLUMN_INDEX);
			if (StringUtils.isEmpty(fromUnit)) {
				showErrorMessage("Unit must be specified");
				editCellAtCurrentRow(UNIT_COLUMN_INDEX);
			} else if (!rowItem.getProduct().getUnits().contains(fromUnit)) {
				showErrorMessage("Product does not have unit specified");
				editCellAtCurrentRow(UNIT_COLUMN_INDEX);
			} else if (tableModel.hasDuplicate(rowItem) || hasDuplicate(rowItem)) {
				showErrorMessage("Duplicate item");
				editCellAtCurrentRow(UNIT_COLUMN_INDEX);
			} else {
				int nextField = QUANTITY_COLUMN_INDEX;
				if (purchaseOrder.isOrdered()) {
					nextField = actualQuantityColumnIndex;
				}
				changeSelection(selectedRow, nextField, false, false);
				editCellAtCurrentRow(nextField);
			}
			break;
		}
	}

	protected void showSelectionDialog() {
		if (isProductCodeFieldSelected()) {
			selectProductDialog.searchProducts((String)getCellEditor().getCellEditorValue());
			selectProductDialog.setVisible(true);
			
			String productCode = selectProductDialog.getSelectedProductCode();
			if (productCode != null) {
				if (isEditing()) {
					getCellEditor().cancelCellEditing();
					requestFocusInWindow(); // cancellCellEditing moves the focus to components before table
				}
				setValueAt(productCode, getSelectedRow(), getSelectedColumn());
				KeyUtil.simulateTabKey();
			}
		} else if (isUnitFieldSelected()) {
			selectUnitDialog.setUnits(getCurrentlySelectedRowItem().getProduct().getUnits());
			selectUnitDialog.searchUnits((String)getCellEditor().getCellEditorValue());
			selectUnitDialog.setVisible(true);
			
			String unit = selectUnitDialog.getSelectedUnit();
			if (unit != null) {
				if (isEditing()) {
					getCellEditor().cancelCellEditing();
					requestFocusInWindow(); // cancellCellEditing moves the focus to components before table
				}
				setValueAt(unit, getSelectedRow(), getSelectedColumn());
				KeyUtil.simulateTabKey();
			}
		}
	}

	public boolean validateQuantity(PurchaseOrderItemRowItem rowItem) {
		if (StringUtils.isEmpty(rowItem.getQuantity())) {
			showErrorMessage("Quantity must be specified");
			editCellAtCurrentRow(QUANTITY_COLUMN_INDEX);
			return false;
		} else {
			return true;
		}
	}
	
	public boolean validateActualQuantity(PurchaseOrderItemRowItem rowItem) {
		if (StringUtils.isEmpty(rowItem.getActualQuantity())) {
			showErrorMessage("Actual Quantity must be specified");
			editCellAtCurrentRow(actualQuantityColumnIndex);
			return false;
		} else {
			return true;
		}
	}
	
	public int getTotalNumberOfItems() {
//		int totalNumberOfItems = purchaseOrder.getTotalNumberOfItems();
//		if (isAdding()) {
//			totalNumberOfItems += tableModel.getItems().size();
//		}
//		return totalNumberOfItems;
		return 0;
	}

	public void highlightQuantityColumn(PurchaseOrderItem item) {
		int row = purchaseOrder.getItems().indexOf(item);
		changeSelection(row, QUANTITY_COLUMN_INDEX, false, false);
		editCellAt(row, QUANTITY_COLUMN_INDEX);
		getEditorComponent().requestFocusInWindow();
	}
	
	public void highlight() {
		if (!purchaseOrder.hasItems()) {
			switchToAddMode();
		} else {
			if (purchaseOrder.isOrdered()) {
				changeSelection(0, actualQuantityColumnIndex, false, false);
				editCellAtCurrentRow(actualQuantityColumnIndex);
			} else {
				changeSelection(0, 0, false, false);
				requestFocusInWindow();
			}
		}
	}
	
	private void initializeRowItemValidationBehavior() {
		getModel().addTableModelListener(new TableModelListener() {
			
			@Override
			public void tableChanged(TableModelEvent e) {
				final int row = getSelectedRow();
				final int column = e.getColumn();
				
				if (column == QUANTITY_COLUMN_INDEX) {
					SwingUtilities.invokeLater(new Runnable() {

						@Override
						public void run() {
							if (validateQuantity(getCurrentlySelectedRowItem())) {
								tableModel.setValueAt("", row, amountColumnIndex);
								changeSelection(row, costColumnIndex, false, false);
								editCellAt(row, costColumnIndex);
								getEditorComponent().requestFocusInWindow();
							}
						}
					});
				} else if (column == costColumnIndex) {
					SwingUtilities.invokeLater(new Runnable() {

						@Override
						public void run() {
							if (validateCost(getCurrentlySelectedRowItem())) {
								tableModel.setValueAt("", row, amountColumnIndex);
								if (isAdding() && isLastRowSelected()) {
									addNewRow();
								} else {
									if (!isLastRowSelected()) {
										int nextColumn = PRODUCT_CODE_COLUMN_INDEX;
										if (purchaseOrder.isOrdered()) {
											nextColumn = actualQuantityColumnIndex;
										}
										changeSelection(row + 1, nextColumn, false, false);
										editCellAtCurrentRow(nextColumn);
									}
								}
							}
						}
					});
				} else if (column == actualQuantityColumnIndex && purchaseOrder.isOrdered()) {
					SwingUtilities.invokeLater(new Runnable() {

						@Override
						public void run() {
							if (validateActualQuantity(getCurrentlySelectedRowItem())) {
								tableModel.setValueAt("", row, amountColumnIndex);
								changeSelection(row, costColumnIndex, false, false);
								editCellAt(row, costColumnIndex);
								getEditorComponent().requestFocusInWindow();
							}
						}
					});
				}
			}
		});
	}
	
	private boolean validateCost(PurchaseOrderItemRowItem rowItem) {
		boolean valid = false;
		if (StringUtils.isEmpty(rowItem.getCost())) {
			showErrorMessage("Cost must be specified");
		} else if (!NumberUtil.isAmount(rowItem.getCost())){
			showErrorMessage("Cost must be a valid amount");
		} else {
			valid = true;
		}
		
		if (!valid) {
			editCellAtCurrentRow(costColumnIndex);
		}
		return valid;
	}
	
	public int getCostColumnIndex() {
		return costColumnIndex;
	}
	
	public int getAmountColumnIndex() {
		return amountColumnIndex;
	}
	
	public int getOrderedColumnIndex() {
		return orderedColumnIndex;
	}
	
	public int getActualQuantityColumnIndex() {
		return actualQuantityColumnIndex;
	}
	
}