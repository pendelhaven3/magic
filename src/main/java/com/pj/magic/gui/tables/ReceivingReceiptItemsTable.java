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
import com.pj.magic.gui.tables.models.ReceivingReceiptItemsTableModel;
import com.pj.magic.gui.tables.rowitems.ReceivingReceiptItemRowItem;
import com.pj.magic.model.ReceivingReceipt;
import com.pj.magic.model.ReceivingReceiptItem;
import com.pj.magic.service.ProductService;
import com.pj.magic.util.KeyUtil;
import com.pj.magic.util.NumberUtil;

@Component
public class ReceivingReceiptItemsTable extends ItemsTable {
	
	public static final int PRODUCT_CODE_COLUMN_INDEX = 0;
	public static final int PRODUCT_DESCRIPTION_COLUMN_INDEX = 1;
	public static final int UNIT_COLUMN_INDEX = 2;
	public static final int QUANTITY_COLUMN_INDEX = 3;
	public static final int COST_COLUMN_INDEX = 4;
	
	private static final String TAB_ACTION_NAME = "tab";
	private static final String DOWN_ACTION_NAME = "down";

	@Autowired private ProductService productService;
	@Autowired private ReceivingReceiptItemsTableModel tableModel;
	
	// dynamic columns
	private int orderedColumnIndex = 4;
	private int actualQuantityColumnIndex = 5;
	private int costColumnIndex;
	private int amountColumnIndex;
	
	private ReceivingReceipt receivingReceipt;
	private Action originalDownAction;
	private Action originalEscapeAction;
	
	@Autowired
	public ReceivingReceiptItemsTable(ReceivingReceiptItemsTableModel tableModel) {
		super(tableModel);
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
		
		DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
		rightRenderer.setHorizontalAlignment(JLabel.RIGHT);
		getColumnModel().getColumn(costColumnIndex).setCellRenderer(rightRenderer);
		getColumnModel().getColumn(amountColumnIndex).setCellRenderer(rightRenderer);
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

	public void switchToEditMode() {
		clearSelection();
		if (isEditing()) {
			getCellEditor().cancelCellEditing();
		}
		
		addMode = false;
		List<ReceivingReceiptItem> items = receivingReceipt.getItems();
		items.addAll(tableModel.getItems());
		tableModel.setItems(items);
		
		if (items.size() > 0) {
			changeSelection(0, 0, false, false);
		}
	}
	
	public ReceivingReceiptItemRowItem getCurrentlySelectedRowItem() {
		return tableModel.getRowItem(getSelectedRow());
	}
	
	private boolean hasDuplicate(ReceivingReceiptItemRowItem rowItem) {
		ReceivingReceiptItem checkItem = new ReceivingReceiptItem();
		checkItem.setProduct(rowItem.getProduct());
		checkItem.setUnit(rowItem.getUnit());
		
		for (ReceivingReceiptItem item : receivingReceipt.getItems()) {
			if (item.equals(checkItem) && item != rowItem.getItem()) {
				return true;
			}
		}
		return false;
	}
	
	public void setReceivingReceipt(ReceivingReceipt receivingReceipt) {
		clearSelection();
		addMode = false;
		this.receivingReceipt = receivingReceipt;
		
		tableModel.setReceivingReceipt(receivingReceipt);
		initializeColumns();
	}
	
	private ReceivingReceiptItem createBlankItem() {
		ReceivingReceiptItem item = new ReceivingReceiptItem();
		item.setParent(receivingReceipt);
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
		case ReceivingReceiptItemsTable.UNIT_COLUMN_INDEX:
			String fromUnit = (String)getValueAt(selectedRow, UNIT_COLUMN_INDEX);
			if (StringUtils.isEmpty(fromUnit)) {
				showErrorMessage("Unit must be specified");
				editCellAtCurrentRow(UNIT_COLUMN_INDEX);
			} else if (!rowItem.getProduct().getUnits().contains(fromUnit)) {
				showErrorMessage("Product does not have unit specified");
				editCellAtCurrentRow(UNIT_COLUMN_INDEX);
			} else {
				int nextField = QUANTITY_COLUMN_INDEX;
				changeSelection(selectedRow, nextField, false, false);
				editCellAtCurrentRow(nextField);
			}
			break;
		}
	}

	public int getTotalNumberOfItems() {
//		int totalNumberOfItems = receivingReceipt.getTotalNumberOfItems();
//		if (isAdding()) {
//			totalNumberOfItems += tableModel.getItems().size();
//		}
//		return totalNumberOfItems;
		return 0;
	}

	public void highlightQuantityColumn(ReceivingReceiptItem item) {
		int row = receivingReceipt.getItems().indexOf(item);
		changeSelection(row, QUANTITY_COLUMN_INDEX, false, false);
		editCellAt(row, QUANTITY_COLUMN_INDEX);
		getEditorComponent().requestFocusInWindow();
	}
	
	public void highlight() {
//		if (!receivingReceipt.hasItems()) {
//			switchToAddMode();
//		} else {
//			if (receivingReceipt.isOrdered()) {
//				changeSelection(0, actualQuantityColumnIndex, false, false);
//				editCellAtCurrentRow(actualQuantityColumnIndex);
//			} else {
//				changeSelection(0, 0, false, false);
//				requestFocusInWindow();
//			}
//		}
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
//							if (validateQuantity(getCurrentlySelectedRowItem())) {
//								tableModel.setValueAt("", row, amountColumnIndex);
//								changeSelection(row, costColumnIndex, false, false);
//								editCellAt(row, costColumnIndex);
//								getEditorComponent().requestFocusInWindow();
//							}
						}
					});
				} else if (column == costColumnIndex) {
					SwingUtilities.invokeLater(new Runnable() {

						@Override
						public void run() {
							if (validateCost(getCurrentlySelectedRowItem())) {
								tableModel.setValueAt("", row, amountColumnIndex);
								if (isAdding() && isLastRowSelected()) {
//									addNewRow();
								} else {
									if (!isLastRowSelected()) {
										int nextColumn = PRODUCT_CODE_COLUMN_INDEX;
										changeSelection(row + 1, nextColumn, false, false);
										editCellAtCurrentRow(nextColumn);
									}
								}
							}
						}
					});
				}
			}
		});
	}
	
	private boolean validateCost(ReceivingReceiptItemRowItem rowItem) {
		boolean valid = false;
		if (StringUtils.isEmpty(rowItem.getCost())) {
			showErrorMessage("Cost must be specified");
		} else if (!NumberUtil.isAmount(rowItem.getCost())){
			showErrorMessage("Cost must be a valid amount");
		} else if (rowItem.getCostAsBigDecimal().equals(BigDecimal.ZERO.setScale(2))){
			showErrorMessage("Cost must not be 0");
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