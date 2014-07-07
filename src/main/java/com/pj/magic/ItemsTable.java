package com.pj.magic;

import java.awt.event.KeyEvent;

import javax.swing.DefaultCellEditor;
import javax.swing.JComboBox;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.table.TableModel;
import javax.swing.text.AbstractDocument;

public class ItemsTable extends JTable {
	
	private static final long serialVersionUID = -8416737029470549899L;

	public ItemsTable(TableModel dm) {
		super(dm);
	}

	public void switchToBlankItems() {
		setModel(new BlankItemsTableModel());
		
		JTextField productCodeTextField = new JTextField();
		productCodeTextField.addKeyListener(new ProductCodeFieldKeyListener());
		((AbstractDocument)productCodeTextField.getDocument()).setDocumentFilter(new UppercaseDocumentFilter());
		getColumnModel().getColumn(0).setCellEditor(new DefaultCellEditor(productCodeTextField));
		
		JComboBox<String> comboBox = new JComboBox<>();
		comboBox.addItem("");
		comboBox.addItem("CSE");
		comboBox.addItem("CTN");
		comboBox.addItem("DOZ");
		comboBox.addItem("PCS");
		getColumnModel().getColumn(2).setCellEditor(new DefaultCellEditor(comboBox));
		
		changeSelection(0, 0, false, false);
		editCellAt(0, 0);
		setSurrendersFocusOnKeystroke(true);
		getEditorComponent().requestFocusInWindow();
		
		getInputMap().clear();
		getActionMap().clear();
		getInputMap(JTable.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke(KeyEvent.VK_TAB, 0), "itemsTableTabAction");
//		getInputMap(JTable.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke(KeyEvent.VK_TAB, KeyEvent.VK_SHIFT), "shiftTabInsideTableAction");
		getInputMap(JTable.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0), "itemsTableDownAction");
		getActionMap().put("itemsTableTabAction", new ItemsTableTabAction(this));
//		getActionMap().put("shiftTabInsideTableAction", new ItemsTableTabAction(this));
		getActionMap().put("itemsTableDownAction", new ItemsTableTabAction(this));
	}
	
}
