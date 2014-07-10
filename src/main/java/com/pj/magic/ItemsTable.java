package com.pj.magic;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.DefaultCellEditor;
import javax.swing.InputMap;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.KeyStroke;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.pj.magic.component.MagicTextField;
import com.pj.magic.dialog.ActionsTableModel;
import com.pj.magic.dialog.SelectActionDialog;
import com.pj.magic.model.Item;
import com.pj.magic.util.KeyUtil;

/*
 * [PJ 7/10/2014] 
 * ItemsTable has 2 modes: edit (default) and add (allows adding blank rows after the last row).
 * It also has 2 instances of ItemsTableModel (one for edit mode, one for add mode).
 */

@Component
@Scope("prototype")
public class ItemsTable extends JTable {
	
	private static final long serialVersionUID = -8416737029470549899L;
	
	public static final int PRODUCT_CODE_COLUMN_INDEX = 0;
	public static final int PRODUCT_DESCRIPTION_COLUMN_INDEX = 1;
	public static final int UNIT_COLUMN_INDEX = 2;
	public static final int QUANTITY_COLUMN_INDEX = 3;
	public static final int UNIT_PRICE_COLUMN_INDEX = 4;
	public static final int AMOUNT_COLUMN_INDEX = 5;
	private static final int PRODUCT_CODE_MAXIMUM_LENGTH = 9;
	private static final int UNIT_MAXIMUM_LENGTH = 3;
	private static final String TAB_ACTION_NAME = "tab";
	private static final String DOWN_ACTION_NAME = "down";
	private static final String SHOW_SELECTION_DIALOG_ACTION_NAME = "showSelectionDialog";
	private static final String SHOW_SELECT_ACTION_DIALOG_ACTION_NAME = "showSelectActionDialog";
	private static final String CANCEL_ACTION_NAME = "cancelAddMode";
	private static final String DELETE_ITEM_ACTION_NAME = "deleteItem";

	@Autowired
	private ItemsTableModel editModeTableModel; 
	
	@Autowired
	private ItemsTableModel addModeTableModel; 
	
	private boolean addMode;
	private List<Item> items = new ArrayList<>();
	
	@PostConstruct
	public void initialize() throws Exception {
		setItemsTableModel(editModeTableModel);
		setSurrendersFocusOnKeystroke(true);
		registerKeyBindings();
	}
	
	private void initializeColumns() {
		MagicTextField productCodeTextField = new MagicTextField();
		productCodeTextField.setMaximumLength(PRODUCT_CODE_MAXIMUM_LENGTH);
		productCodeTextField.addKeyListener(new ProductCodeFieldKeyListener());
		getColumnModel().getColumn(0).setCellEditor(new DefaultCellEditor(productCodeTextField));
		
		MagicTextField unitTextField = new MagicTextField();
		unitTextField.setMaximumLength(UNIT_MAXIMUM_LENGTH);
		unitTextField.addKeyListener(new UnitFieldKeyListener());
		getColumnModel().getColumn(2).setCellEditor(new DefaultCellEditor(unitTextField));
		
		final JTable table = this;
		JTextField quantityTextField = new JTextField();
		quantityTextField.addKeyListener(new KeyListener() {
			
			@Override
			public void keyTyped(KeyEvent e) {
			}
			
			@Override
			public void keyReleased(KeyEvent e) {
			}
			
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ENTER) {
					table.getCellEditor().stopCellEditing();
					KeyUtil.simulateDownKey();
				}
			}
		});
		getColumnModel().getColumn(3).setCellEditor(new DefaultCellEditor(quantityTextField));
	}
	
	public void setItemsTableModel(ItemsTableModel dataModel) {
		setModel(dataModel);
		initializeColumns();
	}
	
	public void switchToAddMode() {
		addMode = true;
		addModeTableModel.clearForNewInput();
		setItemsTableModel(addModeTableModel);
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
		getItemsTableModel().addNewRow();
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

	public boolean isCurrentRowValid() {
		if (isEditing()) {
			getCellEditor().stopCellEditing();
		}
		return getItemsTableModel().getRowItem(getSelectedRow()).isValid();
	}
	
	public ItemsTableModel getItemsTableModel() {
		return (ItemsTableModel)super.getModel();
	}
	
	public boolean isAdding() {
		return addMode;
	}
	
	public void switchToEditMode() {
		addMode = false;
		items.addAll(getItemsTableModel().getItems());
		editModeTableModel.setItems(items);
		setItemsTableModel(editModeTableModel);
		
		if (items.size() > 0) {
			changeSelection(0, 0, false, false);
		}
	}
	
	public void removeCurrentlySelectedRow() {
		int selectedRowIndex = getSelectedRow();
		Item item = getCurrentlySelectedRowItem();
		getItemsTableModel().removeItem(selectedRowIndex);
		items.remove(item);
		
		if (getItemsTableModel().hasItems()) {
			if (selectedRowIndex == getItemsTableModel().getRowCount()) {
				changeSelection(selectedRowIndex - 1, 0, false, false);
			} else {
				changeSelection(selectedRowIndex, 0, false, false);
			}
		}
		
	}
	
	public Item getCurrentlySelectedRowItem() {
		return getItemsTableModel().getRowItem(getSelectedRow());
	}
	
	protected void registerKeyBindings() {
		// TODO: shift + tab
		
		final ItemsTable table = this;
		final Action originalDownAction = getAction(KeyEvent.VK_DOWN);
		final Action originalEscapeAction = getAction(KeyEvent.VK_ESCAPE);
		
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
				SelectActionDialog dialog = new SelectActionDialog();
				dialog.setVisible(true);
				
				String action = dialog.getSelectedAction();
				if (ActionsTableModel.CREATE_ACTION.equals(action)) {
					table.switchToAddMode();
				} else if (ActionsTableModel.MODIFY_ACTION.equals(action)) {
					if (table.isAdding()) {
						table.switchToEditMode();
					}
				}
			}
		});
		actionMap.put(TAB_ACTION_NAME, new ItemsTableTabAction(this));
		actionMap.put(DOWN_ACTION_NAME, new AbstractAction() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				if (table.isAdding() && table.isLastRowSelected() && table.isCurrentRowValid()) {
					table.addNewRow();
				} else {
					originalDownAction.actionPerformed(e);
				}
			}
		});
		actionMap.put(SHOW_SELECTION_DIALOG_ACTION_NAME, new ShowSelectionDialogAction(this));
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
					if (table.getCurrentlySelectedRowItem().isValid()) {
						int confirm = JOptionPane.showConfirmDialog(table, "Do you wish to delete the selected item?");
						if (confirm == JOptionPane.OK_OPTION) {
							table.removeCurrentlySelectedRow();
						}
					}
				}
			}
		});
	}
	
	public void setAddModeTableModel(ItemsTableModel addModeTableModel) {
		this.addModeTableModel = addModeTableModel;
	}
	
	public void setEditModeTableModel(ItemsTableModel editModeTableModel) {
		this.editModeTableModel = editModeTableModel;
	}

}