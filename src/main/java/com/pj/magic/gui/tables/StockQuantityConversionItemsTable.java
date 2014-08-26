package com.pj.magic.gui.tables;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
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
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableColumnModel;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.pj.magic.gui.component.AbstractKeyListener;
import com.pj.magic.gui.component.MagicTextField;
import com.pj.magic.gui.dialog.SelectActionDialog;
import com.pj.magic.gui.dialog.SelectProductDialog;
import com.pj.magic.gui.dialog.SelectUnitDialog;
import com.pj.magic.model.Product;
import com.pj.magic.model.StockQuantityConversion;
import com.pj.magic.model.StockQuantityConversionItem;
import com.pj.magic.service.ProductService;
import com.pj.magic.util.KeyUtil;

/*
 * [PJ 8/25/2014] 
 * ItemsTable has 2 modes: edit (default) and add (allows adding blank rows after the last row).
 * 
 */

@Component
public class StockQuantityConversionItemsTable extends ItemsTable {
	
	public static final int PRODUCT_CODE_COLUMN_INDEX = 0;
	public static final int PRODUCT_DESCRIPTION_COLUMN_INDEX = 1;
	public static final int FROM_UNIT_COLUMN_INDEX = 2;
	public static final int QUANTITY_COLUMN_INDEX = 3;
	public static final int TO_UNIT_COLUMN_INDEX = 4;
	public static final int CONVERTED_QUANTITY_COLUMN_INDEX = 5;
	private static final int PRODUCT_CODE_MAXIMUM_LENGTH = 9;
	private static final int UNIT_MAXIMUM_LENGTH = 3;
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
	@Autowired private StockQuantityConversionItemsTableModel tableModel;
	
	private boolean addMode;
	private StockQuantityConversion stockQuantityConversion;
	private Action originalDownAction;
	private Action originalEscapeAction;
	
	@Autowired
	public StockQuantityConversionItemsTable(StockQuantityConversionItemsTableModel tableModel) {
		super(tableModel);
		setSurrendersFocusOnKeystroke(true);
		initializeColumns();
		initializeModelListener();
		registerKeyBindings();
	}
	
	// TODO: replace tab key simulation with table model listener
	private void initializeColumns() {
		TableColumnModel columnModel = getColumnModel();
		columnModel.getColumn(PRODUCT_CODE_COLUMN_INDEX).setPreferredWidth(100);
		columnModel.getColumn(PRODUCT_DESCRIPTION_COLUMN_INDEX).setPreferredWidth(300);
		columnModel.getColumn(FROM_UNIT_COLUMN_INDEX).setPreferredWidth(70);
		columnModel.getColumn(QUANTITY_COLUMN_INDEX).setPreferredWidth(70);
		columnModel.getColumn(TO_UNIT_COLUMN_INDEX).setPreferredWidth(70);
		columnModel.getColumn(CONVERTED_QUANTITY_COLUMN_INDEX).setPreferredWidth(70);
		
		MagicTextField productCodeTextField = new MagicTextField();
		productCodeTextField.setMaximumLength(PRODUCT_CODE_MAXIMUM_LENGTH);
		productCodeTextField.addKeyListener(new AbstractKeyListener() {
			
			@Override
			public void keyReleased(KeyEvent event) {
				if (KeyUtil.isAlphaNumericKeyCode(event.getKeyCode())) {
					JTextField textField = (JTextField)event.getComponent();
					if (textField.getText().length() == PRODUCT_CODE_MAXIMUM_LENGTH) {
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
		unitTextField.setMaximumLength(UNIT_MAXIMUM_LENGTH);
		unitTextField.addKeyListener(new AbstractKeyListener() {
			
			@Override
			public void keyReleased(KeyEvent event) {
				if (!KeyUtil.isAlphaNumericKeyCode(event.getKeyCode())) {
					return;
				}
				JTextField textField = (JTextField)event.getComponent();
				if (textField.getText().length() == UNIT_MAXIMUM_LENGTH) {
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
		getColumnModel().getColumn(FROM_UNIT_COLUMN_INDEX).setCellEditor(unitCellEditor);
		getColumnModel().getColumn(TO_UNIT_COLUMN_INDEX).setCellEditor(unitCellEditor);
		
		MagicTextField quantityTextField = new MagicTextField();
		quantityTextField.setMaximumLength(QUANTITY_MAXIMUM_LENGTH);
		quantityTextField.setNumbersOnly(true);
		getColumnModel().getColumn(CONVERTED_QUANTITY_COLUMN_INDEX).setCellEditor(new DefaultCellEditor(quantityTextField));
	}
	
	public void switchToAddMode() {
		clearSelection();
		if (isEditing()) {
			getCellEditor().cancelCellEditing();
		}
		
		if (addMode) {
			stockQuantityConversion.getItems().addAll(tableModel.getItems());
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
		return getSelectedColumn() == CONVERTED_QUANTITY_COLUMN_INDEX;
	}
	
	public boolean isProductCodeFieldSelected() {
		return getSelectedColumn() == PRODUCT_CODE_COLUMN_INDEX;
	}

	public boolean isUnitFieldSelected() {
		int selectedColumn = getSelectedColumn();
		return selectedColumn == FROM_UNIT_COLUMN_INDEX || selectedColumn == TO_UNIT_COLUMN_INDEX;
	}
	
	public boolean isLastRowSelected() {
		return getSelectedRow() + 1 == tableModel.getRowCount();
	}

	public boolean isCurrentRowValid() {
		if (isEditing()) {
			getCellEditor().stopCellEditing();
		}
		return tableModel.getRowItem(getSelectedRow()).isValid();
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
		List<StockQuantityConversionItem> items = stockQuantityConversion.getItems();
		items.addAll(tableModel.getItems());
		tableModel.setItems(items);
		
		if (items.size() > 0) {
			changeSelection(0, 0, false, false);
		}
	}
	
	public void removeCurrentlySelectedRow() {
		
		int selectedRowIndex = getSelectedRow();
		StockQuantityConversionItem item = getCurrentlySelectedRowItem();
		clearSelection(); // clear row selection so model listeners will not cause exceptions while model items are being updated
		stockQuantityConversion.getItems().remove(item);
		tableModel.removeItem(selectedRowIndex);
		
		if (tableModel.hasItems()) {
			if (selectedRowIndex == getModel().getRowCount()) {
				changeSelection(selectedRowIndex - 1, 0, false, false);
			} else {
				changeSelection(selectedRowIndex, 0, false, false);
			}
		}
	}
	
	public StockQuantityConversionItem getCurrentlySelectedRowItem() {
		return tableModel.getRowItem(getSelectedRow());
	}
	
	public void editCellAtCurrentRow(int columnIndex) {
		editCellAt(getSelectedRow(), columnIndex);
		getEditorComponent().requestFocusInWindow();
	}
	
	private boolean hasDuplicate(StockQuantityConversionItem checkItem) {
		for (StockQuantityConversionItem item : stockQuantityConversion.getItems()) {
			if (item.equals(checkItem) && item != checkItem) {
				return true;
			}
		}
		return false;
	}
	
	public void setStockQuantityConversion(StockQuantityConversion stockQuantityConversion) {
		clearSelection();
		addMode = false;
		this.stockQuantityConversion = stockQuantityConversion;
		tableModel.setItems(stockQuantityConversion.getItems());
	}
	
	private StockQuantityConversionItem createBlankItem() {
		StockQuantityConversionItem item = new StockQuantityConversionItem();
		item.setParent(stockQuantityConversion);
		return item;
	}
	
	protected void registerKeyBindings() {
		// TODO: shift + tab
		// TODO: Remove table references inside anonymous classes
		// TODO: Modify on other columns dont work
		
		final StockQuantityConversionItemsTable table = this;
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
				doOnTab();
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
				if (table.tableModel.hasItems()) {
					if (table.getCurrentlySelectedRowItem().isFilledUp()) { // check valid row to prevent deleting the blank row
						int confirm = JOptionPane.showConfirmDialog(table, "Do you wish to delete the selected item?", "Select An Option", JOptionPane.YES_NO_OPTION);
						if (confirm == JOptionPane.OK_OPTION) {
							removeCurrentlySelectedRow();
						}
					}
				}
			}
		});
	}
	
	protected void doOnTab() {
		if (getSelectedRow() == -1) {
			return;
		}
		
		if (isEditing()) {
			getCellEditor().stopCellEditing();
		}
		
		int selectedColumn = getSelectedColumn();
		int selectedRow = getSelectedRow();
		StockQuantityConversionItem item = getCurrentlySelectedRowItem();
		
		switch (selectedColumn) {
		case PRODUCT_CODE_COLUMN_INDEX:
			String code = (String)getValueAt(selectedRow, PRODUCT_CODE_COLUMN_INDEX);
			if (StringUtils.isEmpty(code)) {
				showErrorMessage("Product code must be specified");
				editCellAt(selectedRow, PRODUCT_CODE_COLUMN_INDEX);
				getEditorComponent().requestFocusInWindow();
			} else if (productService.findProductByCode(code) == null) {
				showErrorMessage("No product matching code specified");
				editCellAt(selectedRow, PRODUCT_CODE_COLUMN_INDEX);
				getEditorComponent().requestFocusInWindow();
			} else {
				changeSelection(selectedRow, FROM_UNIT_COLUMN_INDEX, false, false);
				editCellAt(selectedRow, FROM_UNIT_COLUMN_INDEX);
				getEditorComponent().requestFocusInWindow();
			}
			break;
		case StockQuantityConversionItemsTable.FROM_UNIT_COLUMN_INDEX:
			String fromUnit = (String)getValueAt(selectedRow, FROM_UNIT_COLUMN_INDEX);
			
			if (StringUtils.isEmpty(fromUnit)) {
				showErrorMessage("From Unit must be specified");
				editCellAtCurrentRow(FROM_UNIT_COLUMN_INDEX);
			} else if (!item.getProduct().getUnits().contains(fromUnit)) {
				showErrorMessage("Product does not have unit specified");
				editCellAtCurrentRow(FROM_UNIT_COLUMN_INDEX);
			} else if (tableModel.hasDuplicate(item) || hasDuplicate(item)) {
				showErrorMessage("Duplicate item");
				editCellAtCurrentRow(FROM_UNIT_COLUMN_INDEX);
			} else {
				changeSelection(selectedRow, QUANTITY_COLUMN_INDEX, false, false);
				editCellAtCurrentRow(QUANTITY_COLUMN_INDEX);
			}
			break;
		case StockQuantityConversionItemsTable.TO_UNIT_COLUMN_INDEX:
			if (selectedRow + 1 < getRowCount()) {
				changeSelection(selectedRow + 1, 0, false, false);
				editCellAt(selectedRow + 1, 0);
				getEditorComponent().requestFocusInWindow();
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

	public boolean validateQuantity(StockQuantityConversionItem item) {
		if (item.getQuantity() == null) {
			JOptionPane.showMessageDialog(this,
					"Quantity must be specified", "Error Message", JOptionPane.ERROR_MESSAGE);
			editCellAtCurrentRow(QUANTITY_COLUMN_INDEX);
			return false;
		} else {
			Product product = productService.getProduct(item.getProduct().getId());
			if (!product.hasAvailableUnitQuantity(item.getFromUnit(), item.getQuantity().intValue())) {
				JOptionPane.showMessageDialog(this,
						"Not enough stocks", "Error Message", JOptionPane.ERROR_MESSAGE);
				editCellAtCurrentRow(QUANTITY_COLUMN_INDEX);
				return false;
			} else {
				return true;
			}
		}
	}
	
	public boolean validateToUnit(StockQuantityConversionItem item) {
		if (StringUtils.isEmpty(item.getToUnit())) {
			showErrorMessage("To Unit must be specified");
			editCellAtCurrentRow(TO_UNIT_COLUMN_INDEX);
			return false;
		} else if (!item.getProduct().hasUnit(item.getToUnit())) {
			showErrorMessage("Product does not have unit specified");
			editCellAtCurrentRow(TO_UNIT_COLUMN_INDEX);
			return false;
		} else if (item.getFromUnit().equals(item.getToUnit())) {
			showErrorMessage("To Unit must be different from From Unit");
			editCellAtCurrentRow(TO_UNIT_COLUMN_INDEX);
			return false;
		} else {
			return true;
		}
	}
	
	public int getTotalNumberOfItems() {
		int totalNumberOfItems = stockQuantityConversion.getTotalNumberOfItems();
		if (isAdding()) {
			totalNumberOfItems += tableModel.getItems().size();
		}
		return totalNumberOfItems;
	}

	public void highlightQuantityColumn(StockQuantityConversionItem item) {
		int row = stockQuantityConversion.getItems().indexOf(item);
		changeSelection(row, QUANTITY_COLUMN_INDEX, false, false);
		editCellAt(row, QUANTITY_COLUMN_INDEX);
		getEditorComponent().requestFocusInWindow();
	}
	
	public void highlight() {
		if (!stockQuantityConversion.hasItems()) {
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
				final int row = getSelectedRow();
				
				switch (e.getColumn()) {
				case QUANTITY_COLUMN_INDEX:
					SwingUtilities.invokeLater(new Runnable() {

						@Override
						public void run() {
							if (validateQuantity(getCurrentlySelectedRowItem())) {
								tableModel.setValueAt("", row, CONVERTED_QUANTITY_COLUMN_INDEX);
								changeSelection(row, TO_UNIT_COLUMN_INDEX, false, false);
								editCellAt(row, TO_UNIT_COLUMN_INDEX);
								getEditorComponent().requestFocusInWindow();
							}
						}
					});
					break;
				case TO_UNIT_COLUMN_INDEX:
					SwingUtilities.invokeLater(new Runnable() {

						@Override
						public void run() {
							if (validateToUnit(getCurrentlySelectedRowItem())) {
								tableModel.setValueAt("", row, CONVERTED_QUANTITY_COLUMN_INDEX);
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
	
}