package com.pj.magic.dialog;

import java.awt.event.KeyEvent;

import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.KeyStroke;

import com.pj.magic.MagicDialog;

public class SelectProductDialog extends MagicDialog {

	private static final long serialVersionUID = -1155384453472953071L;
	private static final String SELECT_PRODUCT_ACTION_NAME = "selectProduct";
	public static final String PRODUCT_CODE_RETURN_VALUE_NAME = "productCode";
	
	public SelectProductDialog() {
		setModal(true);
		setSize(500, 200);
		setLocationRelativeTo(null);
		setTitle("Select Product");
		addContents();
	}

	private void addContents() {
		JTable table = new JTable(new ProductsTableModel());
		table.setRowSelectionInterval(0, 0);
		
		table.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_F9, 0), SELECT_PRODUCT_ACTION_NAME);
		table.getActionMap().put(SELECT_PRODUCT_ACTION_NAME, new SelectProductAction(table, this));
		
		JScrollPane scrollPane = new JScrollPane(table);
		add(scrollPane);	
	}

	public String getSelectedProductCode() {
		return getReturnValue(PRODUCT_CODE_RETURN_VALUE_NAME);
	}
	
}
