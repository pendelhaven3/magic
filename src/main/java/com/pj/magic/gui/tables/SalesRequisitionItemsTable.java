package com.pj.magic.gui.tables;

import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.math.BigDecimal;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumnModel;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.pj.magic.gui.component.MagicCellEditor;
import com.pj.magic.gui.component.MagicTextField;
import com.pj.magic.gui.dialog.SelectProductDialog;
import com.pj.magic.gui.dialog.SelectUnitDialog;
import com.pj.magic.gui.tables.models.SalesRequisitionItemsTableModel;
import com.pj.magic.gui.tables.rowitems.SalesRequisitionItemRowItem;
import com.pj.magic.model.Product;
import com.pj.magic.model.SalesRequisition;
import com.pj.magic.model.SalesRequisitionItem;
import com.pj.magic.service.ProductService;
import com.pj.magic.util.KeyUtil;

/*
 * [PJ 7/10/2014] 
 * ItemsTable has 2 modes: edit (default) and add (allows adding blank rows after the last row).
 * 
 */

@Component
public class SalesRequisitionItemsTable extends MagicTable {
	
	public static final int PRODUCT_CODE_COLUMN_INDEX = 0;
	public static final int PRODUCT_DESCRIPTION_COLUMN_INDEX = 1;
	public static final int UNIT_COLUMN_INDEX = 2;
	public static final int QUANTITY_COLUMN_INDEX = 3;
	public static final int UNIT_PRICE_COLUMN_INDEX = 4;
	public static final int AMOUNT_COLUMN_INDEX = 5;
	private static final int PRODUCT_CODE_MAXIMUM_LENGTH = 9;
	private static final int UNIT_MAXIMUM_LENGTH = 3;
	private static final int QUANTITY_MAXIMUM_LENGTH = 3;
	private static final String SHOW_SELECTION_DIALOG_ACTION_NAME = "showSelectionDialog";
	private static final String F10_ACTION_NAME = "F10";
	private static final String CANCEL_ACTION_NAME = "cancelAddMode";
	private static final String DELETE_ITEM_ACTION_NAME = "deleteItem";
	private static final String F4_ACTION_NAME = "F4";

	@Autowired private SelectProductDialog selectProductDialog;
	@Autowired private SelectUnitDialog selectUnitDialog;
	@Autowired private ProductService productService;
	@Autowired private SalesRequisitionItemsTableModel tableModel;
	
	private boolean addMode;
	private SalesRequisition salesRequisition;
	
	@Autowired
	public SalesRequisitionItemsTable(SalesRequisitionItemsTableModel tableModel) {
		super(tableModel);
	}
	
	@PostConstruct
	public void initialize() {
		initializeColumns();
		initializeModelListener();
		registerKeyBindings();
	}
	
	private void initializeColumns() {
		TableColumnModel columnModel = getColumnModel();
		columnModel.getColumn(PRODUCT_CODE_COLUMN_INDEX).setPreferredWidth(120);
		columnModel.getColumn(PRODUCT_DESCRIPTION_COLUMN_INDEX).setPreferredWidth(300);
		columnModel.getColumn(UNIT_COLUMN_INDEX).setPreferredWidth(70);
		columnModel.getColumn(QUANTITY_COLUMN_INDEX).setPreferredWidth(70);
		columnModel.getColumn(UNIT_PRICE_COLUMN_INDEX).setPreferredWidth(100);
		columnModel.getColumn(AMOUNT_COLUMN_INDEX).setPreferredWidth(100);
		
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
		columnModel.getColumn(PRODUCT_CODE_COLUMN_INDEX).setCellEditor(new ProductCodeCellEditor(productCodeTextField));
		
		MagicTextField unitTextField = new MagicTextField();
		unitTextField.setMaximumLength(UNIT_MAXIMUM_LENGTH);
		unitTextField.addKeyListener(new KeyAdapter() {
			
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
		columnModel.getColumn(UNIT_COLUMN_INDEX).setCellEditor(new UnitCellEditor(unitTextField));
		
		MagicTextField quantityTextField = new MagicTextField();
		quantityTextField.setMaximumLength(QUANTITY_MAXIMUM_LENGTH);
		quantityTextField.setNumbersOnly(true);
		columnModel.getColumn(QUANTITY_COLUMN_INDEX).setCellEditor(new QuantityCellEditor(quantityTextField));
		
		DefaultTableCellRenderer cellRenderer = new DefaultTableCellRenderer();
		cellRenderer.setHorizontalAlignment(JLabel.RIGHT);
		columnModel.getColumn(UNIT_PRICE_COLUMN_INDEX).setCellRenderer(cellRenderer);
		columnModel.getColumn(AMOUNT_COLUMN_INDEX).setCellRenderer(cellRenderer);
	}
	
	public void switchToAddMode() {
		clearSelection();
		if (isEditing()) {
			getCellEditor().cancelCellEditing();
		}
		
		if (addMode) {
			salesRequisition.getItems().addAll(tableModel.getItems());
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
		List<SalesRequisitionItem> items = salesRequisition.getItems();
		items.addAll(tableModel.getItems());
		tableModel.setItems(items);
		
		if (items.size() > 0) {
			changeSelection(0, 0, false, false);
		}
	}
	
	private void doDeleteCurrentlySelectedItem() {
		int selectedRowIndex = getSelectedRow();
		SalesRequisitionItem item = getCurrentlySelectedRowItem().getItem();
		clearSelection(); // clear row selection so model listeners will not cause exceptions while model items are being updated
		salesRequisition.getItems().remove(item);
		tableModel.removeItem(selectedRowIndex);
		
		if (tableModel.hasItems()) {
			if (selectedRowIndex == getModel().getRowCount()) {
				changeSelection(selectedRowIndex - 1, 0, false, false);
			} else {
				changeSelection(selectedRowIndex, 0, false, false);
			}
		}
	}
	
	public SalesRequisitionItemRowItem getCurrentlySelectedRowItem() {
		return tableModel.getRowItem(getSelectedRow());
	}
	
	public void editCellAtCurrentRow(int columnIndex) {
		editCellAt(getSelectedRow(), columnIndex);
		getEditorComponent().requestFocusInWindow();
	}
	
	private boolean hasDuplicate(String unit, SalesRequisitionItemRowItem rowItem) {
		for (SalesRequisitionItem item : salesRequisition.getItems()) {
			if (item.getProduct().equals(rowItem.getProduct()) 
					&& item.getUnit().equals(unit) && item != rowItem.getItem()) {
				return true;
			}
		}
		return tableModel.hasDuplicate(unit, rowItem);
	}
	
	public void setSalesRequisition(SalesRequisition salesRequisition) {
		clearSelection();
		addMode = false;
		this.salesRequisition = salesRequisition;
		tableModel.setSalesRequisition(salesRequisition);
	}
	
	private SalesRequisitionItem createBlankItem() {
		SalesRequisitionItem item = new SalesRequisitionItem();
		item.setParent(salesRequisition);
		return item;
	}
	
	protected void registerKeyBindings() {
		InputMap inputMap = getInputMap(JTable.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
		inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0), DELETE_ITEM_ACTION_NAME);
		inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_F4, 0), F4_ACTION_NAME);
		inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_F5, 0), SHOW_SELECTION_DIALOG_ACTION_NAME);
		inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_F10, 0), F10_ACTION_NAME);
		inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), CANCEL_ACTION_NAME);
		
		ActionMap actionMap = getActionMap();
		actionMap.put(F10_ACTION_NAME, new AbstractAction() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				switchToAddMode();
			}
		});
		actionMap.put(SHOW_SELECTION_DIALOG_ACTION_NAME, new AbstractAction() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (isProductCodeFieldSelected()) {
					if (!isEditing()) {
						editCellAt(getSelectedRow(), PRODUCT_CODE_COLUMN_INDEX);
					}
					openSelectProductDialog((String)getCellEditor().getCellEditorValue());
				} else if (isUnitFieldSelected()) {
					openSelectUnitDialog();
				}
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
				removeCurrentlySelectedItem();
			}
		});
		
		actionMap.put(F4_ACTION_NAME, new AbstractAction() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				openSelectProductDialogUsingPreviousProductCode();
			}
		});
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

	protected void openSelectUnitDialog() {
		if (!isEditing()) {
			editCellAt(getSelectedRow(), UNIT_COLUMN_INDEX);
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
		selectProductDialog.searchProducts(productCodeCriteria, salesRequisition.getPricingScheme());
		selectProductDialog.setVisible(true);
		
		String productCode = selectProductDialog.getSelectedProductCode();
		if (productCode != null) {
			((JTextField)getEditorComponent()).setText(productCode);
			getCellEditor().stopCellEditing();
		}
	}
	
	protected void openSelectProductDialogUsingPreviousProductCode() {
		if (!(isAdding() && isLastRowSelected() && isProductCodeFieldSelected())) {
			return;
		}
		
		if (!isEditing()) {
			editCellAt(getSelectedRow(), getSelectedColumn());
		}
		
		if (tableModel.hasNonBlankItem()) {
			openSelectProductDialog(getPreviousRowItem().getProductCode());
		} else if (salesRequisition.hasItems()) {
			List<SalesRequisitionItem> items = salesRequisition.getItems();
			openSelectProductDialog(items.get(items.size() - 1).getProduct().getCode());
		}
	}

	private SalesRequisitionItemRowItem getPreviousRowItem() {
		return tableModel.getRowItem(getSelectedRow() - 1);
	}

	public BigDecimal getTotalAmount() {
		BigDecimal totalAmount = salesRequisition.getTotalAmount();
		if (isAdding()) {
			for (SalesRequisitionItemRowItem rowItem : tableModel.getRowItems()) {
				if (rowItem.isValid()) {
					totalAmount = totalAmount.add(rowItem.getAmount());
				}
			}
		}
		return totalAmount;
	}
	
	public int getTotalNumberOfItems() {
		int totalNumberOfItems = salesRequisition.getTotalNumberOfItems();
		if (isAdding()) {
			totalNumberOfItems += tableModel.getItems().size();
		}
		return totalNumberOfItems;
	}

	public void highlightColumn(SalesRequisitionItem item, int column) {
		int row = salesRequisition.getItems().indexOf(item);
		changeSelection(row, column, false, false);
		editCellAt(row, column);
		getEditorComponent().requestFocusInWindow();
	}
	
	public void highlight() {
		if (!salesRequisition.hasItems()) {
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
							model.fireTableCellUpdated(row, UNIT_PRICE_COLUMN_INDEX);
							model.fireTableCellUpdated(row, AMOUNT_COLUMN_INDEX);
							selectAndEditCellAt(row, UNIT_COLUMN_INDEX);
							break;
						case UNIT_COLUMN_INDEX:
							model.fireTableCellUpdated(row, UNIT_PRICE_COLUMN_INDEX);
							model.fireTableCellUpdated(row, AMOUNT_COLUMN_INDEX);
							selectAndEditCellAt(row, QUANTITY_COLUMN_INDEX);
							break;
						case QUANTITY_COLUMN_INDEX:
							model.fireTableCellUpdated(row, AMOUNT_COLUMN_INDEX);
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

	private void selectAndEditCellAt(int row, int column) {
		changeSelection(row, column, false, false);
		editCellAt(row, column);
		getEditorComponent().requestFocusInWindow();
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
	
	private class UnitCellEditor extends MagicCellEditor {
		
		public UnitCellEditor(JTextField textField) {
			super(textField);
		}
		
		@Override
		public boolean stopCellEditing() {
			String unit = ((JTextField)getComponent()).getText();
			boolean valid = false;
			if (StringUtils.isEmpty(unit)) {
				showErrorMessage("Unit must be specified");
			} else {
				SalesRequisitionItemRowItem rowItem = getCurrentlySelectedRowItem();
				if (!rowItem.getProduct().hasUnit(unit)) {
					showErrorMessage("Product does not have unit specified");
				} else if (hasDuplicate(unit, rowItem)) {
					showErrorMessage("Duplicate item");
				} else if (rowItem.getProduct().hasNoSellingPrice(unit)) {
					showErrorMessage("No selling price");
				} else if (rowItem.getProduct().hasSellingPriceLessThanCost(unit)) {
					showErrorMessage("Selling price less than cost");
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
				SalesRequisitionItemRowItem rowItem = getCurrentlySelectedRowItem();
				Product product = productService.getProduct(rowItem.getProduct().getId());
				if (!product.hasAvailableUnitQuantity(rowItem.getUnit(), Integer.parseInt(quantity))) {
					showErrorMessage("Not enough stocks");
				} else {
					valid = true;
				}
			}
			return (valid) ? super.stopCellEditing() : false;
		}
		
	}
	
}