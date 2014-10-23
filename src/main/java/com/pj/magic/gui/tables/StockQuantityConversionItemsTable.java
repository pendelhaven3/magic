package com.pj.magic.gui.tables;

import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.List;

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
import javax.swing.table.TableColumnModel;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.pj.magic.gui.component.MagicCellEditor;
import com.pj.magic.gui.component.MagicTextField;
import com.pj.magic.gui.dialog.SelectProductDialog;
import com.pj.magic.gui.dialog.SelectUnitDialog;
import com.pj.magic.gui.tables.models.StockQuantityConversionItemsTableModel;
import com.pj.magic.gui.tables.rowitems.StockQuantityConversionItemRowItem;
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
public class StockQuantityConversionItemsTable extends MagicTable {
	
	public static final int PRODUCT_CODE_COLUMN_INDEX = 0;
	public static final int PRODUCT_DESCRIPTION_COLUMN_INDEX = 1;
	public static final int FROM_UNIT_COLUMN_INDEX = 2;
	public static final int QUANTITY_COLUMN_INDEX = 3;
	public static final int TO_UNIT_COLUMN_INDEX = 4;
	public static final int CONVERTED_QUANTITY_COLUMN_INDEX = 5;
	private static final int PRODUCT_CODE_MAXIMUM_LENGTH = 9;
	private static final int UNIT_MAXIMUM_LENGTH = 3;
	private static final int QUANTITY_MAXIMUM_LENGTH = 3;
	private static final String SHOW_SELECTION_DIALOG_ACTION_NAME = "showSelectionDialog";
	private static final String ADD_ITEM_ACTION_NAME = "addItem";
	private static final String CANCEL_ACTION_NAME = "cancelAddMode";
	private static final String DELETE_ITEM_ACTION_NAME = "deleteItem";
	private static final String F4_ACTION_NAME = "F4";

	@Autowired private SelectProductDialog selectProductDialog;
	@Autowired private SelectUnitDialog selectUnitDialog;
	@Autowired private ProductService productService;
	@Autowired private StockQuantityConversionItemsTableModel tableModel;
	
	private boolean addMode;
	private StockQuantityConversion stockQuantityConversion;
	
	@Autowired
	public StockQuantityConversionItemsTable(StockQuantityConversionItemsTableModel tableModel) {
		super(tableModel);
		initializeColumns();
		initializeModelListener();
		registerKeyBindings();
	}
	
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
		productCodeTextField.addKeyListener(new KeyAdapter() {
			
			@Override
			public void keyReleased(KeyEvent event) {
				if (KeyUtil.isAlphaNumericKeyCode(event.getKeyCode())) {
					JTextField textField = (JTextField)event.getComponent();
					if (textField.getText().length() == PRODUCT_CODE_MAXIMUM_LENGTH) {
						getCellEditor().stopCellEditing();
					};
				}
			}			
		});
		getColumnModel().getColumn(PRODUCT_CODE_COLUMN_INDEX).setCellEditor(
				new ProductCodeCellEditor(productCodeTextField));
		
		MagicTextField fromUnitTextField = new MagicTextField();
		fromUnitTextField.setMaximumLength(UNIT_MAXIMUM_LENGTH);
		fromUnitTextField.addKeyListener(new KeyAdapter() {
			
			@Override
			public void keyReleased(KeyEvent event) {
				if (KeyUtil.isAlphaNumericKeyCode(event.getKeyCode())) {
					JTextField textField = (JTextField)event.getComponent();
					if (textField.getText().length() == UNIT_MAXIMUM_LENGTH) {
						getCellEditor().stopCellEditing();
					}
				}
			}
		});
		getColumnModel().getColumn(FROM_UNIT_COLUMN_INDEX).setCellEditor(new FromUnitCellEditor(fromUnitTextField));
		
		MagicTextField toUnitTextField = new MagicTextField();
		toUnitTextField.setMaximumLength(UNIT_MAXIMUM_LENGTH);
		toUnitTextField.addKeyListener(new KeyAdapter() {
			
			@Override
			public void keyReleased(KeyEvent event) {
				if (KeyUtil.isAlphaNumericKeyCode(event.getKeyCode())) {
					JTextField textField = (JTextField)event.getComponent();
					if (textField.getText().length() == UNIT_MAXIMUM_LENGTH) {
						getCellEditor().stopCellEditing();
					}
				}
			}
		});
		getColumnModel().getColumn(TO_UNIT_COLUMN_INDEX).setCellEditor(new ToUnitCellEditor(toUnitTextField));
		
		MagicTextField quantityTextField = new MagicTextField();
		quantityTextField.setMaximumLength(QUANTITY_MAXIMUM_LENGTH);
		quantityTextField.setNumbersOnly(true);
		getColumnModel().getColumn(QUANTITY_COLUMN_INDEX)
			.setCellEditor(new QuantityCellEditor(quantityTextField));
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
		tableModel.setStockQuantityConversion(stockQuantityConversion);
		
		if (items.size() > 0) {
			changeSelection(0, 0, false, false);
		}
	}
	
	public void removeCurrentlySelectedRow() {
		if (tableModel.hasItems()) {
			if (getCurrentlySelectedRowItem().isValid()) { // check valid row to prevent deleting the blank row
				if (confirm("Do you wish to delete the selected item?")) {
					doDeleteCurrentlySelectedRow();
				}
			}
		}
	}
	
	public void doDeleteCurrentlySelectedRow() {
		int selectedRowIndex = getSelectedRow();
		StockQuantityConversionItem item = getCurrentlySelectedRowItem().getItem();
		// TODO: review this
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
	
	public StockQuantityConversionItemRowItem getCurrentlySelectedRowItem() {
		return tableModel.getRowItem(getSelectedRow());
	}
	
	public void editCellAtCurrentRow(int columnIndex) {
		editCellAt(getSelectedRow(), columnIndex);
		getEditorComponent().requestFocusInWindow();
	}
	
	private boolean hasDuplicate(String toUnit, StockQuantityConversionItemRowItem rowItem) {
		for (StockQuantityConversionItem item : stockQuantityConversion.getItems()) {
			if (item.getProduct().equals(rowItem.getProduct()) && item.getFromUnit().equals(rowItem.getFromUnit())
					&& item.getToUnit().equals(toUnit) && item != rowItem.getItem()) {
				return true;
			}
		}
		return tableModel.hasDuplicate(toUnit, rowItem);
	}
	
	public void setStockQuantityConversion(StockQuantityConversion stockQuantityConversion) {
		clearSelection();
		addMode = false;
		this.stockQuantityConversion = stockQuantityConversion;
		tableModel.setStockQuantityConversion(stockQuantityConversion);
	}
	
	private StockQuantityConversionItem createBlankItem() {
		StockQuantityConversionItem item = new StockQuantityConversionItem();
		item.setParent(stockQuantityConversion);
		return item;
	}
	
	protected void registerKeyBindings() {
		InputMap inputMap = getInputMap(JTable.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
		inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0), DELETE_ITEM_ACTION_NAME);
		inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_F4, 0), F4_ACTION_NAME);
		inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_F5, 0), SHOW_SELECTION_DIALOG_ACTION_NAME);
		inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_F10, 0), ADD_ITEM_ACTION_NAME);
		inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), CANCEL_ACTION_NAME);
		
		ActionMap actionMap = getActionMap();
		actionMap.put(ADD_ITEM_ACTION_NAME, new AbstractAction() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				switchToAddMode();
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
				if (isEditing()) {
					getCellEditor().cancelCellEditing();
					if (getCurrentlySelectedRowItem().isUpdating()) {
						tableModel.reset(getSelectedRow());
					}
				} else if (isAdding()) {
					switchToEditMode();
				}
			}
		});
		actionMap.put(DELETE_ITEM_ACTION_NAME, new AbstractAction() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				removeCurrentlySelectedRow();
			}
		});
		actionMap.put(F4_ACTION_NAME, new AbstractAction() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				openSelectProductDialogUsingPreviousProductCode();
			}
		});
	}
	
	protected void showSelectionDialog() {
		if (isProductCodeFieldSelected()) {
			if (!isEditing()) {
				editCellAt(getSelectedRow(), PRODUCT_CODE_COLUMN_INDEX);
			}
			openSelectProductDialog((String)getCellEditor().getCellEditorValue());
		} else if (isUnitFieldSelected()) {
			openSelectUnitDialog();
		}
	}

	private void openSelectUnitDialog() {
		if (!isEditing()) {
			editCellAt(getSelectedRow(), getSelectedColumn());
		}
		selectUnitDialog.setUnits(getCurrentlySelectedRowItem().getProduct().getUnits());
		selectUnitDialog.searchUnits((String)getCellEditor().getCellEditorValue());
		selectUnitDialog.setVisible(true);
		
		String unit = selectUnitDialog.getSelectedUnit();
		if (unit != null) {
			((JTextField)getEditorComponent()).setText(unit);
			getCellEditor().stopCellEditing();
		}
	}

	private void openSelectProductDialog(String productCodeCriteria) {
		selectProductDialog.searchProducts(productCodeCriteria);
		selectProductDialog.setVisible(true);
		
		String productCode = selectProductDialog.getSelectedProductCode();
		if (productCode != null) {
			((JTextField)getEditorComponent()).setText(productCode);
			getCellEditor().stopCellEditing();
		}
	}

	private void openSelectProductDialogUsingPreviousProductCode() {
		if (!(isAdding() && isLastRowSelected() && isProductCodeFieldSelected())) {
			return;
		}
		
		if (!isEditing()) {
			editCellAt(getSelectedRow(), getSelectedColumn());
		}
		
		if (tableModel.hasNonBlankItem()) {
			openSelectProductDialog(getPreviousRowItem().getProductCode());
		} else if (stockQuantityConversion.hasItems()) {
			List<StockQuantityConversionItem> items = stockQuantityConversion.getItems();
			openSelectProductDialog(items.get(items.size() - 1).getProduct().getCode());
		}
	}

	private StockQuantityConversionItemRowItem getPreviousRowItem() {
		return tableModel.getRowItem(getSelectedRow() - 1);
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
				final AbstractTableModel model = (AbstractTableModel)e.getSource();
				final int row = e.getFirstRow();
				final int column = e.getColumn();
				
				SwingUtilities.invokeLater(new Runnable() {
					
					@Override
					public void run() {
						switch (column) {
						case PRODUCT_CODE_COLUMN_INDEX:
							// Fire entire row. Firing single cells causes this listener to be re-triggered.
							model.fireTableRowsUpdated(row, row);
							selectAndEditCellAt(row, FROM_UNIT_COLUMN_INDEX);
							break;
						case FROM_UNIT_COLUMN_INDEX:
							model.fireTableRowsUpdated(row, row);
							selectAndEditCellAt(row, QUANTITY_COLUMN_INDEX);
							break;
						case QUANTITY_COLUMN_INDEX:
							model.fireTableRowsUpdated(row, row);
							selectAndEditCellAt(row, TO_UNIT_COLUMN_INDEX);
							break;
						case TO_UNIT_COLUMN_INDEX:
							model.fireTableCellUpdated(row, CONVERTED_QUANTITY_COLUMN_INDEX);
							if (isAdding() && isLastRowSelected() && getCurrentlySelectedRowItem().isValid()) {
								addNewRow();
							}
							break;
						}
					}
				});
			}
		});
	}
	
	private class ProductCodeCellEditor extends MagicCellEditor {

		public ProductCodeCellEditor(JTextField textField) {
			super(textField);
		}
		
		@Override
		public boolean stopCellEditing() {
			String code = ((JTextField)getComponent()).getText();
			boolean valid = false;
			if (StringUtils.isEmpty(code)) {
				showErrorMessage("Product code must be specified");
			} else if (productService.findProductByCode(code) == null) {
				showErrorMessage("No product matching code specified");
			} else {
				valid = true;
			}
			return (valid) ? super.stopCellEditing() : false;
		}

	}
	
	private class FromUnitCellEditor extends MagicCellEditor {
		
		public FromUnitCellEditor(JTextField textField) {
			super(textField);
		}
		
		@Override
		public boolean stopCellEditing() {
			String unit = ((JTextField)getComponent()).getText();
			boolean valid = false;
			if (StringUtils.isEmpty(unit)) {
				showErrorMessage("From Unit must be specified");
			} else {
				StockQuantityConversionItemRowItem rowItem = getCurrentlySelectedRowItem();
				if (!rowItem.getProduct().hasUnit(unit)) {
					showErrorMessage("Product does not have unit specified");
				} else {
					valid = true;
				}
			}
			return (valid) ? super.stopCellEditing() : false;
		}
		
	}
	
	private class ToUnitCellEditor extends MagicCellEditor {
		
		public ToUnitCellEditor(JTextField textField) {
			super(textField);
		}
		
		@Override
		public boolean stopCellEditing() {
			String unit = ((JTextField)getComponent()).getText();
			boolean valid = false;
			if (StringUtils.isEmpty(unit)) {
				showErrorMessage("To Unit must be specified");
			} else {
				StockQuantityConversionItemRowItem rowItem = getCurrentlySelectedRowItem();
				if (!rowItem.getProduct().hasUnit(unit)) {
					showErrorMessage("Product does not have unit specified");
				} else if (unit.equals(rowItem.getFromUnit())) {
					showErrorMessage("From Unit and To Unit must not be the same");
				} else if (hasDuplicate(unit, rowItem)) {
					showErrorMessage("Duplicate item");
				} else {
					valid = true;
				}
			}
			return (valid) ? super.stopCellEditing() : false;
		}
		
	}
	
	private class QuantityCellEditor extends MagicCellEditor {
		
		public QuantityCellEditor(JTextField textField) {
			super(textField);
		}
		
		@Override
		public boolean stopCellEditing() {
			String quantity = ((JTextField)getComponent()).getText();
			boolean valid = false;
			if (StringUtils.isEmpty(quantity)) {
				showErrorMessage("Quantity must be specified");
			} else if (Integer.parseInt(quantity) == 0) {
				showErrorMessage("Quantity must be greater than 0");
			} else {
				StockQuantityConversionItemRowItem rowItem = getCurrentlySelectedRowItem();
				Product product = productService.getProduct(rowItem.getProduct().getId());
				if (!product.hasAvailableUnitQuantity(rowItem.getFromUnit(), Integer.parseInt(quantity))) {
					showErrorMessage("Not enough stocks");
				} else {
					valid = true;
				}
			}
			return (valid) ? super.stopCellEditing() : false;
		}
		
	}
	
}