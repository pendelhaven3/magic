package com.pj.magic.gui.tables;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.math.BigDecimal;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.DefaultCellEditor;
import javax.swing.InputMap;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.pj.magic.Constants;
import com.pj.magic.gui.component.AbstractKeyListener;
import com.pj.magic.gui.component.MagicTextField;
import com.pj.magic.gui.dialog.SelectProductDialog;
import com.pj.magic.gui.dialog.SelectUnitDialog;
import com.pj.magic.gui.tables.models.AdjustmentOutItemsTableModel;
import com.pj.magic.gui.tables.rowitems.AdjustmentOutItemRowItem;
import com.pj.magic.model.AdjustmentOut;
import com.pj.magic.model.AdjustmentOutItem;
import com.pj.magic.model.Product;
import com.pj.magic.service.ProductService;
import com.pj.magic.util.KeyUtil;

@Component
public class AdjustmentOutItemsTable extends ItemsTable {
	
	public static final int PRODUCT_CODE_COLUMN_INDEX = 0;
	public static final int PRODUCT_DESCRIPTION_COLUMN_INDEX = 1;
	public static final int UNIT_COLUMN_INDEX = 2;
	public static final int QUANTITY_COLUMN_INDEX = 3;
	public static final int UNIT_PRICE_COLUMN_INDEX = 4;
	public static final int AMOUNT_COLUMN_INDEX = 5;
	private static final int QUANTITY_MAXIMUM_LENGTH = 3;
	private static final String TAB_ACTION_NAME = "tab";
	private static final String DOWN_ACTION_NAME = "down";
	private static final String SHOW_SELECTION_DIALOG_ACTION_NAME = "showSelectionDialog";
	private static final String CANCEL_ACTION_NAME = "cancelAddMode";
	private static final String DELETE_ITEM_ACTION_NAME = "deleteItem";

	@Autowired private SelectProductDialog selectProductDialog;
	@Autowired private SelectUnitDialog selectUnitDialog;
	@Autowired private ProductService productService;
	@Autowired private AdjustmentOutItemsTableModel tableModel;
	
	private boolean addMode;
	private AdjustmentOut adjustmentOut;
	private Action originalDownAction;
	private Action originalEscapeAction;
	
	@Autowired
	public AdjustmentOutItemsTable(AdjustmentOutItemsTableModel tableModel) {
		super(tableModel);
		setSurrendersFocusOnKeystroke(true);
		initializeColumns();
		initializeModelListener();
		registerKeyBindings();
	}
	
	// TODO: replace tab key simulation with table model listener
	private void initializeColumns() {
		TableColumnModel columnModel = getColumnModel();
		columnModel.getColumn(PRODUCT_CODE_COLUMN_INDEX).setPreferredWidth(120);
		columnModel.getColumn(PRODUCT_DESCRIPTION_COLUMN_INDEX).setPreferredWidth(300);
		columnModel.getColumn(UNIT_COLUMN_INDEX).setPreferredWidth(70);
		columnModel.getColumn(QUANTITY_COLUMN_INDEX).setPreferredWidth(70);
		columnModel.getColumn(UNIT_PRICE_COLUMN_INDEX).setPreferredWidth(100);
		columnModel.getColumn(AMOUNT_COLUMN_INDEX).setPreferredWidth(100);
		
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
		getColumnModel().getColumn(UNIT_COLUMN_INDEX).setCellEditor(new DefaultCellEditor(unitTextField));
		
		MagicTextField quantityTextField = new MagicTextField();
		quantityTextField.setMaximumLength(QUANTITY_MAXIMUM_LENGTH);
		quantityTextField.setNumbersOnly(true);
		getColumnModel().getColumn(QUANTITY_COLUMN_INDEX).setCellEditor(new DefaultCellEditor(quantityTextField));
	}
	
	public void switchToAddMode() {
		clearSelection();
		if (isEditing()) {
			getCellEditor().cancelCellEditing();
		}
		
		if (addMode) {
			adjustmentOut.getItems().addAll(tableModel.getItems());
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

	public boolean isAdding() {
		return addMode;
	}
	
	public void switchToEditMode() {
		clearSelection();
		if (isEditing()) {
			getCellEditor().cancelCellEditing();
		}
		
		addMode = false;
		List<AdjustmentOutItem> items = adjustmentOut.getItems();
		items.addAll(tableModel.getItems());
		tableModel.setItems(items);
		
		if (items.size() > 0) {
			changeSelection(0, 0, false, false);
		}
	}
	
	public void removeCurrentlySelectedRow() {
		
		int selectedRowIndex = getSelectedRow();
		AdjustmentOutItem item = getCurrentlySelectedRowItem().getItem();
		clearSelection(); // clear row selection so model listeners will not cause exceptions while model items are being updated
		adjustmentOut.getItems().remove(item);
		tableModel.removeItem(selectedRowIndex);
		
		if (tableModel.hasItems()) {
			if (selectedRowIndex == getModel().getRowCount()) {
				changeSelection(selectedRowIndex - 1, 0, false, false);
			} else {
				changeSelection(selectedRowIndex, 0, false, false);
			}
		}
	}
	
	public AdjustmentOutItemRowItem getCurrentlySelectedRowItem() {
		return tableModel.getRowItem(getSelectedRow());
	}
	
	public void editCellAtCurrentRow(int columnIndex) {
		editCellAt(getSelectedRow(), columnIndex);
		getEditorComponent().requestFocusInWindow();
	}
	
	private boolean hasDuplicate(AdjustmentOutItemRowItem rowItem) {
		AdjustmentOutItem checkItem = new AdjustmentOutItem();
		checkItem.setProduct(rowItem.getProduct());
		checkItem.setUnit(rowItem.getUnit());
		
		for (AdjustmentOutItem item : adjustmentOut.getItems()) {
			if (item.equals(checkItem) && item != rowItem.getItem()) {
				return true;
			}
		}
		return false;
	}
	
	public void setAdjustmentOut(AdjustmentOut adjustmentOut) {
		clearSelection();
		addMode = false;
		this.adjustmentOut = adjustmentOut;
		tableModel.setItems(adjustmentOut.getItems());
		tableModel.setEditable(!adjustmentOut.isPosted());
	}
	
	private AdjustmentOutItem createBlankItem() {
		AdjustmentOutItem item = new AdjustmentOutItem();
		item.setParent(adjustmentOut);
		return item;
	}
	
	protected void registerKeyBindings() {
		// TODO: shift + tab
		// TODO: Remove table references inside anonymous classes
		// TODO: Modify on other columns dont work
		
		final AdjustmentOutItemsTable table = this;
		originalDownAction = getAction(KeyEvent.VK_DOWN);
		originalEscapeAction = getAction(KeyEvent.VK_ESCAPE);
		
		InputMap inputMap = getInputMap(JTable.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
		inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_TAB, 0), TAB_ACTION_NAME);
		inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0), DOWN_ACTION_NAME);
		inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_F3, 0), DELETE_ITEM_ACTION_NAME);
		inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_F5, 0), SHOW_SELECTION_DIALOG_ACTION_NAME);
		inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), CANCEL_ACTION_NAME);
		
		ActionMap actionMap = getActionMap();
		actionMap.put(TAB_ACTION_NAME, new AbstractAction() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (getSelectedRow() == -1) {
					return;
				}
				
				if (table.isEditing()) {
					table.getCellEditor().stopCellEditing();
				}
				
				int selectedColumn = table.getSelectedColumn();
				int selectedRow = table.getSelectedRow();
				AdjustmentOutItemRowItem rowItem = table.getCurrentlySelectedRowItem();
				
				switch (selectedColumn) {
				case PRODUCT_CODE_COLUMN_INDEX:
					String code = (String)table.getValueAt(selectedRow, PRODUCT_CODE_COLUMN_INDEX);
					if (StringUtils.isEmpty(code)) {
						JOptionPane.showMessageDialog(table,
								"Product code must be specified", "Error Message", JOptionPane.ERROR_MESSAGE);
						table.editCellAt(selectedRow, PRODUCT_CODE_COLUMN_INDEX);
						getEditorComponent().requestFocusInWindow();
					} else if (productService.findProductByCode(code) == null) {
						JOptionPane.showMessageDialog(table,
								"No product matching code specified", "Error Message", JOptionPane.ERROR_MESSAGE);
						table.editCellAt(selectedRow, PRODUCT_CODE_COLUMN_INDEX);
						getEditorComponent().requestFocusInWindow();
					} else {
						table.changeSelection(selectedRow, UNIT_COLUMN_INDEX, false, false);
						table.editCellAt(selectedRow, UNIT_COLUMN_INDEX);
						getEditorComponent().requestFocusInWindow();
					}
					break;
				case AdjustmentOutItemsTable.UNIT_COLUMN_INDEX:
					String unit = (String)table.getValueAt(selectedRow, UNIT_COLUMN_INDEX);
					
					if (StringUtils.isEmpty(unit)) {
						JOptionPane.showMessageDialog(table,
								"Unit must be specified", "Error Message", JOptionPane.ERROR_MESSAGE);
						editCellAtCurrentRow(UNIT_COLUMN_INDEX);
					} else if (!rowItem.getProduct().getUnits().contains(unit)) {
						JOptionPane.showMessageDialog(table,
								"Product does not have unit specified", "Error Message", JOptionPane.ERROR_MESSAGE);
						editCellAtCurrentRow(UNIT_COLUMN_INDEX);
					} else if (tableModel.hasDuplicate(rowItem) || hasDuplicate(rowItem)) {
						JOptionPane.showMessageDialog(table,
								"Duplicate item", "Error Message", JOptionPane.ERROR_MESSAGE);
						editCellAtCurrentRow(UNIT_COLUMN_INDEX);
					} else {
						table.changeSelection(selectedRow, QUANTITY_COLUMN_INDEX, false, false);
						editCellAtCurrentRow(QUANTITY_COLUMN_INDEX);
					}
					break;
				case QUANTITY_COLUMN_INDEX:
					if (selectedRow + 1 < table.getRowCount()) {
						table.changeSelection(selectedRow + 1, 0, false, false);
						table.editCellAt(selectedRow + 1, 0);
						table.getEditorComponent().requestFocusInWindow();
					}
					break;
				}
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
		});
		actionMap.put(CANCEL_ACTION_NAME, new AbstractAction() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				if (table.isAdding()) {
					table.switchToEditMode();
				} else {
					originalEscapeAction.actionPerformed(e);
				}
			}
		});
		actionMap.put(DELETE_ITEM_ACTION_NAME, new AbstractAction() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
//				if (table.tableModel.hasItems()) {
//					if (table.getCurrentlySelectedRowItem().isFilledUp()) { // check valid row to prevent deleting the blank row
//						int confirm = JOptionPane.showConfirmDialog(table, "Do you wish to delete the selected item?", "Select An Option", JOptionPane.YES_NO_OPTION);
//						if (confirm == JOptionPane.OK_OPTION) {
//							removeCurrentlySelectedRow();
//						}
//					}
//				}
			}
		});
	}
	
	public boolean validateQuantity(AdjustmentOutItem item) {
		if (item.getQuantity() == null) {
			JOptionPane.showMessageDialog(this,
					"Quantity must be specified", "Error Message", JOptionPane.ERROR_MESSAGE);
			editCellAtCurrentRow(QUANTITY_COLUMN_INDEX);
			return false;
		} else {
			Product product = productService.getProduct(item.getProduct().getId());
			if (!product.hasAvailableUnitQuantity(item.getUnit(), item.getQuantity().intValue())) {
				JOptionPane.showMessageDialog(this,
						"Not enough stocks", "Error Message", JOptionPane.ERROR_MESSAGE);
				editCellAtCurrentRow(QUANTITY_COLUMN_INDEX);
				return false;
			} else {
				return true;
			}
		}
	}
	
	public BigDecimal getTotalAmount() {
		BigDecimal totalAmount = adjustmentOut.getTotalAmount();
		if (isAdding()) {
			for (AdjustmentOutItem item : tableModel.getItems()) {
				totalAmount = totalAmount.add(item.getAmount());
			}
		}
		return totalAmount;
	}
	
	public int getTotalNumberOfItems() {
		int totalNumberOfItems = adjustmentOut.getTotalNumberOfItems();
		if (isAdding()) {
			totalNumberOfItems += tableModel.getItems().size();
		}
		return totalNumberOfItems;
	}

	public void highlightColumn(AdjustmentOutItem item, int column) {
		int row = adjustmentOut.getItems().indexOf(item);
		changeSelection(row, column, false, false);
		editCellAt(row, column);
		getEditorComponent().requestFocusInWindow();
	}
	
	public void highlight() {
		if (!adjustmentOut.hasItems()) {
			switchToAddMode();
		} else {
			changeSelection(0, 0, false, false);
			requestFocusInWindow();
		}
	}
	
	private void initializeModelListener() {
		getModel().addTableModelListener(new TableModelListener() {
			
			@Override
			public void tableChanged(TableModelEvent e) {
				final TableModel model = (TableModel)e.getSource();
				final int row = e.getFirstRow();
				
				switch (e.getColumn()) {
				case UNIT_COLUMN_INDEX:
					model.setValueAt("", row, UNIT_PRICE_COLUMN_INDEX);
					break;
				case QUANTITY_COLUMN_INDEX:
					SwingUtilities.invokeLater(new Runnable() {

						@Override
						public void run() {
							model.setValueAt("", row, AMOUNT_COLUMN_INDEX);
							if (validateQuantity(getCurrentlySelectedRowItem())) {
								if (isAdding() && isLastRowSelected()) {
									addNewRow();
								}
							}
						}
					});
					break;
				}
			}
		});
	}
	
	public boolean validateQuantity(AdjustmentOutItemRowItem rowItem) {
		if (StringUtils.isEmpty(rowItem.getQuantity())) {
			showErrorMessage("Quantity must be specified");
			editCellAtCurrentRow(QUANTITY_COLUMN_INDEX);
			return false;
		} else if (rowItem.getQuantityAsInt() == 0) {
			showErrorMessage("Quantity must be greater than 0");
			editCellAtCurrentRow(QUANTITY_COLUMN_INDEX);
			return false;
		} else {
			Product product = productService.getProduct(rowItem.getProduct().getId());
			if (!product.hasAvailableUnitQuantity(rowItem.getUnit(), rowItem.getQuantityAsInt())) {
				showErrorMessage("Not enough stocks");
				editCellAtCurrentRow(QUANTITY_COLUMN_INDEX);
				return false;
			} else {
				return true;
			}
		}
	}

	// TODO: Rename method
	public void delete() {
		if (tableModel.hasItems()) {
			if (tableModel.isValid(getSelectedRow())) { // check valid row to prevent deleting the blank row
				if (confirm("Do you wish to delete the selected item?")) {
					removeCurrentlySelectedRow();
				}
			}
		}
	}
	
}