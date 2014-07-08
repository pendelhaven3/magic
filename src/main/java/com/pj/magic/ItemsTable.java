package com.pj.magic;

import java.awt.event.KeyEvent;

import javax.swing.Action;
import javax.swing.DefaultCellEditor;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.table.TableModel;
import javax.swing.text.AbstractDocument;

import org.apache.commons.lang.StringUtils;

public class ItemsTable extends JTable {
	
	private static final long serialVersionUID = -8416737029470549899L;
	
	public static final int PRODUCT_CODE_COLUMN_INDEX = 0;
	public static final int UNIT_COLUMN_INDEX = 2;
	public static final int QUANTITY_COLUMN_INDEX = 3;
	
	private UppercaseDocumentFilter uppercaseDocumentFilter = new UppercaseDocumentFilter(); // TODO: Replace with dependency injection
	
	public ItemsTable(TableModel model) {
		super(model);
		setSurrendersFocusOnKeystroke(true);
	}

	public void switchToBlankItems() {
		setModel(new BlankItemsTableModel());
		
		JTextField productCodeTextField = new JTextField();
		productCodeTextField.addKeyListener(new ProductCodeFieldKeyListener());
		((AbstractDocument)productCodeTextField.getDocument()).setDocumentFilter(uppercaseDocumentFilter);
		getColumnModel().getColumn(0).setCellEditor(new DefaultCellEditor(productCodeTextField));
		
		JTextField unitTextField = new JTextField();
		unitTextField.addKeyListener(new UnitFieldKeyListener());
		((AbstractDocument)unitTextField.getDocument()).setDocumentFilter(uppercaseDocumentFilter);
		getColumnModel().getColumn(2).setCellEditor(new DefaultCellEditor(unitTextField));
		
		changeSelection(0, 0, false, false);
		editCellAt(0, 0);
		getEditorComponent().requestFocusInWindow();
		
		// TODO: Handle also shift+tab
		// TODO: Review tab
		
		getActionMap().put("tab", new ItemsTableTabAction(this));
		getActionMap().put("down", new ItemsTableDownAction(this, getAction(KeyEvent.VK_DOWN)));
		getActionMap().put("showSelectProductDialog", new ShowSelectProductDialogAction(this));
				
		getInputMap(JTable.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke(KeyEvent.VK_TAB, 0), "tab");
		getInputMap(JTable.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0), "down");
		getInputMap(JTable.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke(KeyEvent.VK_F5, 0), "showSelectProductDialog");
	}
	
	private Action getAction(int keyEvent) {
		String actionName = (String)getInputMap(JTable.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).get(KeyStroke.getKeyStroke(keyEvent, 0));
		return getActionMap().get(actionName);
	}

	public void addNewRow() {
		int newRowIndex = getSelectedRow() + 1;
		
		BlankItemsTableModel model = (BlankItemsTableModel)getModel();
		model.addBlankRow();
		
		changeSelection(newRowIndex, 0, false, false);
		editCellAt(newRowIndex, 0);
		getEditorComponent().requestFocusInWindow();
	}
	
	public boolean isQuantityFieldSelected() {
		return getSelectedColumn() == QUANTITY_COLUMN_INDEX;
	}
	
	public boolean isLastRowSelected() {
		return getSelectedRow() + 1 == getModel().getRowCount();
	}

	public boolean isCurrentRowValid() {
		if (isEditing()) {
			getCellEditor().stopCellEditing();
		}
		
		TableModel model = getModel();
		int rowIndex = getSelectedRow();
		String productCode = (String)model.getValueAt(rowIndex, PRODUCT_CODE_COLUMN_INDEX);
		String unit = (String)model.getValueAt(rowIndex, UNIT_COLUMN_INDEX);
		String quantity = (String)model.getValueAt(rowIndex, QUANTITY_COLUMN_INDEX);
		
		return !StringUtils.isEmpty(productCode) 
				&& !StringUtils.isEmpty(unit) 
				&& !StringUtils.isEmpty(quantity);
	}
	
}
