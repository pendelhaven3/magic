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

import com.pj.magic.gui.component.AbstractKeyListener;
import com.pj.magic.gui.component.MagicTextField;
import com.pj.magic.gui.dialog.SelectActionDialog;
import com.pj.magic.gui.dialog.SelectProductDialog;
import com.pj.magic.gui.dialog.SelectUnitDialog;
import com.pj.magic.gui.tables.models.ActionsTableModel;
import com.pj.magic.gui.tables.models.SalesRequisitionItemsTableModel;
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
public class SalesRequisitionItemsTable extends JTable {
	
	public static final int PRODUCT_CODE_COLUMN_INDEX = 0;
	public static final int PRODUCT_DESCRIPTION_COLUMN_INDEX = 1;
	public static final int UNIT_COLUMN_INDEX = 2;
	public static final int QUANTITY_COLUMN_INDEX = 3;
	public static final int UNIT_PRICE_COLUMN_INDEX = 4;
	public static final int AMOUNT_COLUMN_INDEX = 5;
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
	
	private boolean addMode;
	private SalesRequisition salesRequisition;
	private Action originalDownAction;
	private Action originalEscapeAction;
	
	@Autowired
	public SalesRequisitionItemsTable(SalesRequisitionItemsTableModel tableModel) {
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
			salesRequisition.getItems().addAll(getItemsTableModel().getItems());
		}
		
		addMode = true;
		getItemsTableModel().clearAndAddItem(createBlankItem());
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
		getItemsTableModel().addItem(createBlankItem());
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
		return getSelectedRow() + 1 == getItemsTableModel().getRowCount();
	}

	// TODO: Remove this method
	public SalesRequisitionItemsTableModel getItemsTableModel() {
		return (SalesRequisitionItemsTableModel)super.getModel();
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
		items.addAll(getItemsTableModel().getItems());
		getItemsTableModel().setItems(items);
		
		if (items.size() > 0) {
			changeSelection(0, 0, false, false);
		}
	}
	
	public void removeCurrentlySelectedRow() {
		
		int selectedRowIndex = getSelectedRow();
		SalesRequisitionItem item = getCurrentlySelectedRowItem();
		clearSelection(); // clear row selection so model listeners will not cause exceptions while model items are being updated
		salesRequisition.getItems().remove(item);
		getItemsTableModel().removeItem(selectedRowIndex);
		
		if (getItemsTableModel().hasItems()) {
			if (selectedRowIndex == getModel().getRowCount()) {
				changeSelection(selectedRowIndex - 1, 0, false, false);
			} else {
				changeSelection(selectedRowIndex, 0, false, false);
			}
		}
	}
	
	public SalesRequisitionItem getCurrentlySelectedRowItem() {
		return getItemsTableModel().getRowItem(getSelectedRow());
	}
	
	public void editCellAtCurrentRow(int columnIndex) {
		editCellAt(getSelectedRow(), columnIndex);
		getEditorComponent().requestFocusInWindow();
	}
	
	private boolean hasDuplicate(SalesRequisitionItem checkItem) {
		for (SalesRequisitionItem item : salesRequisition.getItems()) {
			if (item.equals(checkItem) && item != checkItem) {
				return true;
			}
		}
		return false;
	}
	
	public void setSalesRequisition(SalesRequisition salesRequisition) {
		clearSelection();
		addMode = false;
		this.salesRequisition = salesRequisition;
		getItemsTableModel().setSalesRequisition(salesRequisition);
	}
	
	private SalesRequisitionItem createBlankItem() {
		SalesRequisitionItem item = new SalesRequisitionItem();
		item.setParent(salesRequisition);
		return item;
	}
	
	protected void registerKeyBindings() {
		// TODO: shift + tab
		// TODO: Remove table references inside anonymous classes
		// TODO: Modify on other columns dont work
		
		final SalesRequisitionItemsTable table = this;
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
				if (getSelectedRow() == -1) {
					return;
				}
				
				if (table.isEditing()) {
					table.getCellEditor().stopCellEditing();
				}
				
				int selectedColumn = table.getSelectedColumn();
				int selectedRow = table.getSelectedRow();
				SalesRequisitionItem item = table.getCurrentlySelectedRowItem();
				
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
				case SalesRequisitionItemsTable.UNIT_COLUMN_INDEX:
					String unit = (String)table.getValueAt(selectedRow, UNIT_COLUMN_INDEX);
					
					if (StringUtils.isEmpty(unit)) {
						JOptionPane.showMessageDialog(table,
								"Unit must be specified", "Error Message", JOptionPane.ERROR_MESSAGE);
						editCellAtCurrentRow(UNIT_COLUMN_INDEX);
					} else if (!item.getProduct().getUnits().contains(unit)) {
						JOptionPane.showMessageDialog(table,
								"Product does not have unit specified", "Error Message", JOptionPane.ERROR_MESSAGE);
						editCellAtCurrentRow(UNIT_COLUMN_INDEX);
					} else if (getItemsTableModel().hasDuplicate(item) || hasDuplicate(item)) {
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
					selectProductDialog.searchProducts((String)getCellEditor().getCellEditorValue(),
							salesRequisition.getPricingScheme());
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
				if (table.getItemsTableModel().hasItems()) {
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
	
	public boolean validateQuantity(SalesRequisitionItem item) {
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
		BigDecimal totalAmount = salesRequisition.getTotalAmount();
		if (isAdding()) {
			for (SalesRequisitionItem item : getItemsTableModel().getItems()) {
				if (item.isFilledUp()) {
					totalAmount = totalAmount.add(item.getAmount());
				}
			}
		}
		return totalAmount;
	}
	
	public int getTotalNumberOfItems() {
		int totalNumberOfItems = salesRequisition.getTotalNumberOfItems();
		if (isAdding()) {
			totalNumberOfItems += getItemsTableModel().getItems().size();
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
	
}